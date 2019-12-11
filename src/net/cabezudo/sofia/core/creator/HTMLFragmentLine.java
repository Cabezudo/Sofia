package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import net.cabezudo.sofia.core.html.Tag;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.04
 */
public class HTMLFragmentLine extends Line {

  private final Line startLine;
  private final HTMLFragmentSourceFile file;
  private final Line endLine;

  HTMLFragmentLine(Site site, Path basePath, Path parentPath, TemplateVariables templateVariables, Tag tag, int lineNumber, Caller caller) throws IOException, SiteCreationException, LocatedSiteCreationException, SQLException, InvalidFragmentTag, LibraryVersionConflictException {
    super(lineNumber);
    tag.rename("div");
    startLine = new CodeLine(tag.getStartTag(), lineNumber);
    endLine = new CodeLine(tag.getEndTag(), lineNumber);
    String fileName = tag.getValue("file");
    Path newFileBasePath;
    if (fileName.startsWith("/")) {
      newFileBasePath = site.getSourcesPath();
      fileName = fileName.substring(1);
    } else {
      newFileBasePath = basePath;
    }
    Path partialPath = Paths.get(fileName);
    try {
      Caller newCaller = new Caller(basePath, parentPath, lineNumber, caller);
      file = new HTMLFragmentSourceFile(site, newFileBasePath, partialPath, templateVariables, newCaller);
      file.loadJSONConfigurationFile();
      file.loadHTMLFile();
    } catch (NoSuchFileException e) {
      throw new NoSuchFileException("Can't find the file " + fileName + " called from " + parentPath + ":" + lineNumber);
    }
  }

  @Override
  Libraries getLibraries() {
    return file.getLibraries();
  }

  @Override
  String getCode() {
    StringBuilder sb = new StringBuilder();
    sb.append(startLine.getCode()).append('\n');
    sb.append(file.toHTML());
    sb.append(endLine.getCode()).append('\n');
    return sb.toString();
  }

  @Override
  Line replace(TemplateVariables templateVariables) throws UndefinedLiteralException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  boolean isNotEmpty() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  boolean startWith(String start) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int compareTo(Line o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  Lines getJavaScriptLines() {
    return file.getJavaScriptLines();
  }

  @Override
  Lines getCascadingStyleSheetLines() {
    return file.getCascadingStyleSheetLines();
  }
}