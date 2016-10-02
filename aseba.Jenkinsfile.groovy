#!groovy

def CMake = fileLoader.fromGit('vars/CMake', 'https://github.com/davidjsherman/aseba-jenkins.git', 'master', null, '')

stage 'Setup'
node('inirobot-deb64') {
  sh 'mkdir -p dashel enki aseba'

  dir('dashel') {
    git branch: 'master', url: 'https://github.com/aseba-community/dashel.git'
  }
  dir('enki') {
    git branch: 'master', url: 'https://github.com/enki-community/enki.git'
  }
  dir('aseba') {
    git branch: 'master', url: 'https://github.com/aseba-community/aseba.git'
    sh 'git submodule update --init --recursive'
  }
  
  stash excludes: '.git', name: 'source'
}

def labels = ['inirobot-deb64', 'inirobot-u64', 'inirobot-win7', 'inirobot-osx']
def dashel_builders = [:]	// map to fire all builds in parallel
def enki_builders = [:]		// map to fire all builds in parallel
def aseba_builders = [:]	// map to fire all builds in parallel

for (x in labels) {
  def label = x			// Need to bind the label variable before the closure

  dashel_builders[label] = {
    node(label) {
      unstash 'source'
      CMake.call([buildType: 'Debug',
		  sourceDir: '$workDir/dashel',
		  buildDir: '$workDir/_build/dashel/' + label,
		  installDir: '$workDir/_install',
		  getCmakeArgs: [
		      '-DBUILD_SHARED_LIBS:BOOL=ON'
		      ]
		 ])
      stash includes: '_install/**', name: 'dashel-'+label
    }
  }
  enki_builders[label] = {
    node(label) {
      unstash 'source'
      CMake.call([buildType: 'Debug',
		  sourceDir: '$workDir/enki',
		  buildDir: '$workDir/_build/enki/' + label,
		  installDir: '$workDir/_install',
		  getCmakeArgs: [ '-DBUILD_SHARED_LIBS:BOOL=ON' ]
		 ])
      stash includes: '_install/**', name: 'enki-'+label
    }
  }
  aseba_builders[label] = {
    node(label) {
      unstash 'source'
      env.dashel_DIR = sh ( script: 'dirname $(find _install -name dashelConfig.cmake | head -1)', returnStdout: true).trim()
      CMake.call([buildType: 'Debug',
		  sourceDir: '$workDir/aseba',
		  buildDir: '$workDir/_build/aseba/' + label,
		  installDir: '$workDir/_install',
		  envs: [ "dashel_DIR=${env.dashel_DIR}" ]
		 ])
      stash includes: '_install/**', name: 'aseba-'+label
    }
  }
}

stage 'Dashel Build'
parallel dashel_builders

stage 'Enki Build'
parallel enki_builders

stage 'Aseba Build'
parallel aseba_builders
