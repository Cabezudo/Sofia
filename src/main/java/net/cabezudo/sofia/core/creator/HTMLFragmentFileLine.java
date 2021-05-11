package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.files.FileHelper;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.08
 */
public class HTMLFragmentFileLine extends HTMLFileLine {

  public HTMLFragmentFileLine(Site site, Path basePath, Path parentPath, TemplateVariables templateVariables, TextsFile textsFile, Tag tag, int lineNumber, Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    super(site, basePath, parentPath, templateVariables, textsFile, tag, lineNumber, caller);
  }

  @Override
  Path getConfigurationFilePath(Caller caller) {
    String fileName = getFilePath().toString();
    int i = fileName.lastIndexOf(".");
    String partialFile;
    if (i != -1) {
      partialFile = fileName.substring(0, i);
    } else {
      partialFile = fileName;
    }
    Path configurationFilePath = caller.getBasePath().resolve(caller.getRelativePath()).getParent().resolve(partialFile + ".json");
    Logger.debug("Configuration file path: %s", configurationFilePath);
    return configurationFilePath;
  }

  @Override
  Path getTextsFilePath(Caller caller) {
    String fileName = getFilePath().toString();
    String textsFileName = FileHelper.removeExtension(fileName) + ".texts.json";

    Path textsFilePath = caller.getBasePath().resolve(caller.getRelativePath()).getParent().resolve(textsFileName);
    Logger.debug("Text file path: %s", textsFilePath);
    return textsFilePath;
  }

  @Override
  Path getFilePath() {
    String fileName = getTag().getValue("file");
    return Paths.get(fileName);
  }

  @Override
  HTMLSourceFile getHTMLSourceFile(Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    Path fullFileBasePath;
    String partialFilePathString = getFilePath().toString();
    // FIX This fail with relative paths like file="this.html". Take the root site path
    if (partialFilePathString.startsWith("/") || caller == null) {
      fullFileBasePath = getSite().getVersionedSourcesPath().resolve(partialFilePathString.substring(1));
    } else {
      fullFileBasePath = caller.getBasePath().resolve(partialFilePathString);
    }
    return new HTMLFragmentSourceFile(getSite(), fullFileBasePath, getFilePath(), null, getTemplateVariables(), getTextsFile(), caller);
  }
}
