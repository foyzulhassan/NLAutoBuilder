package edu.utsa.cs.repoanalysis.typeresolver.build.errors;

import edu.utsa.cs.repoanalysis.typeresolver.build.BuildError;

public class PackageDoesNotExist extends BuildError{

    private String packName;

    public PackageDoesNotExist(String fileName, int lineNum, String packName) {
	super(fileName, lineNum, "package does not exist");
	this.packName = packName;
    }
    public String getPackName(){
	return this.packName;
    }

}
