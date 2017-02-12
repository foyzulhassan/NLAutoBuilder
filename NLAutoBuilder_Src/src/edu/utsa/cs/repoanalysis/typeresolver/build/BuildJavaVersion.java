package edu.utsa.cs.repoanalysis.typeresolver.build;

public enum BuildJavaVersion {
    JAVA_18 ("1_8"),
    JAVA_17 ("1_7"),
    JAVA_00 ("0_0");

    private String text;
    private BuildJavaVersion(String text){
	this.text = text;
    }
    public String getText(){
	return this.text;
    }
}
