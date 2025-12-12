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

public class Tokenizer {
    private String code;
    private int pos = 0;
    
    public Tokenizer(String code) {
        this.code = code;
    }
    
    public List<String> tokenize() {
        List<String> tokens = new ArrayList<>();
        
        while (pos < code.length()) {
            char ch = code.charAt(pos);
            
            // Skip whitespace
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                pos++;
                continue;
            }
            
            // String literals
            if (ch == '"') {
                tokens.add(readString());
                continue;
            }
            
            // Numbers
            if (Character.isDigit(ch)) {
                tokens.add(readNumber());
                continue;
            }
            
            // Identifiers and keywords
            if (Character.isLetter(ch)) {
                tokens.add(readWord());
                continue;
            }
            
            // Two-character operators
            if (pos + 1 < code.length()) {
                String twoChar = code.substring(pos, pos + 2);
                if (twoChar.equals(">>") || twoChar.equals("==") || 
                    twoChar.equals("!=") || twoChar.equals(">=") || twoChar.equals("<=")) {
                    tokens.add(twoChar);
                    pos += 2;
                    continue;
                }
            }
            
            // Single character operators
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/' ||
                ch == '>' || ch == '<' || ch == '(' || ch == ')') {
                tokens.add(String.valueOf(ch));
                pos++;
                continue;
            }
            
            pos++; // Skip unknown character
        }
        
        return tokens;
    }
    
    private String readString() {
        StringBuilder sb = new StringBuilder();
        pos++; // Skip opening quote
        
        while (pos < code.length() && code.charAt(pos) != '"') {
            sb.append(code.charAt(pos));
            pos++;
        }
        pos++; // Skip closing quote
        
        return "\"" + sb.toString() + "\"";
    }
    
    private String readNumber() {
        StringBuilder sb = new StringBuilder();
        
        while (pos < code.length() && (Character.isDigit(code.charAt(pos)) || code.charAt(pos) == '.')) {
            sb.append(code.charAt(pos));
            pos++;
        }
        
        return sb.toString();
    }
    
    private String readWord() {
        StringBuilder sb = new StringBuilder();
        
        while (pos < code.length() && (Character.isLetterOrDigit(code.charAt(pos)) || code.charAt(pos) == '_')) {
            sb.append(code.charAt(pos));
            pos++;
        }
        
        return sb.toString();
    }
}