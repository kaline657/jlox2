package br.ufma.ecp;

import java.util.ArrayList;
import java.util.List;

import static br.ufma.ecp.TokenType.BANG;
import static br.ufma.ecp.TokenType.BANG_EQUAL;
import static br.ufma.ecp.TokenType.COMMA;
import static br.ufma.ecp.TokenType.DOT;
import static br.ufma.ecp.TokenType.EOF;
import static br.ufma.ecp.TokenType.EQUAL;
import static br.ufma.ecp.TokenType.EQUAL_EQUAL;
import static br.ufma.ecp.TokenType.GREATER;
import static br.ufma.ecp.TokenType.GREATER_EQUAL;
import static br.ufma.ecp.TokenType.LEFT_BRACE;
import static br.ufma.ecp.TokenType.LEFT_PAREN;
import static br.ufma.ecp.TokenType.LESS;
import static br.ufma.ecp.TokenType.LESS_EQUAL;
import static br.ufma.ecp.TokenType.MINUS;
import static br.ufma.ecp.TokenType.NUMBER;
import static br.ufma.ecp.TokenType.PLUS;
import static br.ufma.ecp.TokenType.RIGHT_BRACE;
import static br.ufma.ecp.TokenType.RIGHT_PAREN;
import static br.ufma.ecp.TokenType.SEMICOLON;
import static br.ufma.ecp.TokenType.SLASH;
import static br.ufma.ecp.TokenType.STAR;
import static br.ufma.ecp.TokenType.STRING;

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
      case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
      case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
      case '/':
        if (match('/')) {
          // Comentário até o fim da linha
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;
      case ' ':
      case '\r':
      case '\t':
        // Ignorar espaços
        break;
      case '\n':
        line++;
        break;
      case '"':
        string();
        break;
      default:
        if (isDigit(c)) {
          number();
        } else {
          Lox.error(line, "Caractere inesperado.");
        }
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

  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "String não terminada.");
      return;
    }

    advance(); // fecha aspas

    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private void number() {
    while (isDigit(peek())) advance();

    if (peek() == '.' && isDigit(peekNext())) {
      advance(); // Consome o ponto

      while (isDigit(peek())) advance();
    }

    String value = source.substring(start, current);
    addToken(NUMBER, Double.parseDouble(value));
  }
}