package edu.utsa.cs.repoanalysis.typeresolver.build;

import java.util.ArrayList;
import java.util.List;

public class JavacBuilder extends BaseBuilder {
    public JavacBuilder(){
	this.type = BuildType.Type_Javac;
    }
    public JavacBuilder(BuildType type){
   	this.type = type;
       }
    protected List<BuildError> fetchErrors(List<String> resultLines) {
	List<BuildError> errors = new ArrayList<BuildError>();
	List<String> errStrs = new ArrayList<String>();
	for(String line : resultLines){
	    if(line.startsWith("/home") && errStrs.size() > 0){
		errors.add(BuildError.parse(errStrs));
		errStrs.clear();
		errStrs.add(line);
	    }else{
		errStrs.add(line);
	    }
	    if(line.startsWith("tmp-")){
		break;
	    }
	}
	return errors;
    }
}
