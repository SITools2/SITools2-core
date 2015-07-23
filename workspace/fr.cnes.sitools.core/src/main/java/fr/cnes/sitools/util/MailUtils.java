package fr.cnes.sitools.util;

import java.util.Map;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.common.Consts;

/**
 * The Class MailUtils.
 * 
 * @author m.gond
 */
public final class MailUtils {

  /**
   * Private constructor because MailUtils is an util class.
   */
  private MailUtils() {

  }

  /**
   * Adds the default parameters to the mail.
   * 
   * @param root
   *          the root
   * @param settings
   *          the settings
   * @param mail
   *          the mail
   */
  public static void addDefaultParameters(Map<String, Object> root, SitoolsSettings settings, Mail mail) {

    root.put("mail", mail);
    root.put(
        "sitoolsUrl",
        settings.getPublicHostDomain() + settings.getString(Consts.APP_URL)
            + settings.getString(Consts.APP_CLIENT_USER_URL) + "/");
    root.put("adminMail", settings.getAdminMail());

  }

}
