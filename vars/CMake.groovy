#!groovy

// vars/CMake.groovy
// Provide a pipeline step function that will configure, make, and install a package.
// Inspired by the [official CMake plugin](https://wiki.jenkins-ci.org/display/JENKINS/CMake+Plugin).

def call(body) {
	
	workDir = pwd()
	
	cleanBuild = body.cleanBuild ?: true
	buildDir = body.buildDir ?: workDir + '/build'
	sourceDir = body.sourceDir ?: workDir
	
	envs = (body.envs instanceof ArrayList ? body.envs : [])
	envs << "getCmakeArgs=" + (body.getCmakeArgs instanceof ArrayList ? body.getCmakeArgs.join(' ') : (body.getCmakeArgs ?: ''))
	envs << "getArguments=" + (body.getArguments instanceof ArrayList ? body.getArguments.join(' ') : (body.getArguments ?: ''))
	envs << "buildDir=" + buildDir // (body.buildDir ?: workDir + '/build')
	envs << "sourceDir=" + sourceDir // (body.sourceDir ?: workDir)
	envs << "buildType=" + (body.buildType ?: 'Debug')
	envs << "installDir=" + (body.installDir ?: workdir + '/dist')
	envs << "getGenerator=" + (body.getGenerator ?: 'Unix Makefiles')
	envs << "workDir=" + (workDir)
	
	preloadScript = (body.preloadScript instanceof ArrayList ? body.preloadScript.join(" ") : body.preloadScript) ?: ''
	
	script = new String('''#!/bin/bash -l
	echo -e run CMake with parameters: "\\n"workDir = ${workDir}"\\n"getArguments = ${getArguments}"\\n"buildDir = ${buildDir}"\\n"sourceDir = ${sourceDir}"\\n"getGenerator = ${getGenerator}"\\n"preloadScript = ${preloadScript}"\\n"buildType = ${buildType}"\\n"getCmakeArgs = ${getCmakeArgs}
'''.stripIndent())
	
	if (cleanBuild && buildDir != sourceDir) {
		script += "rm -rf \"$buildDir\"\n"
	}
	
	script += '''
	mkdir -p "$installDir" "$buildDir"
	cd "$buildDir"
	'''.stripIndent()
	
	if (body.preloadScript) {
		script += preloadScript + "\n"
	}
	
	script += '''
	cmake "$sourceDir" -G "$getGenerator" $getArguments \
		-DCMAKE_INSTALL_PREFIX:PATH="$installDir" \
		-DPYTHON_CUSTOM_TARGET:PATH="$installDir/"$(python -c "import sys; print 'lib/python'+str(sys.version_info[0])+'.'+str(sys.version_info[1])+'/dist-packages'") \
		-DCMAKE_FIND_ROOT_PATH:PATH="$installDir" \
		-DCMAKE_BUILD_TYPE:STRING="$buildType" \
		$getCmakeArgs
	make
	make install
	'''.stripIndent()
	
	withEnv(envs) {
		sh script
	}
}

return this;
