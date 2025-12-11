/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customlang;

/**
 *
 * @author Pearly Jaleco
 */
import java.util.List;

// Base AST Node
abstract class ASTNode {
}

// Program node
class Program extends ASTNode {
    List<ASTNode> statements;
    
    public Program(List<ASTNode> statements) {
        this.statements = statements;
    }
}

// Variable Declaration
class VarDeclaration extends ASTNode {
    String name;
    String varType;
    
    public VarDeclaration(String name, String varType) {
        this.name = name;
        this.varType = varType;
    }
}

// Assignment
class Assignment extends ASTNode {
    String name;
    ASTNode expression;
    
    public Assignment(String name, ASTNode expression) {
        this.name = name;
        this.expression = expression;
    }
}

// Input Statement
class InputStmt extends ASTNode {
    String prompt;
    String varName;
    
    public InputStmt(String prompt, String varName) {
        this.prompt = prompt;
        this.varName = varName;
    }
}

// Print Statement
class PrintStmt extends ASTNode {
    ASTNode value;
    
    public PrintStmt(ASTNode value) {
        this.value = value;
    }
}

// If Statement
class IfStmt extends ASTNode {
    List<ASTNode> conditions;
    List<List<ASTNode>> thenBlocks;
    List<ASTNode> elseBlock;
    
    public IfStmt(List<ASTNode> conditions, List<List<ASTNode>> thenBlocks, List<ASTNode> elseBlock) {
        this.conditions = conditions;
        this.thenBlocks = thenBlocks;
        this.elseBlock = elseBlock;
    }
}

// Condition
class Condition extends ASTNode {
    ASTNode left;
    String operator;
    ASTNode right;
    
    public Condition(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Binary Operation
class BinaryOp extends ASTNode {
    ASTNode left;
    String operator;
    ASTNode right;
    
    public BinaryOp(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Number literal
class NumberNode extends ASTNode {
    Object value;
    
    public NumberNode(Object value) {
        this.value = value;
    }
}

// Identifier
class Identifier extends ASTNode {
    String name;
    
    public Identifier(String name) {
        this.name = name;
    }
}

// String literal
class StringNode extends ASTNode {
    String value;
    
    public StringNode(String value) {
        this.value = value;
    }
}
