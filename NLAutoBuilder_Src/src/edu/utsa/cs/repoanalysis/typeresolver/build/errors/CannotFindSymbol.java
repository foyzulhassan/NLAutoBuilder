package edu.utsa.cs.repoanalysis.typeresolver.build.errors;

import edu.utsa.cs.repoanalysis.typeresolver.build.BuildError;

public class CannotFindSymbol extends BuildError{
    private String symbol;
    private String location;

    public CannotFindSymbol(String fileName, int lineNum, String symbol,
	    String location) {
	super(fileName, lineNum, "cannot find symbol");
	this.symbol = symbol;
	this.location = location;
    }
    public String getSymbol(){
	return this.symbol;
    }
    public String getLocation(){
	return this.location;
    }
}
