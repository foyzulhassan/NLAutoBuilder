package edu.utsa.cs.repoanalysis.typeresolver.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.utsa.repoanalysis.typeresolver.visitors.BasicSignatureVisitor;
import edu.utsa.repoanalysis.typeresolver.visitors.FileAnalyzeInfo;

public class SrcFileAnalyzer {
    private Hashtable<String, CompilationUnit> parsedUnits = new Hashtable<String, CompilationUnit>();

    public void parse(String filePath) throws IOException {
	String sourceCode = "";
	BufferedReader in = new BufferedReader(new FileReader(filePath));
	for (String line = in.readLine(); line != null; line = in.readLine()) {
	    sourceCode = sourceCode + line + "\n";
	}
	// System.out.println(sourceCode);
	in.close();
	ASTParser parse = ASTParser.newParser(AST.JLS4);
	parse.setSource(sourceCode.toCharArray());
	parse.setKind(ASTParser.K_COMPILATION_UNIT);
	final CompilationUnit cu = (CompilationUnit) parse.createAST(null);
	this.parsedUnits.put(filePath, cu);
    }

    public FileAnalyzeInfo shallowAnalyze(String filePath) throws IOException {
	CompilationUnit cu = this.parsedUnits.get(filePath);
	if (cu == null) {
	    parse(filePath);
	    cu = this.parsedUnits.get(filePath);
	    // System.out.println(filePath);
	    // System.out.println(cu);
	}
	BasicSignatureVisitor visitor = new BasicSignatureVisitor();
	cu.accept(visitor);
	return visitor.getInfo();
    }
}
