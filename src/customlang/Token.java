/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customlang;

public class Token {
    private TokenType type;
    private Object value;
    private int line;
    private int column;
    
    public Token(TokenType type, Object value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return "Token{" + type + ", '" + value + "', line=" + line + ", col=" + column + "}";
    }
}

enum TokenType {
    // Keywords
    BEGIN, STOP, THIS, AS, GIVE, GET, PRESENT, IF, THEN, OR_ELSE, OR,
    
    // Data types
    NT, FT, CH, ST,
    
    // Operators
    ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE,
    
    // Relational operators
    EQ, NEQ, GT, LT, GTE, LTE,
    
    // Delimiters
    LPAREN, RPAREN,
    
    // Literals
    NUMBER, STRING, IDENTIFIER,
    
    // Special
    EOF, NEWLINE
}
