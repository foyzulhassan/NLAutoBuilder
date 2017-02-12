package edu.utsa.cs.util;

import java.util.HashSet;
import java.util.Set;

public class ClassManipulator {
    public static Set<String> getPackage(Set<String> jreClasses) {
	Set<String> packs = new HashSet<String>();
	for (String clazz : jreClasses) {
	    if(clazz.lastIndexOf('.')!=-1){
		packs.add(clazz.substring(0, clazz.lastIndexOf(".")));
	    }
	}
	return packs;
    }

    public static String file2class(String name) {
	String classname = name;

	if (name.startsWith("/")) {
	    classname = classname.substring(1);
	}
	classname = classname.replace('/', '.');
	classname = classname.replace('$', '.');
	return classname.substring(0, classname.lastIndexOf('.'));
    }

    public static boolean isPackage(String item) {
	for (int i = 0; i < item.length(); i++) {
	    char c = item.charAt(i);
	    if (Character.isLetter(c) && Character.isUpperCase(c)) {
		return false;
	    }
	}
	return true;
    }
}
