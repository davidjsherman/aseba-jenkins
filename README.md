# Aseba Jenkins
Support for building Aseba components in Jenkins 2.

## Global Library
If this repository is declared as a [Jenkins global library](https://github.com/jenkinsci/workflow-cps-global-lib-plugin/blob/master/README.md), it will provide a pipeline step function **CMake** that will configure, make, and install a package. The function accepts the following arguments:
* `sourceDir=" (default: workDir)
* `getGenerator` (default: 'Unix Makefiles')
* `buildType` (default: 'Debug')
* `buildDir` (default: workDir + '/build')
* `installDir` (default: workdir + '/dist')
* `getCmakeArgs` — additional CMake arguments, list or single string (no default)

## Resources

The directory `resources/buildfarm` contains Ansible playbooks to install development environments for Linux, macOS, and Windows, with prerequisites for compiling Aseba. It will be necessary to customize the `hosts` inventory to reflect your particular installation.

Jenkins can be run in a Docker container to provide a Debian build environment. The directory `resources/buildfarm/docker` contains a Dockerfile that will install Aseba prerequisites for building on the master node, and Jenkins plugins needed to run the Aseba Jenkinsfiles.