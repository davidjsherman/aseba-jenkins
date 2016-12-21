#!groovy

// vars/FindAvailableNodes.groovy
// Find which nodes are available for multi-OS builds

def call(args) {

	candidates = args ?: [ 'debian', 'windows', 'macos' ]

	def labels = [];
	for (n in Jenkins.instance.nodes) {
		for (l in n.getAssignedLabels()) {
			labels << l.getName();
		}
	}
	return labels.intersect(candidates);
}

return this;
