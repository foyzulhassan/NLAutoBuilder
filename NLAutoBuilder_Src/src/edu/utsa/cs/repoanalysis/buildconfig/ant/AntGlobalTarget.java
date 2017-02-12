package edu.utsa.cs.repoanalysis.buildconfig.ant;

import java.io.File;

import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Javac;

import edu.utsa.cs.util.IGraphNode;

public class AntGlobalTarget implements IGraphNode{
    private Target target;
    private boolean canExecute;
    private String path;

    public AntGlobalTarget(Target tgt, File file) {
	this.target = tgt;
	this.path = file.getAbsolutePath();
        for(Task t : tgt.getTasks()){
            if(t instanceof Javac){
                this.canExecute = true;
            }
        }
        (new AntBuildFileVisitor()).visitTarget(tgt);
    }

    @Override
    public int getValue() {
	// TODO Auto-generated method stub
	return 0;
    }
    
    public boolean canExecute(){
	return this.canExecute;
    }

    public String getSignature() {
	return this.path + ":" + this.target.getName();
    }
    
}
