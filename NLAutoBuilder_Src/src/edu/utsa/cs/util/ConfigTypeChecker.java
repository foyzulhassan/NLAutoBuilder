package edu.utsa.cs.util;

import java.io.File;

public class ConfigTypeChecker {
    
    public static int getBuildConfigFileExits(String dir) {
	int type = 0;

	
	if (type == 0) {
	    String filePathString = dir + "//" + "pom.xml";

	    File f = new File(filePathString);
	    if (f.exists() && !f.isDirectory()) {
		type = 2;
	    }
	}

	if (type == 0) {
	    String filePathString = dir + "//" + "build.gradle";

	    File f = new File(filePathString);
	    if (f.exists() && !f.isDirectory()) {
		type = 3;
	    }
	}
	
	if (type == 0) {

	    String filePathString = dir + "//" + "build.xml";

	    File f = new File(filePathString);
	    if (f.exists() && !f.isDirectory()) {
		type = 1;
	    }
	}

	return type;
    }
    
    public static boolean isGradleBuildGradlewExists(String dir) {
	boolean ret = false;

	String filePathString = dir + "//" + "gradlew";

	File f = new File(filePathString);
	if (f.exists() && !f.isDirectory()) {
	    ret = true;
	}
	return ret;
    }

}
