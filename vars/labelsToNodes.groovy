#!groovy

// vars/labelsToNodes.groovy
def build(body) {
  def nodesMap = [:]
  for (x in body.labels) {
    def label = x
    def component = body.component
    nodesMap[label] = {
      node(label) {
	//unstash 'source'
	CMake([buildType: 'Debug',
	       sourceDir: '$workDir/' + component,
	       buildDir: '$workDir/_build/' + component + '/' + label,
	       installDir: '$workDir/_install/' + label,
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
