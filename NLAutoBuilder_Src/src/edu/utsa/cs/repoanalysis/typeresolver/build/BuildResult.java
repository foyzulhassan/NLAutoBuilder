package edu.utsa.cs.repoanalysis.typeresolver.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class BuildResult {
    private List<BuildError> errors;
    private Hashtable<String, List<BuildError>> errorMap = new Hashtable<String, List<BuildError>>();

    public BuildResult(List<BuildError> errors) {
	this.errors = errors;
	
	for(BuildError err:errors){
	    List<BuildError> errs = errorMap.get(err.getClass().getSimpleName());
	    if(errs == null){
		errs = new ArrayList<BuildError>();
		errorMap.put(err.getClass().getSimpleName(), errs);
	    }
	    errs.add(err);
	    
	}
    }
    
    public String getMajorError(){
	int max = 0;
	String majorError = "";
	for(String errType : this.errorMap.keySet()){
	    List<BuildError> errs = this.errorMap.get(errType);
	    if(errs.size() > max){
		max = errs.size();
		majorError = errType;
	    }
	}
	return majorError;
    }
    public String getSummary(){
	String summary = "||";
	List<String> keys = new ArrayList<String>();
	keys.addAll(this.errorMap.keySet());
	Collections.sort(keys);
	for(String key : keys){
	    summary += key + ":" + this.errorMap.get(key).size() + ";";
	}
	return summary;
	
    }
    public List<BuildError> getErrors(){
	return this.errors;
    }

    public boolean success() {
	return this.errors.size() == 0;
    }

}
