# Aseba Jenkins
Support for building Aseba components in Jenkins 2.

## Global Library
If this repository is declared as a [Jenkins global library](https://github.com/jenkinsci/workflow-cps-global-lib-plugin/blob/master/README.md), it will provide a pipeline step function **CMake** that will configure, make, and install a program. The function accepts the following arguments:
* `sourceDir=` (default: workDir)
* `getGenerator` (default: 'Unix Makefiles')
* `buildType` (default: 'Debug')
* `buildDir` (default: workDir + '/build')
* `installDir` (default: workdir + '/dist')
* `makeInvocation` (default: 'make')
* `makeInstallInvocation` (default: 'make install')
* `getCmakeArgs`: additional CMake arguments, list or single string (no default)
* `label=` optional subdirectory to append to `buildDir` and `installDir`

This function is a stopgap, waiting for the [official CMake plugin](https://wiki.jenkins-ci.org/display/JENKINS/CMake+Plugin) to catch up with Jenkins Pipeline (see [JENKINS-34998 Make CMake plugin compatible with pipeline](https://issues.jenkins-ci.org/browse/JENKINS-34998)).
Its arguments try to match [CMake Build Configuration](https://wiki.jenkins-ci.org/display/JENKINS/CMake+Build+Configuration).
Like the official plugin, this function runs both CMake and a build invocation (make, make install).

This global library also provides a utility function **FindAvailableNodes** that will filter a list of candidate node labels, by default `['debian','windows','macos']`, and only return those that are available in the current Jenkins instance.

## Resources

The directory [`resources/buildfarm`](resources/buildfarm) contains Ansible playbooks to install development environments for Linux, macOS, and Windows, with prerequisites for compiling Aseba. It will be necessary to customize the `hosts` inventory to reflect your particular installation.

Jenkins can be run in a Docker container to provide a Debian build environment. The directory [`resources/buildfarm/docker`](resources/buildfarm/docker) contains a Dockerfile that will install Aseba prerequisites for building on the master node, and Jenkins plugins needed to run the Aseba Jenkinsfiles.
