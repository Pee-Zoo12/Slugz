/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customlang;



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
            if (Character.isWhitespace(ch)) { pos++; continue; }

            // String literals
            if (ch == '"') { tokens.add(readString()); continue; }

            // Numbers
            if (Character.isDigit(ch)) { tokens.add(readNumber()); continue; }

            // Identifiers and keywords
            if (Character.isLetter(ch)) { tokens.add(readWordOrKeyword()); continue; }

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

            // Single-character operators
            if ("+-*/><=(){}".contains(String.valueOf(ch))) {
                tokens.add(String.valueOf(ch));
                pos++;
                continue;
            }

            pos++; // skip unknown
        }

        return tokens;
    }

    private String readString() {
        StringBuilder sb = new StringBuilder();
        pos++; // skip opening quote
        while (pos < code.length() && code.charAt(pos) != '"') {
            sb.append(code.charAt(pos));
            pos++;
        }
        pos++; // skip closing quote
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

    private String readWordOrKeyword() {
        StringBuilder sb = new StringBuilder();
        while (pos < code.length() && (Character.isLetterOrDigit(code.charAt(pos)) || code.charAt(pos) == '_')) {
            sb.append(code.charAt(pos));
            pos++;
        }

        String word = sb.toString();

        // Check for OR ELSE: merge two words
        if (word.equals("OR")) {
            int temp = pos;
            while (temp < code.length() && Character.isWhitespace(code.charAt(temp))) temp++;
            if (temp + 3 <= code.length() && code.substring(temp, temp + 4).equals("ELSE")) {
                pos = temp + 4; // consume ELSE
                word = "OR ELSE";
            }
        }

        return word;
    }
}
