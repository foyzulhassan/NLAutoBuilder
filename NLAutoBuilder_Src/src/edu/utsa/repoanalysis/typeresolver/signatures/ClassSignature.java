package edu.utsa.repoanalysis.typeresolver.signatures;

import java.util.ArrayList;
import java.util.List;

import edu.utsa.repoanalysis.typeresolver.visitors.SimpleTypeResolver;

public class ClassSignature {
    private String fullQualifiedName;
    private String fullQualifiedSuper;
    private List<String> implementList;
    private List<String> paraTypes;
    private ClassSignature encloser;

    private List<ClassSignature> subclasses = new ArrayList<ClassSignature>();
    private List<MethodSignature> methods = new ArrayList<MethodSignature>();
    private List<FieldSignature> fields = new ArrayList<FieldSignature>();
    private SimpleTypeResolver typeResolver;

    public ClassSignature(String className) {
	this.fullQualifiedName = className;
	this.encloser = null;
	this.implementList = new ArrayList<String>();
    }

    public ClassSignature(String className, ClassSignature encloser) {
	this.encloser = encloser;
    }

    public String getQualifiedName() {
	return this.fullQualifiedName;
    }

    public ClassSignature getEncloser() {
	return this.encloser;
    }

    public void addMethod(MethodSignature ms) {
	this.methods.add(ms);
    }

    public void addField(FieldSignature fs) {
	this.fields.add(fs);
    }

    public void addSubClass(ClassSignature cs) {
	this.subclasses.add(cs);
    }

    public void setSuper(String fullSuperName) {
	this.fullQualifiedSuper = fullSuperName;
    }

    public String getSuper() {
	return this.fullQualifiedSuper;
    }

    public void addInterface(String iface) {
	this.implementList.add(iface);
    }

    public List<String> getImplementedInterfaces() {
	return this.implementList;
    }

    public void setResolver(SimpleTypeResolver typeResolver) {
	this.typeResolver = typeResolver;
    }

    public SimpleTypeResolver getResolver() {
	return this.typeResolver;
    }

    public List<String> getParaTypes() {
	return paraTypes;
    }

    public void setParaTypes(List<String> paraTypes) {
	this.paraTypes = paraTypes;
    }

}
