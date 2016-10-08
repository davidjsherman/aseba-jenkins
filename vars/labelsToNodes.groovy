#!groovy

// vars/labelsToNodes.groovy
def doit(body) {
  def nodesMap = [:]
  // def component = body.component
  // def labels = body.labels
  for (x in labels) {
    // def label = x
    // nodesMap[label] = {
    //   node(label) {
    // 	//unstash 'source'
    // 	CMake([buildType: 'Debug',
    // 	       sourceDir: '$workDir/', // + component,
    // 	       buildDir: '$workDir/_build/', // + component + '/' + label,
    // 	       installDir: '$workDir/_install/', // + label,
    // 	       getCmakeArgs: [ '-DBUILD_SHARED_LIBS:BOOL=ON' ]
    // 	      ])
    // 	script {
    // 	  env.dashel_DIR = sh ( script: 'dirname $(find _install -name dashelConfig.cmake | head -1)', returnStdout: true).trim()
    // 	}
    //   }
    // }
  }
  println(nodesMap);
  return nodesMap;
}
