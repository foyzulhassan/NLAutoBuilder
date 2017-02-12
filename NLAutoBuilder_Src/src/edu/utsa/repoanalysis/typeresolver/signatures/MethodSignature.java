package edu.utsa.repoanalysis.typeresolver.signatures;

import java.util.List;

public class MethodSignature {
    private TypeSignature returnType;
    private String name;
    private List<TypeSignature> parameters;

    public MethodSignature(TypeSignature retType, String name,
	    List<TypeSignature> paras) {

    }

    public TypeSignature getReturnType() {
	return returnType;
    }

    public void setReturnType(TypeSignature returnType) {
	this.returnType = returnType;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<TypeSignature> getParameters() {
	return parameters;
    }

    public void setParameters(List<TypeSignature> parameters) {
	this.parameters = parameters;
    }

}
