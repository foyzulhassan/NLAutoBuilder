package edu.utsa.repoanalysis.typeresolver.signatures;

import java.util.ArrayList;
import java.util.List;

public class TypeSignature implements Cloneable {
    private String fullName;
    private boolean isPrim;
    private boolean isArray;
    private boolean isParameter;
    private List<TypeSignature> paras;
    private int level;

    public TypeSignature(String fullName) {
	this.fullName = fullName;
	this.isArray = false;
	this.isParameter = false;
	this.isPrim = false;
	this.level = 0;
    }

    public void setArray(int level) {
	this.isArray = true;
	this.level = level;
    }

    public void setParameter() {
	this.isParameter = true;
	this.paras = new ArrayList<TypeSignature>();
    }

    public void setPrim() {
	this.isPrim = true;
    }

    public int getLevel() {
	return this.level;
    }

    public boolean isPrim() {
	return this.isPrim;
    }

    public boolean isParam() {
	return this.isParameter;
    }

    public boolean isArray() {
	return this.isArray;
    }

    public void addPara(TypeSignature para) {
	this.paras.add(para);
    }

    public String getFullName() {
	return this.fullName;
    }

    public TypeSignature reduceLevel() throws CloneNotSupportedException {
	assert (this.isArray);
	TypeSignature ts = (TypeSignature) this.clone();
	ts.level = ts.level - 1;
	if (ts.level == 0) {
	    ts.isArray = false;
	}
	return ts;
    }

    public boolean isVariable() {
	// TODO Auto-generated method stub
	return false;
    }

    public static TypeSignature parseBinaryType(String type) {
	if (type.indexOf('.') == -1) {
	    TypeSignature sig = new TypeSignature(type);
	    sig.setPrim();
	    return sig;
	} else if (type.indexOf('[') != -1) {
	    int level = getCount(type, '[');
	    TypeSignature sig = new TypeSignature(type);
	    sig.setArray(level);
	    return sig;
	}
	return new TypeSignature(type);
    }

    private static int getCount(String type, char c) {
	int sum = 0;
	for (int i = 0; i < type.length(); i++) {
	    if (type.charAt(i) == c) {
		sum++;
	    }
	}
	return sum;
    }
}
