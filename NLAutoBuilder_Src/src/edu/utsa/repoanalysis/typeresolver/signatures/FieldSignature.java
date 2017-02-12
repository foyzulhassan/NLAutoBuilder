package edu.utsa.repoanalysis.typeresolver.signatures;

public class FieldSignature {

    private String name;
    private TypeSignature type;

    public FieldSignature(String name, TypeSignature type) {
	this.setName(name);
	this.setType(type);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public TypeSignature getType() {
	return type;
    }

    public void setType(TypeSignature type) {
	this.type = type;
    }
}
