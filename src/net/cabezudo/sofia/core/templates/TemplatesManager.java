package net.cabezudo.sofia.core.templates;

import java.io.IOException;
import java.util.Locale;
import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.08
 */
public class TemplatesManager {

  private static TemplatesManager INSTANCE;

  public static TemplatesManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new TemplatesManager();
    }
    return INSTANCE;
  }

  private TemplatesManager() {
    // Just to protect the singleton
  }

  public EMailTemplate getEMailPasswordRecoveryTemplate(Locale locale) throws IOException {
    return new EMailTemplate(Configuration.getInstance().getEMailPasswordRecoveryTemplateName(), locale);
  }

  public EMailTemplate getEMailPasswordChangedTemplate(Locale locale) throws IOException {
    return new EMailTemplate(Configuration.getInstance().getEMailPasswordChangedTemplateName(), locale);
  }

  public EMailTemplate getEMailRegistrationRetryAlertTemplate(Locale locale) throws IOException {
    return new EMailTemplate(Configuration.getInstance().getEMailRegistrationRetryAlertTemplateName(), locale);
  }
}
