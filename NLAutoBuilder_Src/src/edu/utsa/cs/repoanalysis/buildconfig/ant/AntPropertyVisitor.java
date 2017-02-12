package edu.utsa.cs.repoanalysis.buildconfig.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import edu.utsa.cs.repoanalysis.buildconfig.ant.AntPropertyMapper.AntProperty;
import edu.utsa.cs.repoanalysis.buildconfig.ant.AntPropertyMapper.PropertyValue;

public class AntPropertyVisitor extends AntBuildFileVisitor{
    AntPropertyMapper mapper;
    private Project proj;
    public AntPropertyVisitor(AntBuildFile abf) throws AntTargetLoopException {
        //super(abf);
	super();
	this.proj=new Project();
        this.mapper = new AntPropertyMapper();
        this.proj=abf.getAntProject();
    }
    public void visitPropertyTask(Task t){
        String propertyName = (String)t.getRuntimeConfigurableWrapper().getAttributeMap().get("name");
        String value = (String)t.getRuntimeConfigurableWrapper().getAttributeMap().get("value");
        if(propertyName!=null && value!=null){
            List<PropertyValue> values = this.mapper.resolve(value, t);
            AntProperty ap = new AntProperty(propertyName, values, t.getLocation().getLineNumber(), t.getOwningTarget().getName());
            this.mapper.put(propertyName, ap);
        }else{
            String filepath = (String)t.getRuntimeConfigurableWrapper().getAttributeMap().get("file");
            List<PropertyValue> values = this.mapper.resolve(filepath, t);
            String filepathValue = "";
            for(PropertyValue propValue : values){
                filepathValue = filepathValue + propValue.getValue();
            }
            try {
                filepathValue = (new File(this.proj.getBaseDir().getAbsoluteFile() + filepathValue)).getCanonicalPath();
                
                Hashtable<String, String> externalTable = readFromFile(filepathValue);
                for(Map.Entry<String, String> entry : externalTable.entrySet()){
                    this.mapper.put(entry.getKey(), new AntProperty(entry.getKey(), entry.getValue(), 
                                    t.getLocation().getLineNumber(), t.getOwningTarget().getName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Hashtable<String, String> readFromFile(String filepathValue) throws IOException {
        Hashtable<String, String> table = new Hashtable<String, String>();
        BufferedReader in = new BufferedReader(new FileReader(filepathValue));
        for(String line = in.readLine(); line!=null; line = in.readLine()){
            String key = line.substring(0, line.indexOf("="));
            String value = line.substring(line.indexOf("=") + 1);
            table.put(key, value);
        }
        in.close();
        return table;
    }

    public AntPropertyMapper getMapper(){
        return this.mapper;
    }
}
