package edu.utsa.repoanalysis.typeresolver.visitors;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.utsa.cs.repoanalysis.typeresolver.client.Constraint;
import edu.utsa.repoanalysis.typeresolver.signatures.ClassSignature;
import edu.utsa.repoanalysis.typeresolver.signatures.TypeSignature;

public class TypeResolveVisitor extends ASTVisitor {
    private Hashtable<String, TypeSignature> varTable = new Hashtable<String, TypeSignature>();
    private ClassSignature cs;
    private Set<Constraint> constraints;

    public TypeResolveVisitor(ClassSignature cs, Set<Constraint> constraints) {
	this.cs = cs;
	this.constraints = constraints;
    }

    public boolean visit(VariableDeclarationStatement decl) {
	TypeSignature varType = this.cs.getResolver().getFullType(
		decl.getType());
	for (Object obj : decl.fragments()) {
	    VariableDeclarationFragment singleVar = (VariableDeclarationFragment) obj;
	    this.varTable.put(singleVar.getName().getIdentifier(), varType);
	}
	return true;
    }

    public void endVisit(ArrayAccess invoc) {
	Expression array = invoc.getArray();
	TypeSignature arrayType = (TypeSignature) array.getProperty("type");
	Expression index = invoc.getIndex();
	TypeSignature indexType = (TypeSignature) index.getProperty("type");
	if (indexType.isVariable()) {
	    this.constraints.add(new Constraint(indexType, "int"));
	}
	if (arrayType.isVariable()) {

	}
	assert (arrayType.isArray());
	try {
	    invoc.setProperty("type", arrayType.reduceLevel());
	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	}
    }

    public void endVisit(ArrayInitializer init) {
	List<Expression> exprs = init.expressions();
	for (Expression expr : exprs) {
	    TypeSignature exprType = (TypeSignature) expr.getProperty("type");
	    if (exprType.isVariable()) {
		this.constraints.add(new Constraint(exprType, "int"));
	    }
	}
    }

    public void endVisit(Assignment invoc) {

    }

    public void endVisit(BooleanLiteral invoc) {

    }

    public void endVisit(CastExpression invoc) {

    }

    public void endVisit(CharacterLiteral invoc) {

    }

    public void endVisit(ClassInstanceCreation invoc) {

    }

    public void endVisit(ConditionalExpression invoc) {

    }

    public void endVisit(ConstructorInvocation invoc) {

    }

    public void endVisit(DoStatement invoc) {

    }

    public void endVisit(EnhancedForStatement invoc) {

    }

    public void endVisit(FieldAccess invoc) {

    }

    public void endVisit(FieldDeclaration invoc) {

    }

    public void endVisit(ForStatement invoc) {

    }

    public void endVisit(IfStatement invoc) {

    }

    public void endVisit(InfixExpression invoc) {

    }

    public void endVisit(NullLiteral invoc) {

    }

    public void endVisit(NumberLiteral invoc) {

    }

    public void endVisit(ParenthesizedExpression invoc) {

    }

    public void endVisit(PrefixExpression invoc) {

    }

    public void endVisit(PostfixExpression invoc) {

    }

    public void endVisit(QualifiedName invoc) {

    }

    public void endVisit(SingleVariableDeclaration invoc) {

    }

    public void endVisit(StringLiteral invoc) {

    }

    public void endVisit(SuperConstructorInvocation invoc) {

    }

    public void endVisit(SwitchStatement invoc) {

    }

    public void endVisit(SynchronizedStatement invoc) {

    }

    public void endVisit(ThisExpression invoc) {

    }

    public void endVisit(TypeLiteral invoc) {

    }

    public void endVisit(TypeParameter invoc) {

    }

    public void endVisit(VariableDeclarationFragment invoc) {

    }

    public void endVisit(VariableDeclarationStatement invoc) {

    }

    public void endVisit(WhileStatement invoc) {

    }

    public void endVisit(MethodInvocation invoc) {

    }

    public void endVisit(ReturnStatement invoc) {

    }
}
