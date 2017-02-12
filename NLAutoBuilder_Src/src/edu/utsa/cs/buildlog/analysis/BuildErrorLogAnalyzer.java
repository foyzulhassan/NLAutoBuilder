package edu.utsa.cs.buildlog.analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BuildErrorLogAnalyzer {
    
    private String buildLogPath=null;   
    private List<String> resultLines;
    private Pattern compileError = Pattern.compile("\\D+(.java:)+(\\d+:)+( error:)");
    
    
    public BuildErrorLogAnalyzer(String buildlogpath) throws IOException
    {
	this.buildLogPath = buildlogpath;
	
	resultLines=new ArrayList<String>();
	
	BufferedReader in=null;
	
	in = new BufferedReader(new FileReader(this.buildLogPath));
	String line;
	
	while ((line= in.readLine()) != null) {
	    resultLines.add(line);
	}
	
	in.close();
	
    }
    
    
    public boolean isBuuildLogContainsCompileError()
    {
	boolean result = false;

	for (String line : resultLines) {

	    Matcher m = compileError.matcher(line);

	    while (m.find()) {
		result = true;
		break;
	    }

	}

	return result;
    }
    
    

    

}
