package edu.utsa.repoanalysis.typeresolver.visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.utsa.repoanalysis.typeresolver.signatures.ClassSignature;

public class BasicSignatureVisitor extends ASTVisitor {
    private ClassSignature currentClass;
    private ClassSignature mainClass;
    private String packagePrefix;
    private SimpleTypeResolver typeResolver;
    private FileAnalyzeInfo info;

    public BasicSignatureVisitor() {
	this.currentClass = null;
	this.typeResolver = new SimpleTypeResolver(this.packagePrefix,
		new ArrayList<String>());
	this.info = new FileAnalyzeInfo(this.typeResolver);
    }

    public boolean visit(PackageDeclaration node) {
	this.packagePrefix = node.getName().getFullyQualifiedName();
	this.typeResolver.setPkgName(this.packagePrefix);
	return true;
    }

    public boolean visit(ImportDeclaration node) {
	this.typeResolver.addImport(node.getName().getFullyQualifiedName());
	return true;
    }

    public boolean visit(TypeDeclaration node) {
	if (this.currentClass == null) {
	    ClassSignature sig = new ClassSignature(this.packagePrefix + "."
		    + node.getName().getIdentifier());
	    this.currentClass = sig;
	    sig.setResolver(this.typeResolver);
	    this.info.setMainClass(sig);
	    this.mainClass = sig;
	} else {
	    ClassSignature sig = new ClassSignature(
		    this.currentClass.getQualifiedName() + "."
			    + node.getName().getIdentifier(), this.currentClass);
	    this.currentClass.addSubClass(sig);
	    this.currentClass = sig;
//	    System.err.println("nested class not supported yet");
	}
	return true;
    }

    public void endVisit(TypeDeclaration node) {
	this.currentClass = this.currentClass.getEncloser();
    }

    public boolean visit(MethodDeclaration node) {
	/*
	 * List<TypeSignature> params = new ArrayList<TypeSignature>(); for
	 * (Object p : node.parameters()) { SingleVariableDeclaration svd =
	 * (SingleVariableDeclaration) p;
	 * params.add(this.typeResolver.getFullType(svd.getType())); }
	 * 
	 * Type retType = node.getReturnType2();
	 * 
	 * if(retType==null){ if(node.getParent() instanceof TypeDeclaration){
	 * TypeDeclaration td = (TypeDeclaration)node.getParent(); retType = td
	 * } System.out.println(retType); } MethodSignature ms = new
	 * MethodSignature(
	 * this.typeResolver.getFullType(node.getReturnType2()), node
	 * .getName().getIdentifier(), params); this.currentClass.addMethod(ms);
	 * return false; }
	 * 
	 * public boolean visit(FieldDeclaration node) { for (Object obj :
	 * node.fragments()) { VariableDeclarationFragment frag =
	 * (VariableDeclarationFragment) obj; FieldSignature fs = new
	 * FieldSignature(frag.getName() .getIdentifier(),
	 * this.typeResolver.getFullType(node .getType()));
	 * this.currentClass.addField(fs); }
	 */
	return false;
    }

    public FileAnalyzeInfo getInfo() {
	return info;
    }
}
