package edu.utsa.cs.repoanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {
    public static String mainDir = "/home/foyzulhassan/Research/Data/autobuilder_testing/";
    public static String workDir = mainDir + "workdir";
    public static String logDir = mainDir + "logs-lib";
    public static String openNLPModelFile = "/models/en-ner-cmd-model5.bin";  
    public static String dictFilePath = "/home/foyzulhassan/Research/Data/Dict_File/"; 
    public static String prunePath = "/home/foyzulhassan/Research/Data/Label_1000/NFold_Final_Label/NFoldV4/prune/";
    public static String classifyOutput = "/home/foyzulhassan/Research/Data/Label_1000/NFold_Final_Label/NFoldV4/prune/";
    public static String srcRepoDir = "/home/foyzulhassan/Research/Data/github_Proj_1000";
    public static String jrePath = "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib";
    public static String libCacheDir = mainDir + "libcache";
    public static String outDir = mainDir + "outputs";
    public static String script =  mainDir + "build-adv.py";
    public static String summaryLog="/home/foyzulhassan/Research/Data/autobuilder_testing/report.log";
    
    public static void reconfig(String filepath) throws IOException{
	BufferedReader in = new BufferedReader(new FileReader(filepath));
	for(String line = in.readLine(); line!=null; line = in.readLine()){
	    if(line.startsWith("#") || line.trim().length() == 0){
		;
	    }else{
		if(line.startsWith("mainDir")){
		    Config.mainDir = line.substring(line.indexOf('=') + 1).trim();
		    Config.logDir = Config.mainDir + "logs-lib";
		    Config.workDir = Config.mainDir + "workdir";
		    Config.outDir = Config.mainDir + "outputs";
		}else if(line.startsWith("srcRepoDir")){
		    Config.srcRepoDir = line.substring(line.indexOf('=') + 1).trim();
		}else if(line.startsWith("jrePath")){
		    Config.jrePath = line.substring(line.indexOf('=') + 1).trim();
		}else if(line.startsWith("libCacheDir")){
		    Config.libCacheDir = line.substring(line.indexOf('=') + 1).trim();
		}else if(line.startsWith("script")){
		    Config.script = line.substring(line.indexOf('=') + 1).trim();
		}
	    }
	}
	in.close();
    }
}
