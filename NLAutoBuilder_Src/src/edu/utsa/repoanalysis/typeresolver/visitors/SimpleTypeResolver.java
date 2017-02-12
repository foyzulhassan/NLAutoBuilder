package edu.utsa.repoanalysis.typeresolver.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;

import edu.utsa.repoanalysis.typeresolver.signatures.TypeSignature;

public class SimpleTypeResolver {
    private String pkgName;
    private List<String> imported;

    public SimpleTypeResolver(String pkgName, List<String> imported) {
	this.pkgName = pkgName;
	this.imported = imported;
    }
    public void setPkgName(String name){
	this.pkgName = name;
    }

    public String getFullName(String simpleName) {
	for (String item : imported) {
	    if (item.endsWith("." + simpleName)) {
		return item;
	    }
	}
	return this.pkgName + "." + simpleName;
    }

    public List<String> getImports() {
	return this.imported;
    }

    public void addImport(String item) {
	String className = getClassName(item);
	this.imported.add(className);
    }

    private String getClassName(String item) {
	int index = 0;
	for (int i = 0; i < item.length() - 1; i++) {
	    char c = item.charAt(i);
	    char next = item.charAt(i + 1);
	    if (c == '.' && Character.isLetter(next)
		    && Character.isUpperCase(next)) {
		index = i + 1;
		int end = item.indexOf('.', index);
		if (end == -1) {
		    return item;
		} else {
		    return item.substring(0, end);
		}
	    }
	}
	return item;
    }

    public TypeSignature getFullType(Type t) {
	if (t.isArrayType()) {
	    ArrayType at = (ArrayType) t;
	    return resolveArray(at);
	} else if (t.isParameterizedType()) {
	    ParameterizedType pt = (ParameterizedType) t;
	    return resolveParam(pt);
	} else if (t.isPrimitiveType()) {
	    TypeSignature ts = new TypeSignature(t.toString());
	    ts.setPrim();
	    return ts;
	} else if (t.isQualifiedType() || t.isSimpleType()) {
	    return resolveSimpleType(t);
	} else {
	    System.err.println("Error: Unsupported type " + t.toString());
	    return null;
	}
    }

    private TypeSignature resolveParam(ParameterizedType pt) {
	TypeSignature ts = resolveSimpleType(pt.getType());
	ts.setParameter();
	List<Type> params = pt.typeArguments();
	for (Type param : params) {
	    TypeSignature paramSig = getFullType(param);
	    ts.addPara(paramSig);
	}
	return ts;
    }

    private TypeSignature resolveArray(ArrayType at) {
	int level = 0;
	Type t = at;
	while (t.isArrayType()) {
	    t = at.getComponentType();
	    level++;
	}
	if (t.isPrimitiveType()) {
	    TypeSignature ts = new TypeSignature(t.toString());
	    ts.setPrim();
	    return ts;
	} else {
	    TypeSignature ts = resolveSimpleType(t);
	    ts.setArray(level);
	    return ts;
	}
    }

    private TypeSignature resolveSimpleType(Type t) {
	if (t.isSimpleType() || t.isQualifiedType()) {
	    String fullName = this.getFullName(t.toString());
	    TypeSignature ts = new TypeSignature(fullName);
	    return ts;
	} else {
	    System.err.println("Error: Unsupported type " + t.toString());
	    return null;
	}
    }
}
