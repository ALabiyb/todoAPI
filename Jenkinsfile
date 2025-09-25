// Load the shared library named 'jenkins-shared-library' from the 'main' branch so it can be used in this pipeline and use its functions
library identifier: 'jenkinsSharedLibrary@main', retriever: modernSCM([$class: 'GitSCMSource', remote: 'https://github.com/ALabiyb/jenkinsSharedLibrary.git', credentialsId: ''])

pipeline {
	agent {
		label 'trivy_docker'
	}

	environment {
		// Register environment variables that can be used throughout the pipeline
		REGISTRY_TYPE = 'dockerhub' // Type of Docker registry (e.g., dockerhub, ecr, gcr or private registry)
		REGISTRY_URL = 'docker.io'
		PRIVATE_REGISTRY_URL = '' // URL of private registry if using one
		REGISTRY_CREDENTIALS_ID = 'docker-registry-credentials'

		// Project Configuration
		PROJECT_NAME = 'todoapi' // Name of the project
		IMAGE_NAME = 'munimdevops/apik8s'  // Name of the Docker image
		IMAGE_TAG = "${env.BUILD_NUMBER ?: 'latest'}"  // Tag for the Docker image, using build number or 'latest' if not available
		BRANCH_NAME = 'apik8s'
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
						templateName: 'start.html',
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
							def registryConfig = getRegistryConfig(env.REGISTRY_TYPE)
							buildResult = buildAppOnly(
								projectName: env.JOB_NAME,
								imageName: env.IMAGE_NAME,
								imageTag: env.IMAGE_TAG,
								registryUrl: registryConfig.url,
								registryCredentialsId: env.REGISTRY_CREDENTIALS_ID,
								dockerfilePath: '',
								buildArgs: [
									'GIT_AUTHOR': env.GIT_AUTHOR,
									'GIT_COMMIT': env.GIT_MESSAGE
								],
								pushToRegistry: false, // Push image after build
								removeAfterPush: false // Remove local image after push
							)

							env.BUILT_IMAGE_NAME = buildResult.imageName

							if (!buildResult.success){
								echo "❌ Build failed: ${buildResult.error}"
								error("Stopping pipeline because build failed")
							}

							// Store success result in environment
							env.BUILD_RESULT_SUCCESS = 'true'
							env.BUILD_RESULT_BUILD_SUCCESS = buildResult.buildSuccess?.toString() ?: 'false'
							env.BUILD_RESULT_PUSH_SUCCESS = buildResult.pushSuccess?.toString() ?: 'false'
							env.BUILD_RESULT_ERROR_TYPE = buildResult.errorType ?: 'NONE'
							env.BUILD_RESULT_ERROR_MESSAGE = buildResult.errorMessage ?: ''
							env.BUILD_RESULT_MESSAGE = buildResult.message ?: 'Build completed successfully'
						}

						echo "✅ Build completed successfully"
					} catch (Exception e) {
						echo "❌ Build failed: ${e.getMessage()}"

						// Store failure result in environment
						env.BUILD_RESULT_SUCCESS = 'false'
						env.BUILD_RESULT_BUILD_SUCCESS = 'false'
						env.BUILD_RESULT_PUSH_SUCCESS = 'false'
						env.BUILD_RESULT_ERROR_TYPE = 'BUILD_ERROR'
						env.BUILD_RESULT_ERROR_MESSAGE = e.getMessage()
						env.BUILD_RESULT_MESSAGE = "Build stage failed: ${e.getMessage()}"
						error("Stopping pipeline because build failed")
						currentBuild.result = 'FAILURE'
					}
				}
			}
		}

		stage ('Image Trivy Scan') {
			when {
				expression { return env.BUILD_RESULT_SUCCESS == 'true'}
			}
			steps {
				script {
					echo "==== Starting Security Scan ===="

					try {
						if (!env.BUILT_IMAGE_NAME) {
							error("No built image found for scanning")
						}

						def scanResult = trivyScan(
							imageName: env.BUILT_IMAGE_NAME,
							severity: 'CRITICAL,HIGH,MEDIUM',
							format: 'table',
							outputFile: "trivy-report-${env.BUILD_NUMBER}.txt",
							failOnVuln: true
						)

						if (scanResult.vulnerabilitiesFound) {
							echo "❌ Vulnerabilities found:\n${scanResult.reportFile}"

							// Send scan result via email before stopping
							notify([
								subject: "❌ Vulnerabilities Found: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
								recipients: 'munimdevops1111@gmail.com',
								templateName: 'trivy.html',
								data: [
									JOB_NAME: env.JOB_NAME,
									BUILD_NUMBER: env.BUILD_NUMBER,
									BUILD_URL: env.BUILD_URL,
									BUILD_STATUS: "VULNERABILITIES_FOUND",
									IMAGE_NAME: env.BUILT_IMAGE_NAME,
									TRIVY_REPORT: scanResult.reportFile
								]
							])

							// error("Stopping pipeline due to vulnerabilities found")
						} else {
							echo "✅ No vulnerabilities found"
						}
					} catch (Exception e) {
						echo "❌ Security scan failed: ${e.getMessage()}"

						// Store failure result in environment
						env.BUILD_RESULT_SUCCESS = 'false'
						env.BUILD_RESULT_BUILD_SUCCESS = env.BUILD_RESULT_BUILD_SUCCESS ?: 'false'
						env.BUILD_RESULT_PUSH_SUCCESS = env.BUILD_RESULT_PUSH_SUCCESS ?: 'false'
						env.BUILD_RESULT_ERROR_TYPE = 'SECURITY_SCAN_ERROR'
						env.BUILD_RESULT_ERROR_MESSAGE = e.getMessage()
						env.BUILD_RESULT_MESSAGE = "Security scan stage failed: ${e.getMessage()}"
						error("Stopping pipeline because security scan failed")
						currentBuild.result = 'FAILURE'
					}
				}
			}
		}

		stage('Push to Registry') {
			when {
				expression { return env.BUILD_RESULT_SUCCESS == 'true'}
			}
			steps {
				script {
					pushToRegistry(
						imageName: env.IMAGE_NAME,
						imageTag: env.IMAGE_TAG,
						registryType: env.REGISTRY_TYPE,
						// registryUrl: env.REGISTRY_URL,
						privateRegistryUrl: env.PRIVATE_REGISTRY_URL,
						credentialsId: env.REGISTRY_CREDENTIALS_ID
					)
				}
			}
		}
	}

		post {
			success {
				script {
					try {
						def finalImageName = getFinalImageName(env.REGISTRY_TYPE, env.IMAGE_NAME, env.IMAGE_TAG)
						notify([
							subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
							recipients: 'munimdevops1111@gmail.com',
							templateName: 'success.html',
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
								// IMAGE_NAME: "${env.REGISTRY_URL}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
								IMAGE_NAME: finalImageName

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
                        templateName: 'failure.html',
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
							BUILD_SUCCESS: env.BUILD_RESULT_BUILD_SUCCESS ?: 'false',
							PUSH_SUCCESS: env.BUILD_RESULT_PUSH_SUCCESS ?: 'false',
							ERROR_TYPE: env.BUILD_RESULT_ERROR_TYPE ?: 'UNKNOWN_ERROR',
							ERROR_MESSAGE: env.BUILD_RESULT_ERROR_MESSAGE ?: 'Pipeline failed - check build logs for details',
							DETAILED_MESSAGE: env.BUILD_RESULT_MESSAGE ?: 'Pipeline failed - check build logs for details'
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


/**
 * Get registry configuration based on type
 */
def getRegistryConfig(registryType) {
	switch(registryType.toLowerCase()) {
		case 'dockerhub':
			return [url: 'docker.io', type: 'dockerhub']
		case 'private':
			return [url: env.PRIVATE_REGISTRY_URL, type: 'private']
		default:
			return [url: 'docker.io', type: 'dockerhub']
	}
}


/**
 * Get final image name based on registry type
 */
def getFinalImageName(registryType, imageName, imageTag) {
	switch(registryType.toLowerCase()) {
		case 'dockerhub':
			return "docker.io/${imageName}:${imageTag}"
		case 'private':
			return "${env.PRIVATE_REGISTRY_URL}/${imageName}:${imageTag}"
		default:
			return "${imageName}:${imageTag}"
	}
}