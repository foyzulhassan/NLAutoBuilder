package edu.utsa.cs.repoanalysis.typeresolver.build.errors;

import edu.utsa.cs.repoanalysis.typeresolver.build.BuildError;

public class DuplicateClass extends BuildError{

    private String duplicate;

    public DuplicateClass(String fileName, int lineNum, String duplicate) {
	super(fileName, lineNum, "duplicate class");
	this.duplicate = duplicate;
    }
    public String getDuplicateClass(){
	return this.duplicate;
    }
}
