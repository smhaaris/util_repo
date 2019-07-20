def function(props) {
	stage('CheckoutProject') {
		app_url =props.APP_REPO		
		git "${app_url}"
		pom = readMavenPom file: props.POM_FILE
		artifactId=pom.artifactId
		echo "${artifactId}"
		version=pom.version
		
	}
	stage('SonarAnalysis')
	{
	commonUtility.sonar();
	}
	stage("SonarQube Quality Gate")
	{
	commonUtility.qualityGate();
	}
	stage('BuildProject') 
	{
	sh props.MAVEN_BUILD		
   	}
	stage('UploadArtifactory') {
	commonUtility.uploadArtifact();
	}
	stage('downloadingArtifact')
	{
	commonUtility.downloadArtifact();	
	}
	stage('Build & Push Docker image')
	{
	  sh props.DOCKER_BUILD
	  sh props.DOCKER_TAG		
	  sh props.DOCKER_PUSH
	}	
	stage('Dev deploy') {
    	 echo 'Docker Deploy'
         sh props.DOCKER_CMD
  	 sh props.DOCKER_RUN
	}
	stage('Test deploy') {
	input "Deploy to Test? "
	echo 'Docker-compose Deploy'
         sh props.ANSIBLE_CMD
  	 sh props.ANSIBLE_RUN
	}	
	stage('Prod Deploy') {
        input "Deploy to Production? "
    	echo 'Deploy to kubernetes'
     	sh props.KUBERNETES_APPLY
     	sh props.KUBERNETES_GET_ALL
	}
	
	
	stage('Email Notification')
	{
		commonUtility.sendEmail();
	}
	}
return this
