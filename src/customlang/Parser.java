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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private List<String> tokens;
    private int pos = 0;

    public Parser(List<String> tokens) {
        this.tokens = tokens;
    }

    private String current() {
        if (pos < tokens.size()) return tokens.get(pos);
        return "";
    }

    private String next() {
        String token = current();
        pos++;
        return token;
    }

    private void expect(String expected) {
        String token = next();
        if (!token.equals(expected)) {
            throw new RuntimeException("Expected '" + expected + "' but got '" + token + "'");
        }
    }

    private String peekNext() {
        if (pos + 1 < tokens.size()) return tokens.get(pos + 1);
        return "";
    }

    public Map<String, Object> parse() {
        expect("BEGIN");
        List<Map<String, Object>> statements = new ArrayList<>();
        while (!current().equals("STOP")) {
            statements.add(parseStatement());
        }
        expect("STOP");

        Map<String, Object> program = new HashMap<>();
        program.put("type", "Program");
        program.put("statements", statements);
        return program;
    }

    private Map<String, Object> parseStatement() {
        String token = current();

        if (token.equals("THIS")) return parseVarDeclaration();
        else if (token.equals("PRESENT")) return parsePrint();
        else if (token.equals("GIVE")) return parseInput();
        else if (token.equals("IF")) return parseIf();
        else if (isIdentifier(token) && peekNext().equals(">>")) return parseAssignment();

        throw new RuntimeException("Unexpected token: " + token);
    }

    private Map<String, Object> parseVarDeclaration() {
        expect("THIS");
        String name = next();
        expect("AS");
        String type = next();

        Map<String, Object> node = new HashMap<>();
        node.put("type", "VarDeclaration");
        node.put("name", name);
        node.put("varType", type);
        return node;
    }

    private Map<String, Object> parseAssignment() {
        String name = next();
        expect(">>");
        Map<String, Object> value = parseExpression();

        Map<String, Object> node = new HashMap<>();
        node.put("type", "Assignment");
        node.put("name", name);
        node.put("value", value);
        return node;
    }

    private Map<String, Object> parsePrint() {
        expect("PRESENT");
        Map<String, Object> value;
        if (current().startsWith("\"")) {
            String str = next();
            value = new HashMap<>();
            value.put("type", "String");
            value.put("value", str.substring(1, str.length() - 1));
        } else {
            value = parseExpression();
        }

        Map<String, Object> node = new HashMap<>();
        node.put("type", "Print");
        node.put("value", value);
        return node;
    }

    private Map<String, Object> parseInput() {
        expect("GIVE");
        String prompt = next();
        expect("GET");
        String varName = next();

        Map<String, Object> node = new HashMap<>();
        node.put("type", "Input");
        node.put("prompt", prompt.substring(1, prompt.length() - 1));
        node.put("varName", varName);
        return node;
    }

    private Map<String, Object> parseIf() {
    expect("IF");
    expect("(");
    Map<String, Object> condition = parseCondition();
    expect(")");
    expect("THEN");

    List<Map<String, Object>> thenBlock = new ArrayList<>();
    while (!current().equals("OR") && !current().equals("OR ELSE") && !current().equals("STOP")) {
        thenBlock.add(parseStatement());
    }

    List<Map<String, Object>> elseBlock = null;

    while (current().equals("OR") || current().equals("OR ELSE")) {
        String token = next(); // consume the token

        if (token.equals("OR ELSE")) {
            expect("(");
            Map<String, Object> elifCondition = parseCondition();
            expect(")");
            expect("THEN");

            List<Map<String, Object>> elifBlock = new ArrayList<>();
            while (!current().equals("OR") && !current().equals("OR ELSE") && !current().equals("STOP")) {
                elifBlock.add(parseStatement());
            }

            Map<String, Object> elifNode = new HashMap<>();
            elifNode.put("type", "If");
            elifNode.put("condition", elifCondition);
            elifNode.put("thenBlock", elifBlock);
            elifNode.put("elseBlock", null);

            if (elseBlock == null) elseBlock = new ArrayList<>();
            elseBlock.add(elifNode);

        } else if (token.equals("OR")) { // default else
            elseBlock = new ArrayList<>();
            while (!current().equals("STOP")) {
                elseBlock.add(parseStatement());
            }
        }
    }

    expect("STOP");

    Map<String, Object> node = new HashMap<>();
    node.put("type", "If");
    node.put("condition", condition);
    node.put("thenBlock", thenBlock);
    node.put("elseBlock", elseBlock);

    return node;
}


    private Map<String, Object> parseCondition() {
        Map<String, Object> left = parseExpression();
        String operator = next();
        Map<String, Object> right = parseExpression();

        Map<String, Object> node = new HashMap<>();
        node.put("type", "Condition");
        node.put("left", left);
        node.put("operator", operator);
        node.put("right", right);
        return node;
    }

    // ---------------- EXPRESSION PARSING ----------------
    private Map<String, Object> parseExpression() {
        Map<String, Object> left = parseTerm();
        while (current().equals("+") || current().equals("-")) {
            String op = next();
            Map<String, Object> right = parseTerm();
            Map<String, Object> node = new HashMap<>();
            node.put("type", "BinaryOp");
            node.put("operator", op);
            node.put("left", left);
            node.put("right", right);
            left = node;
        }
        return left;
    }

    private Map<String, Object> parseTerm() {
        Map<String, Object> left = parseFactor();
        while (current().equals("*") || current().equals("/")) {
            String op = next();
            Map<String, Object> right = parseFactor();
            Map<String, Object> node = new HashMap<>();
            node.put("type", "BinaryOp");
            node.put("operator", op);
            node.put("left", left);
            node.put("right", right);
            left = node;
        }
        return left;
    }

    private Map<String, Object> parseFactor() {
        String token = current();
        if (token.equals("(")) {
            next();
            Map<String, Object> expr = parseExpression();
            expect(")");
            return expr;
        }
        if (isNumber(token)) {
            next();
            Map<String, Object> node = new HashMap<>();
            node.put("type", "Number");
            node.put("value", Double.parseDouble(token));
            return node;
        }
        if (isIdentifier(token)) {
            next();
            Map<String, Object> node = new HashMap<>();
            node.put("type", "Identifier");
            node.put("name", token);
            return node;
        }
        throw new RuntimeException("Unexpected token in expression: " + token);
    }

    private boolean isNumber(String s) {
        try { Double.parseDouble(s); return true; } 
        catch (NumberFormatException e) { return false; }
    }

    private boolean isIdentifier(String s) {
        return s.length() > 0 && Character.isLetter(s.charAt(0));
    }
}
