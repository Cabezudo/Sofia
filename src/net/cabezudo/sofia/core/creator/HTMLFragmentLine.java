package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.08
 */
public class HTMLFragmentLine extends HTMLFileLine {

  public HTMLFragmentLine(Site site, Path basePath, Path parentPath, TemplateVariables templateVariables, Tag tag, int lineNumber, Caller caller) throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    super(site, basePath, parentPath, templateVariables, tag, lineNumber, caller);
  }

  @Override
  Path getFilePath() {
    String fileName = getTag().getValue("file");
    return Paths.get(fileName);
  }

  @Override
  HTMLSourceFile getHTMLSourceFile(Caller caller)
          throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    Path fullFileBasePath;
    String partialFilePathString = getFilePath().toString();
    if (partialFilePathString.startsWith("/") || caller == null) {
      fullFileBasePath = getSite().getVersionedSourcesPath().resolve(partialFilePathString.substring(1));
    } else {
      fullFileBasePath = caller.getBasePath().resolve(partialFilePathString);
    }
    HTMLFragmentSourceFile fragmentSourceFile = new HTMLFragmentSourceFile(getSite(), fullFileBasePath, getFilePath(), null, getTemplateVariables(), caller);
    return fragmentSourceFile;
  }
}
