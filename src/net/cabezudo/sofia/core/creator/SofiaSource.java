package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
abstract class SofiaSource {

  private final Caller caller;
  private Path basePath;
  private Path partialPath;
  private Path path;

  SofiaSource(Caller caller) {
    this.caller = caller;
  }

  void setPaths(Path basePath, Path partialPath) {
    this.basePath = basePath;
    this.partialPath = partialPath;
    if (partialPath != null) {
      if (partialPath.isAbsolute()) {
        throw new RuntimeException(partialPath + " is an absolute path.");
      }
      this.path = basePath.resolve(partialPath);
    } else {
      this.path = null;
    }
  }

  Path getBasePath() {
    return basePath;
  }

  Path getPartialPath() {
    return partialPath;
  }

  public enum Type {
    HTML, JS, CSS, JSON
  }

  private final Lines lines = new Lines();

  Caller getCaller() {
    return caller;
  }

  void load() throws IOException {
    Logger.debug("Loadding %s source file.", partialPath);
    List<String> ls = Files.readAllLines(path);

    int lineNumber = 1;
    for (String s : ls) {
      if (!s.isEmpty()) {
        Line line = new CodeLine(s, path, lineNumber);
        add(line);
      }
      lineNumber++;
    }
  }

  void add(Line line) {
    lines.add(line);
  }

  abstract Type getType();

  Lines getLines() {
    return lines;
  }

  Line get(int i) {
    return lines.get(i);
  }

  void set(int i, Line line) {
    lines.set(i, line);
  }

  String getCode() {
    StringBuilder sb = new StringBuilder();
    for (Line line : getLines()) {
      if (line.isNotEmpty()) {
        sb.append(line.toString());
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  abstract String getDescription();
}
