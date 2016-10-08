#!groovy

// vars/asebaCMakeComponent.groovy

def call(body) {
  def component = body.component
  def label = body.label ?: 'master'

  def newbody = body
  newbody.remove('component')
  newbody.remove('label')

  newbody.sourceDir = '$workDir/' + component
  newbody.buildDir = '$workDir/_build/' + component + '/' + label
  newbody.installDir = '$workDir/_install/' + label
  newbody.getCmakeArgs.push( '-DBUILD_SHARED_LIBS:BOOL=ON' )

  node(label) {
    unstash 'source'
    CMake(newbody)
  }
}

return this;
