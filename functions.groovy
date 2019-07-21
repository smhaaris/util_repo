def uploadArtifact() {
	script {
		def server = Artifactory.server 'artifactory'
               def uploadSpec = """{
   	
                "files":[
                    {
			"pattern":"/var/lib/jenkins/workspace/shared_pipeline/target/*.jar",
			"target":"CRUD-Spring-Boot-JPA-MySQL/${artifactId}/${version}.${BUILD_NUMBER}/"
			}]
		}"""
		server.upload(uploadSpec) 	
		}
	}
def downloadArtifact() {
	script {
		def server = Artifactory.server 'artifactory'
			def downloadSpec = """{
				
			"files":[
			{
			"pattern":"CRUD-Spring-Boot-JPA-MySQL/${artifactId}/${version}.${BUILD_NUMBER}/*.jar",
			"target":"/var/lib/jenkins/workspace/shared_pipeline/"
			}
			]
			}"""
			server.download(downloadSpec)

    }
sh "mv  ${artifactId}/${version}.${BUILD_NUMBER}/*.jar ."
}

def sonar(){
	  withSonarQubeEnv('sonarqube') {
		    withMaven(jdk: 'JAVA_HOME', maven: 'MAVEN_HOME') {
			def mvncmd=props.SONAR_SCAN
	  		sh "${mvncmd}"
		}
	   }
}
def qualityGate() {
	timeout(time: 10, unit: 'MINUTES') {
		def qg = waitForQualityGate() 
           	if (qg.status != 'OK') {
             		error "Pipeline aborted due to quality gate failure: ${qg.status}"
           	}
	}
}

def sendEmail() {
	  emailext body: '${DEFAULT_CONTENT}', subject: '${DEFAULT_SUBJECT}', to:  props.MAIL_ID

}

def failureEmail(error) {
	emailext( 
		subject: '${JOB_NAME} - BUILD # ${BUILD_NUMBER} -  FAILURE', 
		body: "${error}",
		to: props.MAIL_ID
		);
	print 'mail sent'
}
return this
