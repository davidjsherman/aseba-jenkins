#!groovy

// vars/CMake.groovy

def call(body) {

	// body.buildDir
	// body.sourceDir
	// body.buildType
	// body.installDir
	// body.getArguments
	// body.getGenerator
	// body.preloadScript
	// body.getCmakeArgs

	//node {
		workDir = pwd()
		try {
			getCmakeArgs = body.getCmakeArgs.join(' ')
		} catch (e) {
			getCmakeArgs = body.getCmakeArgs
		}
		try {
			getArguments = body.getArguments.join(' ')
		} catch (e) {
			getArguments = body.getArguments
		}
		withEnv(["buildDir=${body.buildDir ?: '_build'}",
			"sourceDir=${body.sourceDir ?: workDir}",
			"buildType=${body.buildType ?: 'Debug'}",
			"installDir=${body.installDir ?: '_install'}",
			"getArguments=${body.getArguments ?: ''}",
			"getGenerator=${body.getGenerator ?: 'Unix Makefiles'}",
			"preloadScript=${body.preloadScript ?: ''}",
			"getCmakeArgs=${getCmakeArgs ?: ''}",
			"workDir=${workDir}"]) {

			sh '''echo run CMake with parameters:
				echo "workDir = ${workDir}"
				echo "getArguments = ${getArguments}"
				echo "buildDir = ${buildDir}"
				echo "sourceDir = ${sourceDir}"
				echo "getGenerator = ${getGenerator}"
				echo "preloadScript = ${preloadScript}"
				echo "buildType = ${buildType}"
				echo "getCmakeArgs = ${getCmakeArgs}"
				mkdir -p "$installDir" "$buildDir"
				cd "$buildDir"
				cmake "$sourceDir" -G "$getGenerator" $getArguments \
					-DCMAKE_INSTALL_PREFIX:PATH="$installDir" \
					-DCMAKE_BUILD_TYPE:STRING="$buildType" \
					$getCmakeArgs
				make
				make install
			'''
		}
	//}
}

return this;
