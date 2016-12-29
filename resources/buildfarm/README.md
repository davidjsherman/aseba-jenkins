# Build Farm
These tools install Jenkins environments for building Aseba. The *Ansible playbooks* are intended for adding an Aseba project to an existing Jenkins platform and configuring build agents for different operating systems. The *Docker image* is intended for making a single node development environment, using Debian.

## Ansible Playbook

This Playbook will install development environments for Ubuntu, macOS, and Windows, with prerequisites for compiling Aseba. The `hosts` file contains an inventory of the machines on which the toolchains should be installed.

### Invocation
The playbook can be invoked using
```
ansible-playbook -i hosts -s site.yml
```
Alternatively, any OS-specific playbook may be invoked separately.

### Preparing nodes
Ubuntu and macOS nodes must allow *ssh* access and the user must be authorized to use *sudo*. macOS nodes must already have Xcode Command Line Tools installed.

Windows nodes must have Powershell >= 3.0 and must allow *Powershell Remoting*. The appropriate user and connection parameters must be set in `group_vars/ci-windows.yml`.

## Docker image
Jenkins provides an [official Docker image](https://hub.docker.com/_/jenkins/) that runs a continuous integration and delivery server. The Dockerfile in `docker/` customizes this image to:
* Add packages dependencies needed to compile Aseba.
* Add recommended Jenkins plugins including those needed to run Aseba Jenkinsfiles.

### Building and running the image
Build the image in the usual way:
```
docker build docker -t aseba/jenkins
```
It is probably most useful to run the image with a persistent volume for `/var/jenkins_home`. To store the volume in the directory `jenkins`, run using:
```
docker run -d -p 49001:8080 -v $PWD/jenkins:/var/jenkins_home:z -t aseba/jenkins
```
The log output will display the admin password that is generated. The server will be available at http://DOCKERHOST:49001. You can find the DOCKERHOST IP using `docker-machine ip` or `boot2docker ip`, depending on your Docker installation.

## Docker image built using ansible-container
As an alternative to the above, a minimal Docker image based on `ubuntu.yml` can be built using [ansible-container](http://docs.ansible.com/ansible-container/index.html). The specification is in the `ansible` directory. Build the image using:
```
ansible-container build
```
The image can be used by Jenkins Pipeline to spin up containers on demand. To run the container by hand, detached and listening for `ssh` connections on port 22022, use:
```
docker run -p22022:22 -d buildfarm-ci-ubuntu
```

## Creating an Aseba project in Jenkins
1. Use Manage Jenkins to add a Pipeline Global Library. Check ‘load implicitly’. Choose ‘Legacy SCM’ with ‘git’, and specify the master branch of https://github.com/davidjsherman/aseba-jenkins.git.
2. Use Credentials to add a GitHub API key. Choose *Username with password*, using the generated API key as the password.
3. Create a Multibranch job for the desired Aseba component. Under Add Source choose GitHub and specify the appropriate repository. Provide the credentials added in the preceding step.

Jenkins will scan the repository and create a job for every branch (that contains a Jenkinsfile).

It will also do so for PRs, attempting to build the merge of the PR with the origin branch. Your credentials will be used to report success or failure to GitHub, and add badges to the pull request.