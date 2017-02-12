package edu.utsa.cs.filefolder.util;

import java.io.File;

public class FolderManipulator {
    public static void createFolderIfNotExists(String path) {

	File theDir = new File(path);

	// if the directory does not exist, create it
	if (!theDir.exists()) {

	    boolean result = false;

	    try {
		theDir.mkdir();
		result = true;
	    } catch (SecurityException se) {
		// handle it
	    }
	    if (result) {
		System.out.println("");
	    }
	}

    }
    

}
