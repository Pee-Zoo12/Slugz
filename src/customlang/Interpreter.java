/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customlang;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {

    private Map<String, Object> variables;
    private Map<String, String> varTypes;
    private LanguageRunner.IOCallback io; // IO for input/output

    public Interpreter() {
        this.variables = new HashMap<>();
        this.varTypes = new HashMap<>();
        this.io = null;
    }

    // Setter for IO callback
    public void setIOCallback(LanguageRunner.IOCallback callback) {
        this.io = callback;
    }

    public void execute(ASTNode node) throws Exception {
        if (node instanceof Program) {
            executeProgram((Program) node);
        } else if (node instanceof VarDeclaration) {
            executeVarDeclaration((VarDeclaration) node);
        } else if (node instanceof Assignment) {
            executeAssignment((Assignment) node);
        } else if (node instanceof InputStmt) {
            executeInputStmt((InputStmt) node);
        } else if (node instanceof PrintStmt) {
            executePrintStmt((PrintStmt) node);
        } else if (node instanceof IfStmt) {
            executeIfStmt((IfStmt) node);
        } else {
            throw new Exception("Unknown AST node type: " + node.getClass().getName());
        }
    }

    private void executeProgram(Program node) throws Exception {
        for (ASTNode stmt : node.statements) {
            execute(stmt);
        }
    }

    private void executeVarDeclaration(VarDeclaration node) {
        varTypes.put(node.name, node.varType);

        switch (node.varType) {
            case "NT": variables.put(node.name, 0); break;
            case "FT": variables.put(node.name, 0.0); break;
            case "CH":
            case "ST": variables.put(node.name, ""); break;
        }
    }

    private void executeAssignment(Assignment node) throws Exception {
        if (!variables.containsKey(node.name)) {
            throw new Exception("Variable '" + node.name + "' not declared");
        }

        Object value = evaluate(node.expression);
        String type = varTypes.get(node.name);

        switch (type) {
            case "NT": variables.put(node.name, ((Number) value).intValue()); break;
            case "FT": variables.put(node.name, ((Number) value).doubleValue()); break;
            case "CH":
            case "ST": variables.put(node.name, value.toString()); break;
        }
    }

    private void executeInputStmt(InputStmt node) throws Exception {
        if (!variables.containsKey(node.varName)) {
            throw new Exception("Variable '" + node.varName + "' not declared");
        }

        if (io == null) throw new Exception("IOCallback not set for input.");

        String inputValue = io.input(node.prompt); // prompt user
        String type = varTypes.get(node.varName);

        switch (type) {
            case "NT": variables.put(node.varName, Integer.parseInt(inputValue)); break;
            case "FT": variables.put(node.varName, Double.parseDouble(inputValue)); break;
            case "CH":
            case "ST": variables.put(node.varName, inputValue); break;
        }
    }

    private void executePrintStmt(PrintStmt node) throws Exception {
        Object value = evaluate(node.value);

        if (io != null) {
            io.print(value.toString()); // print output only via IOCallback
        }
    }

    private void executeIfStmt(IfStmt node) throws Exception {
        for (int i = 0; i < node.conditions.size(); i++) {
            if (evaluateCondition((Condition) node.conditions.get(i))) {
                for (ASTNode stmt : node.thenBlocks.get(i)) {
                    execute(stmt);
                }
                return;
            }
        }

        if (node.elseBlock != null) {
            for (ASTNode stmt : node.elseBlock) execute(stmt);
        }
    }

    private boolean evaluateCondition(Condition node) throws Exception {
        Object left = evaluate(node.left);
        Object right = evaluate(node.right);

        int cmp = compare(left, right);

        switch (node.operator) {
            case "==": return cmp == 0;
            case "!=": return cmp != 0;
            case ">": return cmp > 0;
            case "<": return cmp < 0;
            case ">=": return cmp >= 0;
            case "<=": return cmp <= 0;
        }
        throw new Exception("Unknown operator: " + node.operator);
    }

    private int compare(Object a, Object b) {
        if (a instanceof Number && b instanceof Number)
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        return a.toString().compareTo(b.toString());
    }

    private Object evaluate(ASTNode node) throws Exception {
        if (node instanceof NumberNode) return ((NumberNode) node).value;
        if (node instanceof Identifier) return variables.get(((Identifier) node).name);
        if (node instanceof StringNode) return ((StringNode) node).value;
        if (node instanceof BinaryOp) return evalBinary((BinaryOp) node);

        throw new Exception("Cannot evaluate " + node.getClass().getName());
    }

    private Object evalBinary(BinaryOp node) throws Exception {
        double l = ((Number) evaluate(node.left)).doubleValue();
        double r = ((Number) evaluate(node.right)).doubleValue();

        switch (node.operator) {
            case "+": return l + r;
            case "-": return l - r;
            case "*": return l * r;
            case "/": return l / r;
        }
        throw new Exception("Unknown operator: " + node.operator);
    }

    public void reset() {
        variables.clear();
        varTypes.clear();
    }

    public Map<String, Object> getVariables() {
        return new HashMap<>(variables);
    }
}
