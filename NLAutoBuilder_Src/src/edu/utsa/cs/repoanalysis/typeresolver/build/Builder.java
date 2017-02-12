package edu.utsa.cs.repoanalysis.typeresolver.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.utsa.cs.util.Logger;

public abstract class Builder {
    public static Builder createBuilder(BuildType type){
	switch(type){
	case Type_Ant: return new BaseAntBuilder();
	case Type_Maven: return new BaseMavenBuilder();
	case Type_Gradle: return new BaseGradleBuilder();
	case Type_Gradlew: return new BaseGradlewBuilder();
	case Type_Javac: return new JavacBuilder();
	case Type_His_Ant: return new BaseAntBuilder(type);
	case Type_His_Maven: return new BaseMavenBuilder(type);
	case Type_His_Gradle: return new BaseGradleBuilder(type);
	case Type_His_Gradlew: return new BaseGradlewBuilder(type);
	case Type_His_Javac: return new JavacBuilder(type);
	default: return new BaseBuilder();
	}
    }
    
    protected BuildType type;
    public abstract BuildResult build(String proj, Logger logger, String javasersion, String hisbuild) throws IOException, InterruptedException;
    protected BuildResult checkBuild(String buildLogPath, Logger logger) throws IOException {
	if(!(new File(buildLogPath)).exists()){
	    List<BuildError> dummies = new ArrayList<BuildError>();
	    dummies.add(BuildError.getDefaultError());
	    return new BuildResult(dummies);
	}
	
	logger.logTimeStart("ResolveError");
        BufferedReader in = new BufferedReader(new FileReader(buildLogPath));
        List<String> resultLines = new ArrayList<String>();
        for(String line = in.readLine(); line!=null; line = in.readLine()){
            resultLines.add(line);
        }
        in.close();
        
        List<BuildError> errors = fetchErrors(resultLines);
	logger.logTimeEnd("ResolveError");
        return new BuildResult(errors);
    }
    protected List<BuildError> fetchErrors(List<String> resultLines){
	List<BuildError> errors = new ArrayList<BuildError>();
	for(String line : resultLines){
	    if(line.indexOf("BUILD SUCCESS")!=-1){
		return errors;
	    }
	}
	errors.add(BuildError.getDefaultError());
	return errors;
    }


}
