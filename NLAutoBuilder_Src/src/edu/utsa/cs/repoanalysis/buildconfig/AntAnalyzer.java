package edu.utsa.cs.repoanalysis.buildconfig;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import edu.utsa.cs.repoanalysis.buildconfig.ant.AntBuildFile;
import edu.utsa.cs.repoanalysis.buildconfig.ant.AntGlobalTarget;
import edu.utsa.cs.repoanalysis.buildconfig.ant.AntPropertyMapper;
import edu.utsa.cs.util.FileManager;
import edu.utsa.cs.util.GraphUtil;

public class AntAnalyzer {
    private String projPath;
    private Set<AntBuildFile> antFiles;
    private Hashtable<String, AntGlobalTarget> globalTargets;
    private Hashtable<AntGlobalTarget, Set<AntGlobalTarget>> antDependencyGraph;
    private AntPropertyMapper mapper;
    public AntAnalyzer(String projPath) throws IOException{
	this.projPath = projPath;
	this.antFiles = extractAllAntPaths();
	extractAllTargets();
	extractProperties();
    }
    
    public List<AntGlobalTarget> analyze(){
	return GraphUtil.getOrderedEntries(this.antDependencyGraph);
    }    

    private void extractAllTargets() {
	this.globalTargets = new Hashtable<String, AntGlobalTarget>();
	for(AntBuildFile abf : this.antFiles){
	    this.globalTargets.putAll(abf.getGlobalTargets());
	}
    }

    private void extractProperties() {
	this.mapper = new AntPropertyMapper();
	for(AntBuildFile abf : this.antFiles){
	    this.mapper.incorporate(abf.getPropertyMap());
	}	
    }

    public String getProjPath(){
	return this.projPath;
    }
    private Set<AntBuildFile> extractAllAntPaths() throws IOException{
	Set<String> candidatePaths = new HashSet<String>();
	Set<AntBuildFile> buildFiles = new HashSet<AntBuildFile>();
	FileManager.recursiveFetchFile(this.projPath, candidatePaths, ".xml");
	for(String path : candidatePaths){
	    File f = new File(path);
	    AntBuildFile buildFile = AntBuildFile.loadAntBuildFile(f);
	    if(buildFile != null){
		buildFiles.add(buildFile);
	    }
	}
	return buildFiles;
    }

    public Set<AntBuildFile> getAntFiles() {
	return antFiles;
    }

    public void setAntFiles(Set<AntBuildFile> antFiles) {
	this.antFiles = antFiles;
    }
}
