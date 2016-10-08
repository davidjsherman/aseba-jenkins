#!groovy

// vars/labelsToNodes.groovy
def build(body) {
  def nodesMap = [:]
  for (label in labels) {
    nodesMap[label] = {
      node(label) {
	unstash 'source'
	CMake([buildType: 'Debug',
	       sourceDir: '$workDir/'+body.component,
	       buildDir: '$workDir/_build/'+body.component+'/'+body.label,
	       installDir: '$workDir/_install/'+body.label,
	       getCmakeArgs: [ '-DBUILD_SHARED_LIBS:BOOL=ON' ]
	      ])
	script {
	  env.dashel_DIR = sh ( script: 'dirname $(find _install -name dashelConfig.cmake | head -1)', returnStdout: true).trim()
	}
      }
    }
  }
  println(nodesMap);
  return nodesMap;
}
