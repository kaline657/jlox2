package br.ufma.ecp;

import java.util.ArrayList;
import java.util.List;

import static br.ufma.ecp.TokenType.COMMA;
import static br.ufma.ecp.TokenType.DOT;
import static br.ufma.ecp.TokenType.EOF;
import static br.ufma.ecp.TokenType.LEFT_BRACE;
import static br.ufma.ecp.TokenType.LEFT_PAREN;
import static br.ufma.ecp.TokenType.MINUS;
import static br.ufma.ecp.TokenType.PLUS;
import static br.ufma.ecp.TokenType.RIGHT_BRACE;
import static br.ufma.ecp.TokenType.RIGHT_PAREN;
import static br.ufma.ecp.TokenType.SEMICOLON;
import static br.ufma.ecp.TokenType.STAR;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();

  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      default:
        Lox.error(line, "Caractere inesperado.");
        break;
    }
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}


