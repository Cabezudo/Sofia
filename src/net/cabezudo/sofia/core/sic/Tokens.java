package net.cabezudo.sofia.core.sic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.core.sic.exceptions.EmptyQueueException;
import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class Tokens implements Iterable<Token> {

  private Position position;
  private final List<Token> list = new ArrayList<>();
  private final Queue<Token> queue = new LinkedList<>();
  private Token lastToken;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    list.forEach((token) -> {
      sb.append(token.toString()).append("\n");
    });
    return sb.toString();
  }

  public JSONArray toJSON() {
    JSONArray jsonTokens = new JSONArray();
    list.forEach((token) -> {
      jsonTokens.add(token.toJSON());
    });
    return jsonTokens;
  }

  public String toCode() {
    StringBuilder sb = new StringBuilder();
    list.forEach((token) -> {
      sb.append(token.getValue());
    });
    return sb.toString();
  }

  public boolean add(Token token) {
    if (token == null || token.isEmpty()) {
      return false;
    }

    if (queue.isEmpty()) {
      position = token.getPosition();
    }

    list.add(token);
    return queue.offer(token);
  }

  public Token element() {
    return queue.element();
  }

  public Token peek() throws EmptyQueueException {
    return queue.peek();
  }

  public Position getPosition() {
    return position;
  }

  boolean hasNext() {
    return queue.size() > 0;
  }

  public Token consume() throws EmptyQueueException {
    Token token = queue.poll();
    if (token == null) {
      lastToken.setError(true);
      throw new EmptyQueueException(lastToken.getEndPosition());
    }
    lastToken = token;
    return token;
  }

  @Override
  public Iterator<Token> iterator() {
    return list.iterator();
  }
}
