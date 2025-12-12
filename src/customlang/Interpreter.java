/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customlang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

    private Map<String, Object> variables = new HashMap<>();
    private IOCallback io;

    // ----------------------
    // Constructor
    // ----------------------
    public Interpreter(IOCallback io) {
        this.io = io;
    }

    // ----------------------
    // Inner IOCallback interface
    // ----------------------
    public interface IOCallback {
        void print(String text);
        String read(String prompt);
    }

    // ----------------------
    // Run the program
    // ----------------------
    public void run(Map<String, Object> program) {
        List<Map<String, Object>> statements = (List<Map<String, Object>>) program.get("statements");
        for (Map<String, Object> stmt : statements) {
            execute(stmt);
        }
    }

    // ----------------------
    // Execute a statement
    // ----------------------
    private void execute(Map<String, Object> node) {
        String type = (String) node.get("type");

        switch (type) {
            case "VarDeclaration":
                executeVarDeclaration(node);
                break;
            case "Assignment":
                executeAssignment(node);
                break;
            case "Print":
                executePrint(node);
                break;
            case "Input":
                executeInput(node);
                break;
            case "If":
                executeIf(node);
                break;
            default:
                throw new RuntimeException("Unknown statement type: " + type);
        }
    }

    private void executeVarDeclaration(Map<String, Object> node) {
        String name = (String) node.get("name");
        variables.put(name, 0);
    }

    private void executeAssignment(Map<String, Object> node) {
        String name = (String) node.get("name");
        Map<String, Object> valueNode = (Map<String, Object>) node.get("value");
        Object value = evaluate(valueNode);
        variables.put(name, value);
    }

    private void executePrint(Map<String, Object> node) {
    Map<String, Object> valueNode = (Map<String, Object>) node.get("value");
    Object value = evaluate(valueNode);
    if (value instanceof Double && ((Double) value) % 1 == 0) {
        value = ((Double) value).intValue();
    }

    io.print(value.toString());
}


    private void executeInput(Map<String, Object> node) {
        String prompt = (String) node.get("prompt");
        String varName = (String) node.get("varName");

        String input = io.read(prompt);

        try {
            variables.put(varName, Double.parseDouble(input));
        } catch (NumberFormatException e) {
            variables.put(varName, input);
        }
    }

    private void executeIf(Map<String, Object> node) {
        Map<String, Object> condition = (Map<String, Object>) node.get("condition");
        boolean result = evaluateCondition(condition);

        if (result) {
            List<Map<String, Object>> thenBlock = (List<Map<String, Object>>) node.get("thenBlock");
            for (Map<String, Object> stmt : thenBlock) {
                execute(stmt);
            }
        } else {
            List<Map<String, Object>> elseBlock = (List<Map<String, Object>>) node.get("elseBlock");
            if (elseBlock != null) {
                for (Map<String, Object> stmt : elseBlock) {
                    execute(stmt);
                }
            }
        }
    }

    // ----------------------
    // Evaluate expressions
    // ----------------------
    private Object evaluate(Map<String, Object> node) {
        String type = (String) node.get("type");

        switch (type) {
            case "Number":
                return node.get("value");
            case "String":
                return node.get("value");
            case "Identifier":
                String name = (String) node.get("name");
                if (!variables.containsKey(name)) {
                    throw new RuntimeException("Variable not found: " + name);
                }
                return variables.get(name);
            case "BinaryOp":
                return evaluateBinaryOp(node);
            default:
                throw new RuntimeException("Unknown node type: " + type);
        }
    }

    private Object evaluateBinaryOp(Map<String, Object> node) {
    String operator = (String) node.get("operator");
    Map<String, Object> leftNode = (Map<String, Object>) node.get("left");
    Map<String, Object> rightNode = (Map<String, Object>) node.get("right");

    double left = toNumber(evaluate(leftNode));
    double right = toNumber(evaluate(rightNode));

    double result;

    switch (operator) {
        case "+": result = left + right; break;
        case "-": result = left - right; break;
        case "*": result = left * right; break;
        case "/": result = left / right; break;
        default: throw new RuntimeException("Unknown operator: " + operator);
    }
    if (result == (int) result) {
        return (int) result;
    }
    return result;
}

    private boolean evaluateCondition(Map<String, Object> node) {
        String operator = (String) node.get("operator");
        Map<String, Object> leftNode = (Map<String, Object>) node.get("left");
        Map<String, Object> rightNode = (Map<String, Object>) node.get("right");

        double left = toNumber(evaluate(leftNode));
        double right = toNumber(evaluate(rightNode));

        switch (operator) {
            case "==": return left == right;
            case "!=": return left != right;
            case ">": return left > right;
            case "<": return left < right;
            case ">=": return left >= right;
            case "<=": return left <= right;
            default: throw new RuntimeException("Unknown condition operator: " + operator);
        }
    }

    private double toNumber(Object value) {
        if (value instanceof Double) return (Double) value;
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot convert to number: " + value);
            }
        }
        throw new RuntimeException("Cannot convert to number: " + value);
    }
}
