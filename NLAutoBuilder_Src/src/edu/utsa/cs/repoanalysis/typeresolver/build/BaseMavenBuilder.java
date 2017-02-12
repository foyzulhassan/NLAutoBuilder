package edu.utsa.cs.repoanalysis.typeresolver.build;

public class BaseMavenBuilder extends BaseBuilder{
    public BaseMavenBuilder(){
	this.type = BuildType.Type_Maven;
    }
    public BaseMavenBuilder(BuildType type){
	this.type = type;
    }
}
