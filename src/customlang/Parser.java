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
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int pos;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }
    
    private Token currentToken() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return tokens.get(tokens.size() - 1); // Return EOF
    }
    
    private Token peekToken(int offset) {
        int newPos = pos + offset;
        if (newPos < tokens.size()) {
            return tokens.get(newPos);
        }
        return tokens.get(tokens.size() - 1); // Return EOF
    }
    
    private void advance() {
        if (pos < tokens.size() - 1) {
            pos++;
        }
    }
    
    private Token expect(TokenType type) throws Exception {
        if (currentToken().getType() != type) {
            throw new Exception("Expected " + type + ", got " + currentToken().getType() + 
                              " at line " + currentToken().getLine());
        }
        Token token = currentToken();
        advance();
        return token;
    }
    
    public Program parse() throws Exception {
        expect(TokenType.BEGIN);
        List<ASTNode> statements = new ArrayList<>();
        
        while (currentToken().getType() != TokenType.STOP) {
            if (currentToken().getType() == TokenType.EOF) {
                throw new Exception("Expected STOP before end of file");
            }
            statements.add(parseStatement());
        }
        
        expect(TokenType.STOP);
        return new Program(statements);
    }
    
    private ASTNode parseStatement() throws Exception {
        Token token = currentToken();
        
        switch (token.getType()) {
            case THIS:
                return parseVarDeclaration();
            case IDENTIFIER:
                return parseAssignment();
            case GIVE:
                return parseInput();
            case PRESENT:
                return parsePrint();
            case IF:
                return parseIf();
            default:
                throw new Exception("Unexpected token " + token.getType() + " at line " + token.getLine());
        }
    }
    
    private VarDeclaration parseVarDeclaration() throws Exception {
        expect(TokenType.THIS);
        String name = (String) expect(TokenType.IDENTIFIER).getValue();
        expect(TokenType.AS);
        
        Token typeToken = currentToken();
        if (typeToken.getType() != TokenType.NT && typeToken.getType() != TokenType.FT &&
            typeToken.getType() != TokenType.CH && typeToken.getType() != TokenType.ST) {
            throw new Exception("Expected type (NT, FT, CH, ST) at line " + typeToken.getLine());
        }
        
        String varType = (String) typeToken.getValue();
        advance();
        return new VarDeclaration(name, varType);
    }
    
    private Assignment parseAssignment() throws Exception {
        String name = (String) expect(TokenType.IDENTIFIER).getValue();
        expect(TokenType.ASSIGN);
        ASTNode expression = parseExpression();
        return new Assignment(name, expression);
    }
    
    private InputStmt parseInput() throws Exception {
        expect(TokenType.GIVE);
        String prompt = (String) expect(TokenType.STRING).getValue();
        expect(TokenType.GET);
        String varName = (String) expect(TokenType.IDENTIFIER).getValue();
        return new InputStmt(prompt, varName);
    }
    
    private PrintStmt parsePrint() throws Exception {
        expect(TokenType.PRESENT);
        
        ASTNode value;
        if (currentToken().getType() == TokenType.STRING) {
            value = new StringNode((String) currentToken().getValue());
            advance();
        } else if (currentToken().getType() == TokenType.IDENTIFIER) {
            value = new Identifier((String) currentToken().getValue());
            advance();
        } else {
            value = parseExpression();
        }
        
        return new PrintStmt(value);
    }
    
    private IfStmt parseIf() throws Exception {
        expect(TokenType.IF);
        
        List<ASTNode> conditions = new ArrayList<>();
        List<List<ASTNode>> thenBlocks = new ArrayList<>();
        
        // First IF condition
        expect(TokenType.LPAREN);
        ASTNode condition = parseCondition();
        expect(TokenType.RPAREN);
        expect(TokenType.THEN);
        
        conditions.add(condition);
        List<ASTNode> thenBlock = new ArrayList<>();
        
        while (currentToken().getType() != TokenType.OR_ELSE && 
               currentToken().getType() != TokenType.OR && 
               currentToken().getType() != TokenType.STOP) {
            thenBlock.add(parseStatement());
        }
        
        thenBlocks.add(thenBlock);
        
        // OR ELSE clauses
        while (currentToken().getType() == TokenType.OR_ELSE) {
            advance(); // OR_ELSE
            expect(TokenType.LPAREN);
            condition = parseCondition();
            expect(TokenType.RPAREN);
            expect(TokenType.THEN);
            
            conditions.add(condition);
            thenBlock = new ArrayList<>();
            
            while (currentToken().getType() != TokenType.OR_ELSE && 
                   currentToken().getType() != TokenType.OR && 
                   currentToken().getType() != TokenType.STOP) {
                thenBlock.add(parseStatement());
            }
            
            thenBlocks.add(thenBlock);
        }
        
        // OR (else) clause
        List<ASTNode> elseBlock = null;
        if (currentToken().getType() == TokenType.OR) {
            advance();
            elseBlock = new ArrayList<>();
            
            while (currentToken().getType() != TokenType.STOP) {
                elseBlock.add(parseStatement());
            }
        }
        
        expect(TokenType.STOP);
        return new IfStmt(conditions, thenBlocks, elseBlock);
    }
    
    private Condition parseCondition() throws Exception {
        ASTNode left = parseExpression();
        
        String operator = (String) currentToken().getValue();
        if (currentToken().getType() != TokenType.EQ && currentToken().getType() != TokenType.NEQ &&
            currentToken().getType() != TokenType.GT && currentToken().getType() != TokenType.LT &&
            currentToken().getType() != TokenType.GTE && currentToken().getType() != TokenType.LTE) {
            throw new Exception("Expected relational operator at line " + currentToken().getLine());
        }
        
        advance();
        ASTNode right = parseExpression();
        
        return new Condition(left, operator, right);
    }
    
    private ASTNode parseExpression() throws Exception {
        ASTNode left = parseTerm();
        
        while (currentToken().getType() == TokenType.PLUS || currentToken().getType() == TokenType.MINUS) {
            String operator = (String) currentToken().getValue();
            advance();
            ASTNode right = parseTerm();
            left = new BinaryOp(left, operator, right);
        }
        
        return left;
    }
    
    private ASTNode parseTerm() throws Exception {
        ASTNode left = parseFactor();
        
        while (currentToken().getType() == TokenType.MULTIPLY || currentToken().getType() == TokenType.DIVIDE) {
            String operator = (String) currentToken().getValue();
            advance();
            ASTNode right = parseFactor();
            left = new BinaryOp(left, operator, right);
        }
        
        return left;
    }
    
    private ASTNode parseFactor() throws Exception {
        Token token = currentToken();
        
        if (token.getType() == TokenType.NUMBER) {
            advance();
            return new NumberNode(token.getValue());
        } else if (token.getType() == TokenType.IDENTIFIER) {
            advance();
            return new Identifier((String) token.getValue());
        } else if (token.getType() == TokenType.LPAREN) {
            advance();
            ASTNode expr = parseExpression();
            expect(TokenType.RPAREN);
            return expr;
        } else {
            throw new Exception("Unexpected token " + token.getType() + " in expression at line " + token.getLine());
        }
    }
}