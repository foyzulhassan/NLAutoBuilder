package edu.utsa.cs.repoanalysis.buildconfig.ant;

import java.util.Hashtable;

public class AntDependencyVisitor extends AntBuildFileVisitor{
    Hashtable<String, AntGlobalTarget> allTargets;
    public AntDependencyVisitor(Hashtable<String, AntGlobalTarget> targets) {
	super();
    }
    
    
}
