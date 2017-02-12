package edu.utsa.cs.repoanalysis.typeresolver.build;

public class BaseGradlewBuilder extends BaseBuilder {
    public BaseGradlewBuilder(){
	this.type = BuildType.Type_Gradlew;
    }
    public BaseGradlewBuilder(BuildType type){
	this.type = type;
    }

}
