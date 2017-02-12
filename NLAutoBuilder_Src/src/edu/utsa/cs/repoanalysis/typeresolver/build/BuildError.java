package edu.utsa.cs.repoanalysis.typeresolver.build;

import java.util.List;

import edu.utsa.cs.repoanalysis.typeresolver.build.errors.CannotFindSymbol;
import edu.utsa.cs.repoanalysis.typeresolver.build.errors.DuplicateClass;
import edu.utsa.cs.repoanalysis.typeresolver.build.errors.PackageDoesNotExist;

public class BuildError {
    protected String fileName;
    protected int lineNum;
    protected String errorType;
    
    public BuildError(String fileName, int lineNum, String errorType) {
	this.fileName = fileName;
	this.lineNum = lineNum;
	this.errorType = errorType;
    }
    public static BuildError getDefaultError(){
	return new BuildError(".", 0, "dummy");
    }
    public static BuildError parse(List<String> errStrs) {
	String labelLine = errStrs.get(0);
	String[] labels = labelLine.split(":");
	String fileName = labels[0];
	int lineNum = Integer.parseInt(labels[1].trim());
	String errorType = labels[3].trim();
	if(errorType.equals("cannot find symbol")){
	    String symbol = errStrs.get(3);
	    String location = errStrs.size() > 4 ? errStrs.get(4) : "";
	    return new CannotFindSymbol(fileName, lineNum, symbol, location);
	}else if(errorType.equals("duplicate class")){
	    String duplicate = labels[4].trim();
	    return new DuplicateClass(fileName, lineNum, duplicate);
	}else if(errorType.startsWith("package") && errorType.endsWith("does not exist")){
	    String packName = errorType.substring(8, errorType.indexOf(" ", 10));
	    return new PackageDoesNotExist(fileName, lineNum, packName);
	}else{
	    return new BuildError(fileName, lineNum, errorType);
	}
    }  

}
