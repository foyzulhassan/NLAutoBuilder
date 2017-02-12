package edu.utsa.cs.repoanalysis.typeresolver.librepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.utsa.cs.util.CommandRunner;
import edu.utsa.cs.util.FileManager;
import edu.utsa.repoanalysis.typeresolver.signatures.ClassSignature;
import edu.utsa.repoanalysis.typeresolver.signatures.FieldSignature;
import edu.utsa.repoanalysis.typeresolver.signatures.MethodSignature;
import edu.utsa.repoanalysis.typeresolver.signatures.TypeSignature;

public class LoadBasic {
    private Set<String> JREClasses;
    private Hashtable<String, List<String>> repoClassMap;
    private String jrePath;

    public LoadBasic(String jrePath, String repoPath) throws IOException {
	this.jrePath = jrePath;
	this.JREClasses = new HashSet<String>();
	this.repoClassMap = new Hashtable<String, List<String>>();
	Set<String> JREJars = new HashSet<String>();
	Set<String> repoJars = new HashSet<String>();
	FileManager.recursiveFetchFile(jrePath, JREJars, ".jar");
	FileManager.recursiveFetchFile(repoPath, repoJars, ".jar");
	for (String jreJar : JREJars) {
	    try {
		this.JREClasses.addAll(FileManager.fetchClassesFromJar(jreJar));
	    } catch (IOException e) {
		System.err.println("Error when extracting " + jreJar);
	    }
	}
	for (String repoJar : repoJars) {
	    try {
		Set<String> classes = FileManager.fetchClassesFromJar(repoJar);
		for (String repoClass : classes) {
		    if (!repoClassMap.keySet().contains(repoClass)) {
			this.repoClassMap.put(repoClass,
				new ArrayList<String>());
		    }
		    this.repoClassMap.get(repoClass).add(repoJar);
		}
	    } catch (IOException e) {
		System.err.println("Error when extracting " + repoJar);
	    }

	}
    }

    public Set<String> getJreClasses() {
	return this.JREClasses;
    }

    public ClassSignature tryResolveStructure(String classPath) {
	String classFilePath = classPath.replace('.', '/') + ".class";
	if (this.JREClasses.contains(classFilePath)) {
	    return resolveStructure(classFilePath);
	} else {
	    return null;
	}
    }

    private ClassSignature resolveStructure(String classFilePath) {
	try {
	    String signaturesRaw = CommandRunner.runCommand(
		    "javap " + this.jrePath + "/" + classFilePath).getStdOut();
	    return parseSignature(classFilePath, signaturesRaw);
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    private ClassSignature parseSignature(String classFilePath,
	    String signaturesRaw) {
	String classPath = classFilePath.substring(0,
		classFilePath.length() - 6).replace('/', '.');
	ClassSignature cs = new ClassSignature(classPath);
	for (String line : signaturesRaw.split("\n")) {
	    if (line.startsWith("public class")) {
		parseClassLine(cs, line);
	    } else if (line.indexOf('(') != -1 && line.indexOf(')') != -1) {
		parseMethodLine(cs, line);
	    } else if (line.endsWith(";")) {
		parseFieldLine(cs, line);
	    }
	}
	return null;
    }

    private void parseFieldLine(ClassSignature cs, String line) {
	StringTokenizer st = new StringTokenizer(line.substring(0,
		line.length() - 1));
	while (st.hasMoreTokens()) {
	    String token = st.nextToken();
	    if (token.equals("public") || token.equals("private")
		    || token.equals("protected")) {
		// todo
	    } else if (token.equals("static")) {
		// todo
	    } else if (token.equals("final")) {
		// todo
	    } else {
		String type = token;
		String fieldName = st.nextToken();
		cs.addField(new FieldSignature(fieldName, TypeSignature
			.parseBinaryType(type)));
	    }
	}
    }

    private void parseMethodLine(ClassSignature cs, String line) {
	StringTokenizer st = new StringTokenizer(line.substring(0,
		line.length() - 1));
	while (st.hasMoreTokens()) {
	    String token = st.nextToken();
	    if (token.equals("public") || token.equals("private")
		    || token.equals("protected")) {
		// todo
	    } else if (token.equals("static")) {
		// todo
	    } else if (token.equals("final")) {
		// todo
	    } else {
		String type = token;
		String signature = st.nextToken();
		String methodName = signature.substring(0,
			signature.indexOf('('));
		String[] paras = signature.substring(
			signature.indexOf('(') + 1, signature.indexOf(')'))
			.split(", ");
		List<TypeSignature> paraTypes = new ArrayList<TypeSignature>();
		for (String para : paras) {
		    paraTypes.add(TypeSignature.parseBinaryType(para));
		}
		cs.addMethod(new MethodSignature(TypeSignature
			.parseBinaryType(type), methodName, paraTypes));
	    }
	}

    }

    private void parseClassLine(ClassSignature cs, String line) {
	StringTokenizer st = new StringTokenizer(line);
	while (st.hasMoreTokens()) {
	    String word = st.nextToken();
	    if (word.equals("extends")) {
		cs.setSuper(st.nextToken());
	    }
	    if (word.equals("implements")) {
		while (st.hasMoreTokens()) {
		    cs.addInterface(st.nextToken());
		}
	    }
	}
    }

    public Hashtable<String, List<String>> getRepoMap() {
	return this.repoClassMap;
    }
}
