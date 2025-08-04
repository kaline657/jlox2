package br.ufma.ecp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Lox {
  private static final Interpreter interpreter = new Interpreter();

  public static void main(String[] args) throws IOException {
    runPrompt();
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) break;

      run(line);
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();

    // Só executa se não houve erro
    if (expression != null) {
      interpreter.interpret(expression);
    }
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " no final", message);
    } else {
      report(token.line, " no '" + token.lexeme + "'", message);
    }
  }

  private static void report(int line, String where, String message) {
    System.err.println("[linha " + line + "] Erro" + where + ": " + message);
  }
}
