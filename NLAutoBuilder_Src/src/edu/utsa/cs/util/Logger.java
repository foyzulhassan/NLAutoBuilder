package edu.utsa.cs.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import org.apache.commons.io.FileUtils;

import edu.utsa.cs.repoanalysis.Config;
import edu.utsa.cs.repoanalysis.typeresolver.build.BuildType;

public class Logger {
    public static int LEVEL_DEBUG = 0;
    public static int LEVEL_VERBOSE = 1;
    public static int LEVEL_INFO = 2;
    public static int LEVEL_IMPORTANT = 3;
    
    private String logpath;
    private String buildLogPath;
    private String javacLogPath;
    private String antLogPath;
    private String mavenLogPath;
    private String gradleLogPath;
   
    private String projName;
    private String sumLogPath;
    private String logProjDir;
    private int logger_level;
    
    private String hisjavacLogPath;
    private String hisantLogPath;
    private String hismavenLogPath;
    private String hisgradleLogPath;
    
    private Hashtable<String, TimeInfo> timeTable = new Hashtable<String, TimeInfo>();

    public Logger(String projname, int level) throws IOException {
	this.logger_level = level;
	this.projName = projname;
	this.logProjDir = Config.logDir + "/" + projname;
	this.logpath = this.logProjDir + "/info.log";
	this.javacLogPath = this.logProjDir + "/build-javac.log";
	this.mavenLogPath = this.logProjDir + "/build-maven.log";
	this.antLogPath = this.logProjDir + "/build-ant.log";
	this.gradleLogPath=this.logProjDir + "/build-gradle.log";
	
	this.hisjavacLogPath = this.logProjDir + "/his-build-javac.log";
	this.hismavenLogPath = this.logProjDir + "/his-build-maven.log";
	this.hisantLogPath = this.logProjDir + "/his-build-ant.log";
	this.hisgradleLogPath=this.logProjDir + "/his-build-gradle.log";
	
	
	this.buildLogPath = this.logProjDir + "/build.log";
	this.sumLogPath = Config.logDir + "/summary.log";
	File logDirFile = new File(this.logProjDir);
	FileUtils.deleteDirectory(logDirFile);
	logDirFile.mkdirs();
    }

    public String getLogPath() {
	return this.logpath;
    }

    public void log(String message, int level) {
	if(level < this.logger_level){
	    return;
	}
	try {
	    PrintWriter pw = new PrintWriter(new FileWriter(this.logpath, true));
	    pw.println(message);
	    pw.close();
	} catch (IOException e) {
	    System.err.println("Error in logging in " + this.logpath
		    + " with info " + message);
	}
    }

    public void log(String header, String message, int level) {
	log("[" + header + "]: " + message, level);
    }
    
    public void logSummary(String message){
	this.logTimeSum();
	try {
	    PrintWriter pw = new PrintWriter(new FileWriter(this.sumLogPath, true));
	    pw.println("[" + this.projName + "]" + message);
	    pw.close();
	} catch (IOException e) {
	    System.err.println("Error in summary logging in " + this.sumLogPath
		    + " with info " + message);
	}
    }

    public String getBuildLogPath(BuildType type) {	
	switch(type){
	case Type_Javac: return this.javacLogPath;
	case Type_Maven: return this.mavenLogPath;
	case Type_Ant: return this.antLogPath;
	case Type_Gradle: return this.gradleLogPath;
	case Type_Gradlew: return this.gradleLogPath;
	
	case Type_His_Javac: return this.hisjavacLogPath;
	case Type_His_Maven: return this.hismavenLogPath;
	case Type_His_Ant: return this.hisantLogPath;
	case Type_His_Gradle: return this.hisgradleLogPath;
	case Type_His_Gradlew: return this.hisgradleLogPath;
	
	default: return this.buildLogPath;
	}
    }
    
    public void logTimeStart(String key){
	TimeInfo info = this.timeTable.get(key);
	if(info == null){
	    info = new TimeInfo();
	    this.timeTable.put(key, info);
	}
	info.setStart(System.currentTimeMillis());
    }
    public void logTimeEnd(String key){
	TimeInfo info = this.timeTable.get(key);
	if(info == null){
	    info = new TimeInfo();
	    this.timeTable.put(key, info);
	}
	long end = System.currentTimeMillis();
	info.addToSum(end - info.getStart());
    }
    
    public void logTimeSum(){
	for(String key : this.timeTable.keySet()){
	    log("[Time Summary][" + key + "]:" + this.timeTable.get(key).getSum(), Logger.LEVEL_INFO);
	}
    }

}
