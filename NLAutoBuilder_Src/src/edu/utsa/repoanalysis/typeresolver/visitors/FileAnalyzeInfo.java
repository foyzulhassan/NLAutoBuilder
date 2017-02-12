package edu.utsa.repoanalysis.typeresolver.visitors;

import edu.utsa.repoanalysis.typeresolver.signatures.ClassSignature;

public class FileAnalyzeInfo {
    private SimpleTypeResolver typeResolver;
    private ClassSignature mainClass;

    public FileAnalyzeInfo(SimpleTypeResolver typeResolver) {
	this.typeResolver = typeResolver;
    }

    public SimpleTypeResolver getTypeResolver() {
	return typeResolver;
    }

    public ClassSignature getMainClass() {
	return mainClass;
    }

    public void setMainClass(ClassSignature mainClass) {
	this.mainClass = mainClass;
    }
}
