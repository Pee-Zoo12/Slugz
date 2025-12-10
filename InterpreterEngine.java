
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jez
 */
public class InterpreterEngine {
    // --- 1. INTEGRATION ENTRY POINT ---
    
    /**
     * Call this method from your GUI "Run" button.
     * @param sourceCode The raw text from the text editor.
     * @return The output of the program or error messages.
     */
    public String runCode(String sourceCode) {
        StringBuilder outputBuffer = new StringBuilder();
        try {
            // Step 1: Lexing (Tokenization)
            Lexer lexer = new Lexer(sourceCode);
            java.util.List<Token> tokens = lexer.scanTokens();

            // Step 2: Parsing (AST Generation)
            Parser parser = new Parser(tokens);
            java.util.List<Stmt> statements = parser.parse();

            // Step 3: Interpreting (Execution)
            Interpreter interpreter = new Interpreter(outputBuffer);
            interpreter.interpret(statements);

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return outputBuffer.toString();
    }

    // --- 2. TOKEN DEFINITIONS ---

    private enum TokenType {
        // Keywords
        SET, PRINT, IF, THEN, ELSE, ENDIF, WHILE, DO, ENDWHILE,
        // Literals & Identifiers
        IDENTIFIER, STRING, NUMBER,
        // Operators
        PLUS, MINUS, STAR, SLASH, EQUAL, GT, LT,
        // End of File
        EOF
    }

    private static class Token {
        final TokenType type;
        final String lexeme;
        final Object literal;

        Token(TokenType type, String lexeme, Object literal) {
            this.type = type;
            this.lexeme = lexeme;
            this.literal = literal;
        }

        @Override
        public String toString() { return type + " " + lexeme; }
    }

    // --- 3. LEXER (Tokenizer) ---

    private static class Lexer {
        private final String source;
        private final java.util.List<Token> tokens = new java.util.ArrayList<Token>();
        private int start = 0;
        private int current = 0;

        private static final Map<String, TokenType> keywords;

        static {
            keywords = new HashMap<String, TokenType>();
            keywords.put("SET", TokenType.SET);
            keywords.put("PRINT", TokenType.PRINT);
            keywords.put("IF", TokenType.IF);
            keywords.put("THEN", TokenType.THEN);
            keywords.put("ELSE", TokenType.ELSE);
            keywords.put("ENDIF", TokenType.ENDIF);
            keywords.put("WHILE", TokenType.WHILE);
            keywords.put("DO", TokenType.DO);
            keywords.put("ENDWHILE", TokenType.ENDWHILE);
        }

        Lexer(String source) {
            this.source = source;
        }

        java.util.List<Token> scanTokens() {
            while (!isAtEnd()) {
                start = current;
                scanToken();
            }
            tokens.add(new Token(TokenType.EOF, "", null));
            return tokens;
        }

        private void scanToken() {
            char c = advance();
            switch (c) {
                case ' ': case '\r': case '\t': case '\n': break; // Ignore whitespace
                case '+': addToken(TokenType.PLUS); break;
                case '-': addToken(TokenType.MINUS); break;
                case '*': addToken(TokenType.STAR); break;
                case '/': addToken(TokenType.SLASH); break;
                case '=': addToken(TokenType.EQUAL); break;
                case '>': addToken(TokenType.GT); break;
                case '<': addToken(TokenType.LT); break;
                case '"': string(); break;
                default:
                    if (Character.isDigit(c)) {
                        number();
                    } else if (Character.isLetter(c)) {
                        identifier();
                    } else {
                        throw new RuntimeException("Unexpected character: " + c);
                    }
                    break;
            }
        }

        private void string() {
            while (peek() != '"' && !isAtEnd()) advance();
            if (isAtEnd()) throw new RuntimeException("Unterminated string.");
            advance(); // The closing "
            String value = source.substring(start + 1, current - 1);
            addToken(TokenType.STRING, value);
        }

        private void number() {
            while (Character.isDigit(peek())) advance();
            int value = Integer.parseInt(source.substring(start, current));
            addToken(TokenType.NUMBER, value);
        }

        private void identifier() {
            while (Character.isLetterOrDigit(peek())) advance();
            String text = source.substring(start, current);
            TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
            addToken(type);
        }

        private boolean isAtEnd() { return current >= source.length(); }
        private char advance() { return source.charAt(current++); }
        private char peek() { return isAtEnd() ? '\0' : source.charAt(current); }
        private void addToken(TokenType type) { addToken(type, null); }
        private void addToken(TokenType type, Object literal) {
            String text = source.substring(start, current);
            tokens.add(new Token(type, text, literal));
        }
    }

    // --- 4. AST NODES (Abstract Syntax Tree) ---

    private interface Stmt {
        <R> R accept(Visitor<R> visitor);
    }
    
    private interface Expr {
        <R> R accept(Visitor<R> visitor);
    }

    // Visitor pattern to separate logic from data
    private interface Visitor<R> {
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
        R visitIfStmt(If stmt);
        R visitWhileStmt(While stmt);
        R visitBinaryExpr(Binary expr);
        R visitLiteralExpr(Literal expr);
        R visitVariableExpr(Variable expr);
    }

    // Statement Nodes
    private static class Print implements Stmt {
        final Expr expression;
        Print(Expr expression) { this.expression = expression; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitPrintStmt(this); }
    }

    private static class Var implements Stmt {
        final Token name;
        final Expr initializer;
        Var(Token name, Expr initializer) { this.name = name; this.initializer = initializer; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitVarStmt(this); }
    }

    private static class If implements Stmt {
        final Expr condition;
        final java.util.List<Stmt> thenBranch;
        final java.util.List<Stmt> elseBranch;
        If(Expr condition, java.util.List<Stmt> thenBranch, java.util.List<Stmt> elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitIfStmt(this); }
    }

    private static class While implements Stmt {
        final Expr condition;
        final java.util.List<Stmt> body;
        While(Expr condition, java.util.List<Stmt> body) {
            this.condition = condition;
            this.body = body;
        }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitWhileStmt(this); }
    }

    // Expression Nodes
    private static class Binary implements Expr {
        final Expr left;
        final Token operator;
        final Expr right;
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitBinaryExpr(this); }
    }

    private static class Literal implements Expr {
        final Object value;
        Literal(Object value) { this.value = value; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitLiteralExpr(this); }
    }

    private static class Variable implements Expr {
        final Token name;
        Variable(Token name) { this.name = name; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitVariableExpr(this); }
    }

    // --- 5. PARSER ---

    private static class Parser {
        private final java.util.List<Token> tokens;
        private int current = 0;

        Parser(java.util.List<Token> tokens) { this.tokens = tokens; }

        java.util.List<Stmt> parse() {
            java.util.List<Stmt> statements = new java.util.ArrayList<Stmt>();
            while (!isAtEnd()) {
                statements.add(statement());
            }
            return statements;
        }

        private Stmt statement() {
            if (match(TokenType.PRINT)) return printStatement();
            if (match(TokenType.SET)) return varDeclaration();
            if (match(TokenType.IF)) return ifStatement();
            if (match(TokenType.WHILE)) return whileStatement();
            throw new RuntimeException("Expect statement at " + peek().lexeme);
        }

        private Stmt printStatement() {
            Expr value = expression();
            return new Print(value);
        }

        private Stmt varDeclaration() {
            Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
            consume(TokenType.EQUAL, "Expect '=' after variable name.");
            Expr initializer = expression();
            return new Var(name, initializer);
        }
        
        // Implements Selection control structure
        private Stmt ifStatement() {
            Expr condition = expression();
            consume(TokenType.THEN, "Expect 'THEN' after if condition.");
            
            java.util.List<Stmt> thenBranch = new java.util.ArrayList<Stmt>();
            while (!check(TokenType.ELSE) && !check(TokenType.ENDIF) && !isAtEnd()) {
                thenBranch.add(statement());
            }

            java.util.List<Stmt> elseBranch = new java.util.ArrayList<Stmt>();
            if (match(TokenType.ELSE)) {
                while (!check(TokenType.ENDIF) && !isAtEnd()) {
                    elseBranch.add(statement());
                }
            }
            consume(TokenType.ENDIF, "Expect 'ENDIF' after if block.");
            return new If(condition, thenBranch, elseBranch);
        }

        // Implements Repetition control structure
        private Stmt whileStatement() {
            Expr condition = expression();
            consume(TokenType.DO, "Expect 'DO' after while condition.");
            java.util.List<Stmt> body = new java.util.ArrayList<Stmt>();
            while (!check(TokenType.ENDWHILE) && !isAtEnd()) {
                body.add(statement());
            }
            consume(TokenType.ENDWHILE, "Expect 'ENDWHILE' after loop body.");
            return new While(condition, body);
        }

        // Expression Parsing (Order of Operations)
        private Expr expression() { return comparison(); }

        private Expr comparison() {
            Expr expr = term();
            while (match(TokenType.GT, TokenType.LT)) {
                Token operator = previous();
                Expr right = term();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }

        private Expr term() {
            Expr expr = factor();
            while (match(TokenType.MINUS, TokenType.PLUS)) {
                Token operator = previous();
                Expr right = factor();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }

        private Expr factor() {
            Expr expr = primary();
            while (match(TokenType.SLASH, TokenType.STAR)) {
                Token operator = previous();
                Expr right = primary();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }

        private Expr primary() {
            if (match(TokenType.NUMBER, TokenType.STRING)) return new Literal(previous().literal);
            if (match(TokenType.IDENTIFIER)) return new Variable(previous());
            throw new RuntimeException("Expect expression.");
        }

        // Helpers
        private boolean match(TokenType... types) {
            for (TokenType type : types) {
                if (check(type)) { advance(); return true; }
            }
            return false;
        }
        private Token consume(TokenType type, String message) {
            if (check(type)) return advance();
            throw new RuntimeException(message);
        }
        private boolean check(TokenType type) {
            if (isAtEnd()) return false;
            return peek().type == type;
        }
        private Token advance() {
            if (!isAtEnd()) current++;
            return previous();
        }
        private boolean isAtEnd() { return peek().type == TokenType.EOF; }
        private Token peek() { return tokens.get(current); }
        private Token previous() { return tokens.get(current - 1); }
    }

    // --- 6. INTERPRETER (Execution Engine) ---

    private static class Interpreter implements Visitor<Object> {
        private final StringBuilder output;
        private final Map<String, Object> environment = new HashMap<String, Object>();

        Interpreter(StringBuilder output) {
            this.output = output;
        }

        void interpret(java.util.List<Stmt> statements) {
            // Implements Sequence control structure
            for (Stmt stmt : statements) {
                execute(stmt);
            }
        }

        private void execute(Stmt stmt) {
            stmt.accept(this);
        }

        @Override
        public Object visitPrintStmt(Print stmt) {
            Object value = evaluate(stmt.expression);
            output.append(value.toString()).append("\n");
            return null;
        }

        @Override
        public Object visitVarStmt(Var stmt) {
            Object value = evaluate(stmt.initializer);
            environment.put(stmt.name.lexeme, value); // Stores variable
            return null;
        }

        @Override
        public Object visitIfStmt(If stmt) {
            // Selection Logic
            if (isTruthy(evaluate(stmt.condition))) {
                for (Stmt s : stmt.thenBranch) execute(s);
            } else if (stmt.elseBranch != null) {
                for (Stmt s : stmt.elseBranch) execute(s);
            }
            return null;
        }

        @Override
        public Object visitWhileStmt(While stmt) {
            // Repetition Logic
            while (isTruthy(evaluate(stmt.condition))) {
                for (Stmt s : stmt.body) execute(s);
            }
            return null;
        }

        @Override
        public Object visitBinaryExpr(Binary expr) {
            Object left = evaluate(expr.left);
            Object right = evaluate(expr.right);

            if (left instanceof Integer && right instanceof Integer) {
                int l = (int) left;
                int r = (int) right;
                switch (expr.operator.type) {
                    case PLUS: return l + r;
                    case MINUS: return l - r;
                    case STAR: return l * r;
                    case SLASH: return l / r;
                    case GT: return l > r;
                    case LT: return l < r;
                }
            }
            // String concatenation
            if (expr.operator.type == TokenType.PLUS) {
                return left.toString() + right.toString();
            }
            return null;
        }

        @Override
        public Object visitLiteralExpr(Literal expr) {
            return expr.value;
        }

        @Override
        public Object visitVariableExpr(Variable expr) {
            if (environment.containsKey(expr.name.lexeme)) {
                return environment.get(expr.name.lexeme);
            }
            throw new RuntimeException("Undefined variable '" + expr.name.lexeme + "'.");
        }

        private Object evaluate(Expr expr) {
            return expr.accept(this);
        }

        private boolean isTruthy(Object object) {
            if (object == null) return false;
            if (object instanceof Boolean) return (boolean) object;
            return true;
        }
    }
    
}
