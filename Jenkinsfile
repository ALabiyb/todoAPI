// Load the shared library named 'jenkins-shared-library' from the 'main' branch so it can be used in this pipeline and use its functions
library identifier: 'jenkinsSharedLibrary@main', retriever: modernSCM([$class: 'GitSCMSource', remote: 'https://github.com/ALabiyb/jenkinsSharedLibrary.git', credentialsId: ''])

pipeline {
	// agent {
	// 	label 'trivy_node'
	// }

	agent any

	environment {
		// Register environment variables that can be used throughout the pipeline
		REGISTRY_URL = 'docker.io'
		REGISTRY_CREDENTIALS_ID = 'docker-registry-credentials'

		// Project Configuration
		PROJECT_NAME = 'todoapi' // Name of the project
		IMAGE_NAME = 'todoapi'  // Name of the Docker image
		IMAGE_TAG = "${env.BUILD_NUMBER ?: 'latest'}"  // Tag for the Docker image, using build number or 'latest' if not available
	}

	stages {
		stage ('Send Start Notification') {
			steps {
				script {
					// Call the commonSteps function from the shared library to send a start notification
					def gitInfo = commonSteps(
						branch: env.BRANCH_NAME ?: 'main',
						repoUrl: 'https://github.com/ALabiyb/todoAPI.git',
						credentialsId: 'password'
					)

					echo "Git Info: ${gitInfo}"
					def triggerBy = detectBuildTrigger()
					echo "Build triggered by: ${triggerBy}"

					notify([
						subject: "Pipeline Started: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
						recipients: 'munimdevops1111@gmail.com',
						template: 'start.html',
						data: [
							JOB_NAME: env.JOB_NAME,
							BUILD_NUMBER: env.BUILD_NUMBER,
							BUILD_URL: env.BUILD_URL,
							TRIGGERED_BY: triggerBy,
							BRANCH: env.BRANCH_NAME ?: 'main',
							BUILD_STATUS: 'STARTED',
							GIT_COMMIT: gitInfo?.message ?: "No commit info",
							GIT_AUTHOR: gitInfo?.author ?: "Unknown"
						]
					])

					// Store Git information in environment variables for later stages
					env.GIT_MESSAGE = gitInfo?.message ?: "No commit info"
					env.GIT_AUTHOR = gitInfo?.author ?: "Unknown"
				}
			}
		}
		stage ('Build Application') {
			steps {
				script {
					echo "==== Building Application ===="

					agent {
						label 'trivy_docker'
					}

					def buildResult

					try {
						// Starting logic to build Application
						echo "Starting application building...."

						// Check if docker-compose.yml exists to determine build method
						def composeExists = sh(script: 'test -f docker-compose.yml', returnStatus: true) == 0
						def composeYmlExists = sh(script: 'test -f docker-compose.yaml', returnStatus: true) == 0

						if (composeExists || composeYmlExists) {
							echo "Using Docker Compose build method...."
						} else {
							echo "No docker-compose file found. Building with Dockerfile..."
							buildResult = buildAppOnly(
								projectName: env.JOB_NAME,
								imageName: env.IMAGE_NAME,
								imageTag: env.IMAGE_TAG,
								registryUrl: env.REGISTRY_URL,
								registryCredentialsId: env.REGISTRY_CREDENTIALS_ID,
								buildArgs: [
									'GIT_AUTHOR': env.GIT_AUTHOR,
									'GIT_COMMIT': env.GIT_MESSAGE
								],
								pushToRegistry: true, // Push image after build
								removeAfterPush: true // Remove local image after push
							)
						}

						echo "✅ Build completed successfully"
					} catch (Exception e) {
						echo "❌ Build failed: ${e.getMessage()}"
						error("Stopping pipeline because build failed")
						currentBuild.result = 'FAILURE'
					}
				}
			}
		}
	}

	post {
		success {
			script {
				try {
					notify([
                        subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        recipients: 'munimdevops1111@gmail.com',
                        template: 'success.html',
                        data: [
                            JOB_NAME: env.JOB_NAME,
                            BUILD_NUMBER: env.BUILD_NUMBER,
                            BRANCH: env.BRANCH_NAME ?: "main",
                            BUILD_URL: env.BUILD_URL,
                            TRIGGERED_BY: detectBuildTrigger(),
                            BUILD_STATUS: "SUCCESS",
                            GIT_AUTHOR: env.GIT_AUTHOR,
                            GIT_COMMIT: env.GIT_MESSAGE,
                            CHANGED_FILES: env.CHANGED_FILES,
                            CHANGE_TYPES: env.CHANGE_TYPES,
                            IMAGE_NAME: "${env.REGISTRY_URL}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                        ]
                    ])
                } catch (Exception e) {
					echo "Failed to send success notification: ${e.getMessage()}"
                }
            }
        }

		failure {
			script {
				try {
					notify([
                        subject: "❌ Pipeline Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        recipients: 'munimdevops1111@gmail.com',
                        template: 'failure.html',
                        data: [
                            JOB_NAME: env.JOB_NAME,
                            BUILD_NUMBER: env.BUILD_NUMBER,
                            BRANCH: env.BRANCH_NAME ?: "main",
                            BUILD_URL: env.BUILD_URL,
                            TRIGGERED_BY: detectBuildTrigger(),
                            BUILD_STATUS: "FAILED",
                            GIT_AUTHOR: env.GIT_AUTHOR ?: "Unknown",
                            GIT_COMMIT: env.GIT_MESSAGE ?: "Unknown",
                            CHANGED_FILES: env.CHANGED_FILES ?: "Unknown",
                            CHANGE_TYPES: env.CHANGE_TYPES ?: "Unknown",
                            ERROR_MESSAGE: "Pipeline failed - check build logs for details"
                        ]
                    ])
                } catch (Exception e) {
					echo "Failed to send failure notification: ${e.getMessage()}"
                }
            }
        }
	}
}

/**
 * Detect build trigger source
 */
def detectBuildTrigger() {
	def triggeredBy = "Unknown"
    def causes = currentBuild.getBuildCauses()

    if (causes) {
		def cause = causes[0]
        if (cause.shortDescription.contains("GitLab")) {
			triggeredBy = cause.shortDescription
        } else if (cause.userName) {
			triggeredBy = cause.userName
        } else if (cause.shortDescription.toLowerCase().contains("scm change")) {
			triggeredBy = "SCM Trigger"
        } else {
			triggeredBy = cause.shortDescription
        }
    }

    return triggeredBy
}