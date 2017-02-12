package edu.utsa.cs.repoanalysis.typeresolver.librepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.utsa.cs.repoanalysis.Config;
import edu.utsa.cs.repoanalysis.typeresolver.client.SrcFileAnalyzer;
import edu.utsa.cs.util.ClassManipulator;
import edu.utsa.cs.util.FileManager;
import edu.utsa.cs.util.Logger;
import edu.utsa.repoanalysis.typeresolver.visitors.FileAnalyzeInfo;

public class BaseResolver {
    private String workPath;
    private String projname;
    private Logger logger;

    public BaseResolver(String projPath, Logger logger) throws IOException {
	this.projname = projPath.substring(projPath.lastIndexOf('/') + 1);
	this.workPath = Config.workDir + "/" + projname;
	this.projname = projPath.substring(projPath.lastIndexOf('/') + 1);
	this.logger = logger;
    }

    public void resolve(LoadBasic loader) throws LibResolvingException, IOException {
	Set<String> jreClasses = loader.getJreClasses();
	Set<String> jrePacks = ClassManipulator.getPackage(jreClasses);

	Set<String> javaFiles = new HashSet<String>();
	Set<String> allImports = new HashSet<String>();
	Set<String> allClasses = new HashSet<String>();
	Set<String> jarFiles = new HashSet<String>();

	FileManager.recursiveFetchFile(this.workPath, jarFiles, ".jar");
	for (String jarFile : jarFiles) {
	    try {
		allClasses.addAll(FileManager.fetchClassesFromJar(jarFile));
	    } catch (IOException e) {
		logger.log("Jar Extraction Error", jarFile, Logger.LEVEL_IMPORTANT);
		System.err.println("Unable to extract jar file: " + jarFile);
	    }
	}

	FileManager.recursiveFetchFile(this.workPath, javaFiles, ".java");
	SrcFileAnalyzer sfa = new SrcFileAnalyzer();
	for (String javaFile : javaFiles) {
	    try {
		FileAnalyzeInfo cs = sfa.shallowAnalyze(javaFile);
		List<String> imports = cs.getTypeResolver().getImports();
		// System.out.println(imports);
		allImports.addAll(imports);
		if (cs.getMainClass() != null) {
		    allClasses.add(cs.getMainClass().getQualifiedName());
		}
	    } catch (IOException e) {
		System.err.println("Unable to parse file: " + javaFile);
		logger.log("Java Parse Error", javaFile, Logger.LEVEL_IMPORTANT);
	    }
	}
	logger.log("All Imports", allImports.toString(), Logger.LEVEL_DEBUG);

	Set<String> allPacks = ClassManipulator.getPackage(allClasses);
	List<String> unresolved = new ArrayList<String>();
	for (String importItem : allImports) {
	    if (jreClasses.contains(importItem)
		    || allClasses.contains(importItem)
		    || importItem.endsWith(".R")) {

	    } else if (ClassManipulator.isPackage(importItem)) {
		if (jrePacks.contains(importItem)
			|| allPacks.contains(importItem)) {

		} else {
		    unresolved.add(importItem);
		}
	    } else {
		unresolved.add(importItem);
	    }
	}
	logger.log("Unresolved classes", unresolved.toString(), Logger.LEVEL_VERBOSE);
	LibDownloader downloader = new LibDownloader(loader, this.workPath,
		Config.libCacheDir);
	downloader.resolveClassesFromMavenRepo(unresolved, logger);
    }
}
