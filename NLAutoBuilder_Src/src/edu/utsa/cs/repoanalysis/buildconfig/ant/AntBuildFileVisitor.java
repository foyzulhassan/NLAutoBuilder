package edu.utsa.cs.repoanalysis.buildconfig.ant;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;

public class AntBuildFileVisitor {
//    protected List<Target> sortTargets;
//    private Hashtable<String, HashSet<String>> dependTable;
//    private Hashtable<String, HashSet<String>> sequenceTable;
//    private Hashtable<String, String> previousTable;
    
    public AntBuildFileVisitor(){
//        this.sortTargets = new ArrayList<Target>();
//        this.dependTable = new Hashtable<String, HashSet<String>>();
//        this.sequenceTable = new Hashtable<String, HashSet<String>>();
//        this.previousTable = new Hashtable<String, String>();
//        sortTargets();
    }
/*    private void sortTargets() throws AntTargetLoopException {
        List<Target> targets = new ArrayList<Target>();
        targets.addAll(proj.getTargets().values());
        for(Target t : targets){
            sequenceTable.put(t.getName(), new HashSet<String>());
        }
        for(Target t : targets){
            Enumeration<String> depends = t.getDependencies();
            HashSet<String> dependset = new HashSet<String>();
            String lastDepend = "";
            while(depends.hasMoreElements()){
                String depend = depends.nextElement();
                lastDepend = depend;
                sequenceTable.get(depend).addAll(dependset);
                dependset.add(depend);
            }
            this.previousTable.put(t.getName(), lastDepend);
            dependTable.put(t.getName(), dependset);
        }
        HashSet<String> rmTargetNames = new HashSet<String>();
        while(targets.size() > 0){
            boolean addAtleastOne = false;
            for(int i = 0; i< targets.size(); i++){
                Target t = targets.get(i);
                HashSet<String> depends = dependTable.get(t.getName());
                HashSet<String> befores = sequenceTable.get(t.getName());
                if(rmTargetNames.contains(depends) && rmTargetNames.contains(befores)){
                    addAtleastOne = true;
                    this.sortTargets.add(t);
                    rmTargetNames.add(t.getName());
                    targets.remove(i);
                    i--;
                }
            }
            if(!addAtleastOne){
                throw new AntTargetLoopException("Target Dependency Loop in Ant Build File", targets);
            }
        }
    }*/
    
    public void visitAntProject(Project proj) {
	for(Target tgt : proj.getTargets().values()){
	    visitTarget(tgt);
	}
    }    
    public void visitAntProject(Project proj, Target t){
        List<Target> targets = new ArrayList<Target>();
        Enumeration<String> depends = t.getDependencies();
        while(depends.hasMoreElements()){
            targets.add(proj.getTargets().get(depends.nextElement()));
        }
        targets.add(t);
        visitTargets(targets);
    }
    protected void visitTargets(List<Target> targets) {
        for(Target tgt : targets){
            visitTarget(tgt);
        }
    }
    protected boolean beforeVisitTarget(Target tgt) {
	return true;
    }    
    protected void afterVisitTarget(Target tgt){
	
    }
    protected void visitTarget(Target tgt) {
	if(!beforeVisitTarget(tgt)){
	    return;
	}
        for(Task t : tgt.getTasks()){
            visitTask(t);
        }
        afterVisitTarget(tgt);
    }
    protected boolean beforeVisitTask(Task t) {
	return true;
    }    
    protected void visitTask(Task t) {
	if(!beforeVisitTask(t)){
	    return;
	}
        if(t.getTaskType().equals("BZip2") || t.getTaskType().equals("GZip") || t.getTaskType().equals("Jar") || t.getTaskType().equals("SignJar")
                        || t.getTaskType().equals("Tar")|| t.getTaskType().equals("War")|| t.getTaskType().equals("Zip")){
            visitCompressTask(t);
        }else if(t.getTaskType().equals("Depend") || t.getTaskType().equals("Javac") || t.getTaskType().equals("Apt") || t.getTaskType().equals("JspC") 
                        || t.getTaskType().equals("Rmic")){
            visitCompileTask(t);
        }else if(t.getTaskType().equals("Javadoc")){
            visitJavadoc(t);
        }else if(t.getTaskType().equals("BUnzip2")||t.getTaskType().equals("GUnzip")||t.getTaskType().equals("Unjar")||t.getTaskType().equals("Untar")
                        ||t.getTaskType().equals("Unwar")||t.getTaskType().equals("Unzip")){
            visitDecompressTask(t);
        }else if(t.getTaskType().equals("Ant")||t.getTaskType().equals("AntCall")||t.getTaskType().equals("Exec")||t.getTaskType().equals("Java")
                        ||t.getTaskType().equals("Parallel")||t.getTaskType().equals("Sequential")){
            visitExecutionTask(t);
        }else if(t.getTaskType().equals("Copy")||t.getTaskType().equals("Move")){
            visitFileTransferTask(t);
        }else if(t.getTaskType().equals("Mkdir")||t.getTaskType().equals("Touch")||t.getTaskType().equals("Tmpfile")){
            visitFileNewTask(t);
        }else if(t.getTaskType().equals("Delete")){
            visitDeleteTask(t);
        }else if(t.getTaskType().equals("Macrodef")){
            visitMacrodefTask(t);            
        }else if(t.getTaskType().equals("Presetdef")){
            visitPresetdefTask(t);            
        }else if(t.getTaskType().equals("Available") || t.getTaskType().equals("Condition") || t.getTaskType().equals("Uptodate") || t.getTaskType().equals("Property")
                        ||t.getTaskType().equals("LoadProperties")){
            visitGeneralPropTask(t);
        }else if(t.getTaskType().equals("Fail")){
            visitFailTask(t);
        }else if(t.getTaskType().equals("Junit")){
            visitJunitTask(t);
        }else if(t.getTaskType().equals("Import")){
            visitImportTask(t);
        }else if(t.getTaskType().equals("Include")){
            visitIncludeTask(t);
        }else{
            visitOtherTask(t);
        }
        afterVisitTask(t);
    }
    protected void visitIncludeTask(Task t) {
	// TODO Auto-generated method stub
	
    }

    protected void visitImportTask(Task t) {
	// TODO Auto-generated method stub
	
    }

    protected void afterVisitTask(Task t) {
    }    

    protected void visitPresetdefTask(Task t) {
        
    }
    protected void visitOtherTask(Task t) {
        
    }
    protected void visitJunitTask(Task t) {
        
    }
    protected void visitFailTask(Task t) {
        
    }
    protected boolean beforeVisitGeneralTask(Task t) {
	return true;
    }    
    protected void afterVisitGeneralPropTask(Task t) {
	
    }
    protected void visitGeneralPropTask(Task t) {
	if(!beforeVisitGeneralTask(t)){
	    return;
	}
        if(t.getTaskType().equals("Available")){
            visitAvailableTask(t);
        }else if(t.getTaskType().equals("Condition")){
            visitConditionTask(t);
        }else if(t.getTaskType().equals("Uptodate")){
            visitUptodateTask(t);
        }else if(t.getTaskType().equals("Property")){
            visitPropertyTask(t);
        }else if(t.getTaskType().equals("LoadProperties")){
            visitLoadPropertiesTask(t);
        }
        afterVisitGeneralPropTask(t);
    }

    protected void visitLoadPropertiesTask(Task t) {
        
    }
    protected void visitPropertyTask(Task t) {
        
    }
    protected void visitUptodateTask(Task t) {
        
    }
    protected void visitConditionTask(Task t) {
        
    }
    protected void visitAvailableTask(Task t) {
        
    }
    protected boolean beforeVisitMacrodefTask(Task t) {
	return true;
    }    
    protected void afterVisitMacrodefTask(Task t) {
	
    }
    protected void visitMacrodefTask(Task t) {
	if(!beforeVisitMacrodefTask(t)){
	    return;
	}
        UnknownElement une = (UnknownElement)t;
        for(UnknownElement nested : une.getChildren()){
            visitTask(nested);
        }
        afterVisitMacrodefTask(t);
    }
    protected void visitDeleteTask(Task t) {
        
    }
    protected boolean beforeVisitFileNewTask(Task t) {
	return true;
    }    
    protected void afterVisitFileNewTask(Task t) {
	
    }
    protected void visitFileNewTask(Task t) {
	if(!beforeVisitMacrodefTask(t)){
	    return;
	}
        if(t.getTaskType().equals("Mkdir")){
            visitMkdir(t);
        }else if(t.getTaskType().equals("Touch")){
            visitTouch(t);
        }else if(t.getTaskType().equals("Tmpfile")){
            visitTmpfile(t);
        }
        afterVisitFileNewTask(t);
    }
    protected void visitTmpfile(Task t) {
        
    }
    protected void visitTouch(Task t) {
        
    }
    protected void visitMkdir(Task t) {
        
    }
    protected boolean beforeVisitFileTransferTask(Task t) {
	return true;
    }    
    protected void afterVisitFileTransferTask(Task t) {
	
    }
    protected void visitFileTransferTask(Task t) {
	if(!beforeVisitFileTransferTask(t)){
	    return;
	}
        if(t.getTaskType().equals("Copy")){
            visitCopy(t);
        }else if(t.getTaskType().equals("Move")){
            visitMove(t);
        }
        afterVisitFileTransferTask(t);
    }
    protected void visitMove(Task t) {
        
    }
    protected void visitCopy(Task t) {
        
    }
    protected boolean beforeVisitExecutionTask(Task t) {
	return true;
    }    
    protected void afterVisitExecutionTask(Task t) {
	
    }
    protected void visitExecutionTask(Task t) {
	if(!beforeVisitExecutionTask(t)){
	    return;
	}
        if(t.getTaskType().equals("Ant")){
            visitAnt(t);
        }else if(t.getTaskType().equals("AntCall")){
            visitAntCall(t);            
        }else if(t.getTaskType().equals("Exec")){
            visitExec(t);
        }else if(t.getTaskType().equals("Java")){
            visitJava(t);
        }else if(t.getTaskType().equals("Parallel")){
            visitParallel(t);
        }else if(t.getTaskType().equals("Sequential")){
            visitSequential(t);
        } 
        afterVisitExecutionTask(t);
    }
    protected boolean beforeVisitSequential(Task t) {
	return true;
    }    
    protected void afterVisitSequential(Task t) {
 	
    }

    protected void visitSequential(Task t) {
	if(!beforeVisitSequential(t)){
	    return;
	}
        UnknownElement une = (UnknownElement)t;
        for(UnknownElement nested : une.getChildren()){
            visitTask(nested);
        }
        afterVisitSequential(t);
    }
    protected boolean beforeVisitParallel(Task t) {
	return true;
    }    
    protected void afterVisitParallel(Task t) {
 	
    }
    
    protected void visitParallel(Task t) {
	if(!beforeVisitParallel(t)){
	    return;
	}
        UnknownElement une = (UnknownElement)t;
        for(UnknownElement nested : une.getChildren()){
            visitTask(nested);
        }   
        afterVisitParallel(t);
    }
    protected void visitJava(Task t) {
        
    }
    protected void visitExec(Task t) {
        
    }
    protected void visitAntCall(Task t) {
        
    }
    protected void visitAnt(Task t) {
        
    }
    protected boolean beforeVisitDecompressTask(Task t) {
	return true;
    }    
    protected void afterVisitDecompressTask(Task t) {
 	
    }
    protected void visitDecompressTask(Task t) {
	if(!beforeVisitDecompressTask(t)){
	    return;
	}
        if(t.getTaskType().equals("BUnzip2")){
            visitBZip2(t);
        }else if(t.getTaskType().equals("GUnzip")){
            visitGZip(t);            
        }else if(t.getTaskType().equals("UnJar")){
            visitJar(t);
        }else if(t.getTaskType().equals("UnTar")){
            visitTar(t);
        }else if(t.getTaskType().equals("UnWar")){
            visitWar(t);
        }else if(t.getTaskType().equals("UnZip")){
            visitZip(t);
        }       
        afterVisitDecompressTask(t);
    }
    protected void visitJavadoc(Task t) {
        
    }
    protected boolean beforeVisitCompileTask(Task t) {
	return true;
    }    
    protected void afterVisitCompileTask(Task t) {
 	
    }
    protected void visitCompileTask(Task t) {
	if(!beforeVisitCompileTask(t)){
	    return;
	}
        if(t.getTaskType().equals("Depend")){
            visitDepend(t);
        }else if(t.getTaskType().equals("Javac")){
            visitJavac(t);            
        }else if(t.getTaskType().equals("Apt")){
            visitApt(t);
        }else if(t.getTaskType().equals("JspC")){
            visitJspC(t);
        }else if(t.getTaskType().equals("Rmic")){
            visitRmic(t);
        }  
        afterVisitCompileTask(t);
    }
    protected void visitRmic(Task t) {
        
    }
    protected void visitJspC(Task t) {
        
    }
    protected void visitApt(Task t) {
        
    }
    protected void visitJavac(Task t) {
        
    }
    protected void visitDepend(Task t) {
        
    }
    protected boolean beforeVisitCompressTask(Task t) {
	return true;
    }    
    protected void afterVisitCompressTask(Task t) {
 	
    }
    protected void visitCompressTask(Task t) {
	if(!beforeVisitCompressTask(t)){
	    return;
	}
        if(t.getTaskType().equals("BZip2")){
            visitBZip2(t);
        }else if(t.getTaskType().equals("GZip")){
            visitGZip(t);            
        }else if(t.getTaskType().equals("Jar")){
            visitJar(t);
        }else if(t.getTaskType().equals("SignJar")){
            visitSignJar(t);
        }else if(t.getTaskType().equals("Tar")){
            visitTar(t);
        }else if(t.getTaskType().equals("War")){
            visitWar(t);
        }else if(t.getTaskType().equals("Zip")){
            visitZip(t);
        }
        afterVisitCompressTask(t);
    }
    protected void visitZip(Task t) {
        
    }
    protected void visitWar(Task t) {
        
    }
    protected void visitTar(Task t) {
        
    }
    protected void visitSignJar(Task t) {
        
    }
    protected void visitJar(Task t) {
        
    }
    protected void visitGZip(Task t) {
        
    }
    protected void visitBZip2(Task t) {
        
    }
}
