package edu.utsa.cs.repoanalysis.typeresolver.build;

public enum BuildType {
    Type_Maven ("mvn"),
    Type_Ant ("ant"),
    Type_Javac ("javac"),
    Type_Gradle ("gradle"),
    Type_Gradlew ("gradlew"),
    
    Type_His_Maven ("mvn"),
    Type_His_Ant ("ant"),
    Type_His_Javac ("javac"),
    Type_His_Gradle ("gradle"),
    Type_His_Gradlew ("gradlew");

    private String text;
    private BuildType(String text){
	this.text = text;
    }
    public String getText(){
	return this.text;
    }
}
