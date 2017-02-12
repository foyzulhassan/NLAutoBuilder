package edu.utsa.cs.repoanalysis.buildconfig.ant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.tools.ant.Task;

import edu.utsa.cs.util.CommonUtils;

public class AntPropertyMapper {
    private Hashtable<String, List<AntProperty>> map;
    public AntPropertyMapper(){
        this.map = new Hashtable<String, List<AntProperty>>();
    }
    public AntProperty getProperty(String name, Task t){
        String enclose = t.getOwningTarget().getName();
        List<AntProperty> props = this.map.get(enclose);
        List<String> depends = CommonUtils.toList(t.getOwningTarget().getDependencies());
        Collections.sort(props, new AntPropertyComparator(depends));
        if(depends.indexOf(props.get(0).targetName) == -1){
            return null;
        }else{
            return props.get(0);
        }
    }
    public List<PropertyValue> resolve(String value, Task t) {
        List<PropertyValue> values = new ArrayList<PropertyValue>();
        int pos = value.indexOf("${");
        int lastStart = 0;
        while(pos!=-1){
            String constant = value.substring(lastStart, pos);
            values.add(new ConstantValue(constant));
            int next = value.indexOf("}", pos);
            String propName = value.substring(pos + 2, next);
            values.add(this.getProperty(propName, t));
            lastStart = next + 1;
            pos = value.indexOf("${", lastStart);
        }
        values.add(new ConstantValue(value.substring(lastStart)));
        return values;
    }
    public static abstract class PropertyValue{
        protected abstract String getValue();
        protected abstract boolean useProperty(String propName);
        protected abstract String getRelativeValue(String propName);
    }
    public static class AntProperty extends PropertyValue{
        private String name;
        private List<PropertyValue> values;
        private int lineNo;
        private String targetName;
        public AntProperty(String propertyName, List<PropertyValue> values, int lineNo, String targetName){
            this.name = propertyName;
            this.values = values;
            this.lineNo = lineNo;
            this.targetName = targetName;
        }
        public AntProperty(String propertyName, String value, int lineNo, String targetName) {
            this(propertyName, new ArrayList<PropertyValue>(), lineNo, targetName);
            this.values.add(new ConstantValue(value));
        }
        public String getEnclosingTarget(){
            return targetName;
        }
        public String getValue(){
            String value = "";
            for(PropertyValue val : this.values){
                value = value + val.getValue();
            }
            return value;
        }
        public List<PropertyValue> getValues(){
            return this.values;
        }
        public String getID(){
            return this.name + "_" + lineNo;
        }
        @Override
        protected boolean useProperty(String propName) {
            if(this.name.equals(propName)){
                return true;
            }
            for(PropertyValue value : this.values){
                if(value.useProperty(propName)){
                    return true;
                }
            }
            return false;
        }
        @Override
        protected String getRelativeValue(String propName) {
            String relVal = "";
            if(this.name.equals(propName)){
                return "${" + propName + "}";
            }
            for(PropertyValue value : this.values){
                relVal = relVal + value.getRelativeValue(propName);
            }
            return relVal;
        }
    }
    public static class AntPropertyComparator implements Comparator<AntProperty>{
        private List<String> depends;
        public AntPropertyComparator(List<String> depends){
            this.depends = depends;
        }
        @Override
        public int compare(AntProperty o1, AntProperty o2) {
            String target1 = o1.getEnclosingTarget();
            String target2 = o2.getEnclosingTarget();
            if(!target1.equals(target2)){
                Integer index1 = depends.indexOf(target1);
                Integer index2 = depends.indexOf(target2);
                return index1.compareTo(index2);
            }else{
                Integer line1 = o1.lineNo;
                Integer line2 = o2.lineNo;
                return line1.compareTo(line2);                
            }
        }
        
    }
    public static class ConstantValue extends PropertyValue{
        private String value;
        public ConstantValue(String value){
            this.value = value;
        }
        public String getValue(){
            return this.value;
        }
        protected String getRef() {
            return null;
        }
        @Override
        protected boolean useProperty(String propName) {
            return false;
        }
        @Override
        protected String getRelativeValue(String propName) {
            return this.value;
        }
    }
    public void put(String propertyName, AntProperty ap) {
        List<AntProperty> props = this.map.get(propertyName);
        if(props == null){
            props = new ArrayList<AntProperty>();
            props.add(ap);
        }
        this.map.put(propertyName, props);
    }
    public void incorporate(AntPropertyMapper propertyMap) {
	this.map.putAll(propertyMap.map);
	
    }
}

