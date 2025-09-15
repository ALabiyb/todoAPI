library identifier: 'refactoringsoftcmspipeline@main', retriever: modernSCM([$class: 'GitSCMSource', remote: 'http://192.168.15.85/personal/refactoringsoftcmspipeline.git', credentialsId: 'LASAID'])

pipeline {
    agent {
		label 'trivy_node'
    }


stages {
		stage('Build Pipeline') {
			steps {
				script {
					buildPipeline([
				// Required parameters])
				registry: 'registry.192.168.15.10.nip.io',
				imageName: 'todoapi',
				gitRepo: 'http://192.168.15.85/personal/todoapi.git',
				appGitCredId: 'git-credentials-id',
				registryCredentials: 'registry-credentials-id',
				deploymentGitCredId: 'deployment-git-credentials-id',
				manifestPath: 'manifests',

				// SonarQube parameters
				sonarProjectKey: 'my-app',
				sonarProjectName: 'My App',
				sonarProjectVersion: '1.0',
				sonarQubeServer: 'SonarQube',
				sonarSources: 'src',
				sonarExclusions: '**/test/**,**/tests/**,**/*.spec.js',
				sonarJavaBindings: 'true',
				sonarTests: 'tests',
				failOnQualityGate: true,

				// Security scanning parameters
				failOnSecurityScan: true,
				maxHighVulnerabilities: 5,

				// Email notification parameters
				//emailProjectLead: 'project.lead@softnet.co.tz',
				cicdEmail: 'lsaid@softnet.co.tz',

				// Other optional parameters can be added here
				branchName: 'main',
				servicesToBuild: 'app',
				manifestFile: 'deployment.yaml',
				namespace: 'softcms',
				])
			}
		}
	}
}
}