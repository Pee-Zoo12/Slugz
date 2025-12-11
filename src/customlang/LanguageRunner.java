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
import java.util.Map;
import java.util.function.Consumer;

public class LanguageRunner {

    // --- IO Callback Interface Here ---
    public interface IOCallback {
        void print(String text);
        String input(String prompt);
    }

    private Tokenizer tokenizer;
    private Parser parser;
    private Interpreter interpreter;
    private String lastError;
    private IOCallback ioCallback;

    public LanguageRunner() {
        this.interpreter = new Interpreter();
        this.lastError = null;
    }

    // Set IO callback (optional)
    public void setIOCallback(IOCallback callback) {
        this.ioCallback = callback;
        interpreter.setIOCallback(callback);
    }

    public boolean run(String sourceCode) {
        return run(sourceCode, null);
    }

    public boolean run(String sourceCode, Consumer<String> outputCallback) {
        try {
    lastError = null;

    // Tokenize
    tokenizer = new Tokenizer(sourceCode);
    List<Token> tokens = tokenizer.tokenize();

    // Parse
    parser = new Parser(tokens);
    Program ast = parser.parse();

    // Interpret
    interpreter.reset();
    interpreter.execute(ast);

    return true;

} catch (Exception e) {
    lastError = e.getMessage();

    if (outputCallback != null) {
        outputCallback.accept("Error: " + e.getMessage());
    } else {
        System.err.println("Error: " + e.getMessage());
    }

    return false;
}

    }

    public String getLastError() {
        return lastError;
    }

    public Map<String, Object> getVariables() {
        return interpreter.getVariables();
    }
}
