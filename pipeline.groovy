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
	functions.sonar();
	}
	stage("SonarQube Quality Gate")
	{
	functions.qualityGate();
	}
	stage('BuildProject') 
	{
	sh props.MAVEN_BUILD		
   	}
	stage('UploadArtifactory') {
	functions.uploadArtifact();
	}
	stage('downloadingArtifact')
	{
	functions.downloadArtifact();	
	}
	stage('Build & Push Docker image')
	{
	  sh props.DOCKER_BUILD
	  sh props.DOCKER_TAG		
	  sh props.DOCKER_PUSH
	}	
	}
return this
