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
	println(body.envs.getClass())
	println(body.envs)
	envs << "getCmakeArgs=" + (body.getCmakeArgs instanceof ArrayList ? body.getCmakeArgs.join(' ') : (body.getCmakeArgs ?: ''))
	envs << "getArguments=" + (body.getArguments instanceof ArrayList ? body.getArguments.join(' ') : (body.getArguments ?: ''))
	envs << "buildDir=" + buildDir + (body.label ? '/'+body.label : '')
	envs << "sourceDir=" + sourceDir
	envs << "buildType=" + (body.buildType ?: 'Debug')
	envs << "installDir=" + (body.installDir ?: workDir + '/dist') + (body.label ? '/'+body.label : '')
	envs << "getGenerator=" + (body.getGenerator ?: 'Unix Makefiles')
	envs << "makeInvocation=" + (body.makeInvocation ?: 'make')
	envs << "makeInstallInvocation=" + (body.makeInstallInvocation ?: 'make install')
	envs << "workDir=" + (workDir)
	
	preloadScript = (body.preloadScript instanceof ArrayList ? body.preloadScript.join(" ") : body.preloadScript) ?: ''
	
	script = new String('''#!/bin/bash -l
	echo -e run CMake with parameters: "\\n"workDir = ${workDir}"\\n"getArguments = ${getArguments}"\\n"buildDir = ${buildDir}"\\n"installDir = ${installDir}"\\n"sourceDir = ${sourceDir}"\\n"getGenerator = ${getGenerator}"\\n"preloadScript = ${preloadScript}"\\n"buildType = ${buildType}"\\n"getCmakeArgs = ${getCmakeArgs}
set
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
		-DCMAKE_FIND_ROOT_PATH:PATH="$installDir" \
		-DCMAKE_BUILD_TYPE:STRING="$buildType" \
		$getCmakeArgs
	$makeInvocation
	$makeInstallInvocation
	'''.stripIndent()
	
	withEnv(envs) {
		if (isUnix()) {
			sh script
		} else {
			script = '$__body__ = @"' + "\n" + script + '"@' + "\n"
			script += '$__wrap__ = \'C:/msys32/usr/bin/bash.exe -l -c "\' + $__body__ + \'"\'' + "\n"
			script += 'invoke-expression $__wrap__'
			powershell script
		}
	}
}

return this;
