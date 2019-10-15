package net.cabezudo.sofia.core.files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.06
 */
public class FileHelper {

  public static void copyDirectory(Path src, Path dest) throws IOException {
    Path systemPath = Configuration.getInstance().getSystemPath();
    Logger.debug("Copy resources from %s to %s.", systemPath.relativize(src).toString(), systemPath.relativize(dest));

    if (src == null) {
      throw new IllegalArgumentException("src parameter is null.");
    }
    if (dest == null) {
      throw new IllegalArgumentException("dest parameter is null.");
    }
    if (Files.exists(src, LinkOption.NOFOLLOW_LINKS)) {
      if (!Files.isDirectory(src)) {
        if (Files.isRegularFile(src)) {
          throw new IllegalArgumentException("src " + src + " is not a directory.");
        } else {
          throw new FileNotFoundException(src + " not found.");
        }
      }

      List<Path> list = Files.walk(src).collect(Collectors.toList());

      for (Path path : list) {
        Path destPath = dest.resolve(src.relativize(path));
        if (!Files.exists(destPath)) {
          if (Files.isDirectory(path)) {
            Files.createDirectory(destPath);
          } else {
            Files.copy(path, destPath);
          }
        }
      }
    }
  }
}
