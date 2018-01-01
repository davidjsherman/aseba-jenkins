#!groovy

// vars/CMake.groovy
// Provide a pipeline step function that will configure, make, and install a package.
// Inspired by the [official CMake plugin](https://wiki.jenkins-ci.org/display/JENKINS/CMake+Plugin).

def call(body) {
	
	def workDir = "${pwd()}"

	// Standardize directory layout
	def buildDir = (body.buildDir ?: workDir + '/build' + (body.label ? '/' + body.label : ''))
	def sourceDir = (body.sourceDir ? workDir + '/' + body.sourceDir : workDir)
	def installDir = (body.installDir ?: workDir + '/dist' + (body.label ? '/'+body.label : ''))

	// Default values
	def cleanBuild = body.cleanBuild ?: true
	def buildType = (body.buildType ?: 'Debug')
	def getGenerator = (body.getGenerator ?: 'Unix Makefiles')
	def makeInvocation = (body.makeInvocation ?: 'make')
	def makeInstallInvocation = (body.makeInstallInvocation ?: 'make install')

	// Sanitize arguments
	def envs = (body.envs instanceof ArrayList ? body.envs : [])
	def getCmakeArgs = (body.getCmakeArgs instanceof ArrayList ? body.getCmakeArgs.join(' ') : (body.getCmakeArgs ?: ''))
	def getArguments = (body.getArguments instanceof ArrayList ? body.getArguments.join(' ') : (body.getArguments ?: ''))
	def preloadScript = (body.preloadScript instanceof ArrayList ? body.preloadScript.join(' ') : body.preloadScript) ?: ''

	// Bash script to be built
	def script = new String("#!/bin/bash -l")

	// Record arguments in script comments
	// Note that variables are interpolated due to """, single quotes are copied as-is to the script
	script += """
		# CMake.groovy arguments:
		# workDir='${workDir}'
		# buildDir='${buildDir}'
		# installDir='${installDir}'
		# sourceDir='${sourceDir}'
		# getArguments='${getArguments}'
		# getGenerator='${getGenerator}'
		# preloadScript='${preloadScript}'
		# buildType='${buildType}'
		# getCmakeArgs='${getCmakeArgs}'
	""".stripIndent()

	// Clean out build directory unless specified otherwise
	if (cleanBuild && buildDir != sourceDir) {
		script += """
			pwd
			rm -rf '$buildDir'
		""".stripIndent()
	}

	// Initialize directory layout if necessary
	script += """
		mkdir -p '$installDir' '$buildDir'
	""".stripIndent()

	// Optional preload script. Run in work directory
	if (body.preloadScript) {
		script += preloadScript + "\n"
	}

	// Main event: cmake, make, make install. Run in build directory
	script += """
		cd '$buildDir'
		cmake '$sourceDir' -G '$getGenerator' $getArguments \
			-DCMAKE_INSTALL_PREFIX:PATH='$installDir' \
			-DCMAKE_FIND_ROOT_PATH:PATH='$installDir' \
			-DCMAKE_BUILD_TYPE:STRING='$buildType' \
			$getCmakeArgs
		$makeInvocation
		$makeInstallInvocation
	""".stripIndent()

	// Actually run the prepared script
	if (isUnix()) {
		withEnv(envs) {
			// Dump script to log files
			println( "==== CMake.groovy script for ${body.label}: ====\n${script}==============================\n" )
			sh script
		}
	} else {
		withEnv(envs) {
			// Black magic to use Powershell to run a Bash script in the Mingw32 environment
			script = '$cmakeScriptBody = @"' + "\n" + script + '"@' + "\n\n"
			script += '$cmakeScriptInvoke = \'C:/msys32/usr/bin/bash.exe -l -c @"\n\' + $cmakeScriptBody + \'\n"@\'' + "\n"
			script += 'invoke-expression $cmakeScriptInvoke' + "\n"
			// Dump script to log files
			println( "==== CMake.groovy script for ${body.label}: ====\n${script}==============================\n" )
			powershell script
		}
	}
}

return this;
