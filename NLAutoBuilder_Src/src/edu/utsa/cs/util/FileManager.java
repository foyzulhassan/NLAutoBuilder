package edu.utsa.cs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileManager {
    public static Object loadObject(String path) throws IOException,
	    ClassNotFoundException {
	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
	Object obj = ois.readObject();
	ois.close();
	return obj;
    }

    public static void recursiveDeleteDir(String path) {
	File pathFile = new File(path);
	if (!pathFile.exists()) {
	    return;
	}
	if (pathFile.isDirectory()) {
	    if (pathFile.list().length == 0) {
		pathFile.delete();
	    } else {
		for (String subPath : pathFile.list()) {
		    recursiveDeleteDir(path + "/" + subPath);
		}
	    }
	    pathFile.delete();
	} else {
	    pathFile.delete();
	}
    }

    public static void recursiveFetchFile(String path, Set<String> files,
	    String postfix) throws IOException {
	File pathFile = new File(path);
	if(!pathFile.exists()){
	    throw new IOException(path + " does not exist!");
	}
	if (!pathFile.isDirectory()) {
	    if (path.endsWith(postfix)) {
		files.add(path);
	    }
	} else {
	    for (String subfile : pathFile.list()) {
		recursiveFetchFile(path + '/' + subfile, files, postfix);
	    }
	}
    }

    public static Set<String> fetchClassesFromJar(String jarPath)
	    throws IOException {
	Set<String> allClasses = new HashSet<String>();
	JarFile jf = new JarFile(jarPath);
	Enumeration<JarEntry> enu = jf.entries();
	while (enu.hasMoreElements()) {
	    JarEntry entry = (JarEntry) enu.nextElement();
	    if (entry.getName().endsWith(".class")) {
		String fullName = ClassManipulator.file2class(entry.getName());
		allClasses.add(fullName);
	    }
	}
	jf.close();
	return allClasses;
    }
}
