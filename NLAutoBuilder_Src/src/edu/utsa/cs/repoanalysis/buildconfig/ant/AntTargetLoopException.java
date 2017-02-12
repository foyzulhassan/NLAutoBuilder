package edu.utsa.cs.repoanalysis.buildconfig.ant;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Target;

public class AntTargetLoopException extends Exception{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<Target> loop = new ArrayList<Target>();
    private String message;
    public AntTargetLoopException(String message, List<Target> loop){
        this.loop = loop;
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public List<Target> getLoop(){
        return this.loop;
    }
}
