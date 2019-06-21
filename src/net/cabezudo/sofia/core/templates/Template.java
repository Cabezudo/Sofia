package net.cabezudo.sofia.core.templates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.08
 */
public abstract class Template {

  private final Path path;
  private final String name;

  public Template(Path path, String name) {
    this.path = path;
    this.name = name;
  }

  public abstract void set(String key, String value);

  Path getPath() {
    return path;
  }

  String getName() {
    return name;
  }

  String loadFile(Path path, String string) throws IOException {
    Path templatePath = path.resolve(string);
    return loadFile(templatePath);
  }

  String loadFile(Path templatePath) throws IOException {
    byte[] encoded = Files.readAllBytes(templatePath);
    return new String(encoded, Configuration.getInstance().getEncoding());
  }
}
