package edu.utsa.cs.buildlog.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.utsa.cs.repoanalysis.typeresolver.build.BuildError;
import edu.utsa.cs.repoanalysis.typeresolver.build.BuildResult;

public class BuildLogAnalyzer {    
    
    private String buildLogPath;
    
      
    public BuildLogAnalyzer(String buildlogpath)
    {
	this.buildLogPath=buildlogpath;	
    }
    
    
    public static boolean isBuildSuccessful(String buildlogpath) throws IOException
    {
	boolean status=false;
	
	if(!(new File(buildlogpath)).exists()){
	   status=false;
	   return status;
	}
	
        BufferedReader in=null;;
	try {
	    in = new BufferedReader(new FileReader(buildlogpath));
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}        
        for(String line = in.readLine(); line!=null; line = in.readLine()){
            if(line.indexOf("BUILD SUCCESS")!=-1){
        	status=true;        	
	    }
        }
        in.close();  
      
	return status;
    }   

}
