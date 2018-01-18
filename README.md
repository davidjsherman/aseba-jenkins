# Aseba Continuous Integration
Support for continuous integration of Aseba software components in Jenkins 2 and in Docker.

## Global Library

### CMake
If this repository is declared as a [Jenkins global library](https://jenkins.io/doc/book/pipeline/shared-libraries/), it will provide a pipeline step function **CMake** that will configure, make, and install a program. The function accepts the following arguments:
* `label` optional subdirectory to append to `buildDir` and `installDir`
* `buildDir` where to compile (default: workDir + '/build/' + label)
* `sourceDir` where to find the source (default: workDir)
* `installDir` where to install (default: workdir + '/dist/' + label)
* `cleanBuild` should buildDir be emptied first (default: true)
* `buildType` CMake build type (default: 'Debug')
* `getGenerator` which build generator (default: 'Unix Makefiles')
* `makeInvocation` how to build (default: 'make')
* `makeInstallInvocation` how to install (default: 'make install')
* `getCmakeArgs`: additional CMake arguments, list or single string (no default)
* `getArgumentss`: additional CMake arguments, list or single string (no default)
* `preloadScript`: commands to run first (no default)

This function is a stopgap, waiting for the [official CMake plugin](https://wiki.jenkins-ci.org/display/JENKINS/CMake+Plugin) to catch up with Jenkins Pipeline (see [JENKINS-34998 Make CMake plugin compatible with pipeline](https://issues.jenkins-ci.org/browse/JENKINS-34998)).
Its arguments try to match [CMake Build Configuration](https://wiki.jenkins-ci.org/display/JENKINS/CMake+Build+Configuration).
Like the official plugin, this function runs both CMake and a build invocation (make, make install).

The CMake step constructs a Bash script that is then executed, using a **shell** step if run on Unix, or a **powershell** step if run on Windows. The script is written to the console log so that it can be run manually if desired.

### FindAvailableNodes
The utility function **FindAvailableNodes** will filter a list of candidate node labels, by default `['debian','windows','macos']`, and only return those that are available in the current Jenkins instance. This can be used to build multi-OS pipelines where some OSes may not be available at a given time.

## Resources

### Build node configuration
The directory [`resources/buildfarm`](resources/buildfarm) contains [Ansible](https://www.ansible.com) playbooks to install development environments for Linux, macOS, and Windows, with prerequisites for compiling Aseba. It will be necessary to customize the `hosts` inventory to reflect your particular installation.

### Docker images
Jenkins can be run in a Docker container to provide a Debian build environment. The directory [`resources/buildfarm/docker`](resources/buildfarm/docker) contains a Dockerfile that will install Aseba prerequisites for building on the master node, and Jenkins plugins needed to run the Aseba Jenkinsfiles.
