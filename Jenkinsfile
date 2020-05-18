properties([
    parameters ([
        string(name: 'BUILD_NODE', defaultValue: 'omar-build', description: 'The build node to run on'),
        booleanParam(name: 'CLEAN_WORKSPACE', defaultValue: true, description: 'Clean the workspace at the end of the run')
    ]),
    pipelineTriggers([
            [$class: "GitHubPushTrigger"]
    ]),
    [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github
    .com/ossimlabs/lidar-server-converter'],
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '3', daysToKeepStr: '', numToKeepStr: '20')),
    disableConcurrentBuilds()
])

node("${BUILD_NODE}") {

 	stage ('Clone repository') {
		checkout scm
	}

	 stage ("Assemble") {
            sh """
            ./gradlew assemble
            """
            archiveArtifacts "build/libs/*.jar"
        }

	stage ('Build image') {
        sh """
           docker build -t nexus-docker-private-hosted.ossim.io/lidar-converter-server .
        """
    }

	stage('Push Docker Images to Nexus Registry'){
		withDockerRegistry(credentialsId: 'mavenCredentials', url: "https://nexus-docker-private-hosted.ossim.io")  {

			sh """
			   docker push nexus-docker-private-hosted.ossim.io/lidar-converter-server
			"""
		}
	}

    stage("Clean Workspace"){
        if ("${CLEAN_WORKSPACE}" == "true")
           	step([$class: 'WsCleanup'])
    	}
}
