package net.cabezudo.sofia.core.templates;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.08
 */
public class EMailTemplate extends Template {

  private String html;
  private String text;
  private final Path htmlTemplatePath;
  private final String subject;

  EMailTemplate(String templateName, Locale locale) throws IOException {
    super(Configuration.getInstance().getCommonsMailTemplatesPath(), templateName);
    if (locale == null) {
      throw new RuntimeException("The locale is null");
    }
    htmlTemplatePath = super.getPath().resolve(super.getName() + "." + locale.getLanguage() + ".html");
    html = super.loadFile(htmlTemplatePath);
    text = super.loadFile(super.getPath(), super.getName() + "." + locale.getLanguage() + ".txt");

    String searchString = Pattern.quote("\"");
    String replacement = Matcher.quoteReplacement("\\\"");
    html = html.replaceAll(searchString, replacement);
    text = text.replaceAll(searchString, replacement);

    html = html.replaceAll(">[ \\n]*<", "><");

    searchString = Pattern.quote("\n");
    replacement = Matcher.quoteReplacement("");
    html = html.replaceAll(searchString, replacement);

    searchString = Pattern.quote("\n");
    replacement = Matcher.quoteReplacement("\\n");
    text = text.replaceAll(searchString, replacement);

    Pattern p = Pattern.compile("<head>.*?<title>(.*?)</title>.*?</head>", Pattern.DOTALL);
    Matcher m = p.matcher(html);
    if (m.find()) {
      subject = m.group(1);
    } else {
      throw new RuntimeException("Title not found in template " + htmlTemplatePath.toString());
    }
  }

  public String getSubject() {
    return subject;
  }

  public String getPlainText() {
    return text;
  }

  public String getHtmlText() {
    return html;
  }

  @Override
  public void set(String key, String value) {
    String searchString = Pattern.quote("#{" + key + "}");
    String replacement = Matcher.quoteReplacement(value);
    html = html.replaceAll(searchString, replacement);
    text = text.replaceAll(searchString, replacement);
  }
}
