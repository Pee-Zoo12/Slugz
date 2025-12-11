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

public class Tokenizer {
    private String source;
    private int pos;
    private int line;
    private int column;
    private List<Token> tokens;
    private Map<String, TokenType> keywords;
    
    public Tokenizer(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        
        // Initialize keywords
        keywords = new HashMap<>();
        keywords.put("BEGIN", TokenType.BEGIN);
        keywords.put("STOP", TokenType.STOP);
        keywords.put("THIS", TokenType.THIS);
        keywords.put("AS", TokenType.AS);
        keywords.put("GIVE", TokenType.GIVE);
        keywords.put("GET", TokenType.GET);
        keywords.put("PRESENT", TokenType.PRESENT);
        keywords.put("IF", TokenType.IF);
        keywords.put("THEN", TokenType.THEN);
        keywords.put("OR", TokenType.OR);
        keywords.put("NT", TokenType.NT);
        keywords.put("FT", TokenType.FT);
        keywords.put("CH", TokenType.CH);
        keywords.put("ST", TokenType.ST);
    }
    
    private Character currentChar() {
        if (pos >= source.length()) {
            return null;
        }
        return source.charAt(pos);
    }
    
    private Character peekChar(int offset) {
        int newPos = pos + offset;
        if (newPos >= source.length()) {
            return null;
        }
        return source.charAt(newPos);
    }
    
    private void advance() {
        if (pos < source.length()) {
            if (source.charAt(pos) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            pos++;
        }
    }
    
    private void skipWhitespace() {
        while (currentChar() != null && Character.isWhitespace(currentChar())) {
            advance();
        }
    }
    
    private Token readNumber() {
        int startLine = line;
        int startCol = column;
        StringBuilder numStr = new StringBuilder();
        
        while (currentChar() != null && (Character.isDigit(currentChar()) || currentChar() == '.')) {
            numStr.append(currentChar());
            advance();
        }
        
        String str = numStr.toString();
        if (str.contains(".")) {
            return new Token(TokenType.NUMBER, Double.parseDouble(str), startLine, startCol);
        } else {
            return new Token(TokenType.NUMBER, Integer.parseInt(str), startLine, startCol);
        }
    }
    
    private Token readString() {
        int startLine = line;
        int startCol = column;
        StringBuilder stringVal = new StringBuilder();
        advance(); // Skip opening quote
        
        while (currentChar() != null && currentChar() != '"') {
            if (currentChar() == '\\') {
                advance();
                if (currentChar() != null) {
                    stringVal.append(currentChar());
                    advance();
                }
            } else {
                stringVal.append(currentChar());
                advance();
            }
        }
        
        if (currentChar() == '"') {
            advance(); // Skip closing quote
        }
        
        return new Token(TokenType.STRING, stringVal.toString(), startLine, startCol);
    }
    
    private Token readIdentifier() {
        int startLine = line;
        int startCol = column;
        StringBuilder ident = new StringBuilder();
        
        while (currentChar() != null && (Character.isLetterOrDigit(currentChar()) || currentChar() == '_')) {
            ident.append(currentChar());
            advance();
        }
        
        String identStr = ident.toString();
        
        // Check for multi-word keywords like "OR ELSE"
        if (identStr.equals("OR") && currentChar() != null && Character.isWhitespace(currentChar())) {
            int savedPos = pos;
            skipWhitespace();
            StringBuilder nextWord = new StringBuilder();
            
            while (currentChar() != null && Character.isLetter(currentChar())) {
                nextWord.append(currentChar());
                advance();
            }
            
            if (nextWord.toString().equals("ELSE")) {
                return new Token(TokenType.OR_ELSE, "OR ELSE", startLine, startCol);
            } else {
                // Restore position
                pos = savedPos;
            }
        }
        
        TokenType tokenType = keywords.getOrDefault(identStr, TokenType.IDENTIFIER);
        return new Token(tokenType, identStr, startLine, startCol);
    }
    
    public List<Token> tokenize() throws Exception {
        while (pos < source.length()) {
            skipWhitespace();
            
            if (pos >= source.length()) {
                break;
            }
            
            char c = currentChar();
            
            // Numbers
            if (Character.isDigit(c)) {
                tokens.add(readNumber());
            }
            // Strings
            else if (c == '"') {
                tokens.add(readString());
            }
            // Identifiers and keywords
            else if (Character.isLetter(c)) {
                tokens.add(readIdentifier());
            }
            // Operators
            else if (c == '>') {
                if (peekChar(1) != null && peekChar(1) == '>') {
                    tokens.add(new Token(TokenType.ASSIGN, ">>", line, column));
                    advance();
                    advance();
                } else if (peekChar(1) != null && peekChar(1) == '=') {
                    tokens.add(new Token(TokenType.GTE, ">=", line, column));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.GT, ">", line, column));
                    advance();
                }
            }
            else if (c == '<') {
                if (peekChar(1) != null && peekChar(1) == '=') {
                    tokens.add(new Token(TokenType.LTE, "<=", line, column));
                    advance();
                    advance();
                } else {
                    tokens.add(new Token(TokenType.LT, "<", line, column));
                    advance();
                }
            }
            else if (c == '=') {
                if (peekChar(1) != null && peekChar(1) == '=') {
                    tokens.add(new Token(TokenType.EQ, "==", line, column));
                    advance();
                    advance();
                } else {
                    throw new Exception("Unexpected character '=' at line " + line + ", column " + column);
                }
            }
            else if (c == '!') {
                if (peekChar(1) != null && peekChar(1) == '=') {
                    tokens.add(new Token(TokenType.NEQ, "!=", line, column));
                    advance();
                    advance();
                } else {
                    throw new Exception("Unexpected character '!' at line " + line + ", column " + column);
                }
            }
            else if (c == '+') {
                tokens.add(new Token(TokenType.PLUS, "+", line, column));
                advance();
            }
            else if (c == '-') {
                tokens.add(new Token(TokenType.MINUS, "-", line, column));
                advance();
            }
            else if (c == '*') {
                tokens.add(new Token(TokenType.MULTIPLY, "*", line, column));
                advance();
            }
            else if (c == '/') {
                tokens.add(new Token(TokenType.DIVIDE, "/", line, column));
                advance();
            }
            else if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "(", line, column));
                advance();
            }
            else if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")", line, column));
                advance();
            }
            else {
                throw new Exception("Unexpected character '" + c + "' at line " + line + ", column " + column);
            }
        }
        
        tokens.add(new Token(TokenType.EOF, null, line, column));
        return tokens;
    }
}
