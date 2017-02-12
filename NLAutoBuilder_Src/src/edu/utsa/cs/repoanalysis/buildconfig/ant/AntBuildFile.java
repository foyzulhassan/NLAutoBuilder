package edu.utsa.cs.repoanalysis.buildconfig.ant;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;

public class AntBuildFile{
    private Project antProj;
    private AntPropertyMapper mapper;
    private List<AntGlobalTarget> targets;
    public AntBuildFile(File file, Project antProj){
        this.antProj = antProj;
        for(Target tgt : this.antProj.getTargets().values()){
            this.targets.add(new AntGlobalTarget(tgt, file));
        }
        try {
            AntPropertyVisitor visitor = new AntPropertyVisitor(this);
            visitor.visitAntProject(this.antProj);
            this.mapper = visitor.getMapper();
        } catch (AntTargetLoopException e) {
            e.printStackTrace();
        }
        
    }
    public Project getAntProject(){
        return this.antProj;
    }
    public static AntBuildFile loadAntBuildFile(File file){
        try{
            Project proj = new Project();
            ProjectHelper.configureProject(proj, file); 
            return new AntBuildFile(file, proj);
        }catch(Exception e){
            return null;
        }
    }
    public static void main(String args[]){
        AntBuildFile abf = AntBuildFile.loadAntBuildFile(new File("C:/putty/jstock/build.xml"));
        Project antproj = abf.antProj;
        for(Target tgt : antproj.getTargets().values()){
            if(tgt.getName().equals("-init-macrodef-javac")){
            }
            if(tgt.getName().equals("")){
                
            }
        }
        System.out.println();
    }
    public String getProjName(){
        return this.antProj.getName();
    }
    public Target getDefaultTarget(){
        return this.antProj.getTargets().get(this.antProj.getDefaultTarget());
    }
    public File getBaseDir(){
        return this.antProj.getBaseDir();
    }
    public AntPropertyMapper getPropertyMap() {
	return mapper;
    }
    public List<AntGlobalTarget> getTargets() {
	return targets;
    }
    public Hashtable<String, AntGlobalTarget> getGlobalTargets() {
	Hashtable<String, AntGlobalTarget> targetTable = new Hashtable<String, AntGlobalTarget>();
	for(AntGlobalTarget tgt : this.targets){
	    targetTable.put(tgt.getSignature(), tgt);
	}
	
	return targetTable;
    }
}
