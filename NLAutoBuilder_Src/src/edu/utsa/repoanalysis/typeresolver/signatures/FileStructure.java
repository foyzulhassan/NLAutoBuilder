package edu.utsa.repoanalysis.typeresolver.signatures;

import java.util.ArrayList;
import java.util.List;

import edu.utsa.repoanalysis.typeresolver.visitors.SimpleTypeResolver;

public class FileStructure {
    private String fileName;
    private List<ClassSignature> classes = new ArrayList<ClassSignature>();
    private SimpleTypeResolver resolver;

    public FileStructure(String fileName) {
	this.fileName = fileName;
    }

    public String getName() {
	return this.fileName;
    }

    public void setResolver(SimpleTypeResolver typeResolver) {
	this.resolver = typeResolver;
    }

    public void addClass(ClassSignature clazz) {
	this.classes.add(clazz);
    }

    public SimpleTypeResolver getResolver() {
	return this.resolver;
    }

    public void addConstraint(TypeSignature indexType, String string) {
	// TODO Auto-generated method stub

    }
}
