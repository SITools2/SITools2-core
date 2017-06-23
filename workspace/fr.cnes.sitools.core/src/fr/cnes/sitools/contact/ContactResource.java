package fr.cnes.sitools.contact;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.MailUtils;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;

public class ContactResource extends AbstractContactResource {

  @Override
  public void sitoolsDescribe() {
    setName("ContactResource");
    setDescription("Resource to post contact form");
    setNegotiated(false);
  }
  
  @Put
  public Representation sendMessage(Representation representation, Variant variant) {
    if (representation == null) {
      trace(Level.INFO, "Cannot send message");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "PROJECT_REPRESENTATION_REQUIRED");
    }
    try {
      Contact contact = getObject(representation, variant);
      if (isValid(contact)) {
        String adminMail = getSettings().getAdminMail();
        if (adminMail == null) {
          getLogger().info("No email address for administrator, cannot send contact email");
          throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "ADMIN_MAIL_NOT_DEFINED");
        }
        Mail mail = prepareMail(contact, adminMail);
        send(mail);
      } else {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "CONTACT_FORM_IS_NOT_VALID");
      }
      Response response = new Response(true, contact, Contact.class, "contact");
      return getRepresentation(response, variant);
    } catch (ResourceException e) {
      trace(Level.INFO, "Cannot send message");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot send message");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  private Mail prepareMail(Contact contact, String recipient) {
    Mail mail = new Mail();
    mail.setSubject("SITools2 - Someone has contacted you");
    mail.setBody(buildBodyWithTemplate(contact, mail));
    String[] toList = new String[] {recipient};
    mail.setToList(Arrays.asList(toList));
    
    return mail;
  }
  
  private String buildBodyWithTemplate(Contact contact, Mail mail) {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.contact.ftl";
    contact.setBody(StringEscapeUtils.escapeHtml(contact.getBody()));
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("contact", contact);
    //MailUtils.addDefaultParameters(root, getSettings(), mail);
    root.put("sitoolsUrl", settings.getPublicHostDomain() + settings.getString(Consts.APP_URL) + settings.getString(Consts.APP_CLIENT_PORTAL_URL) + "/");
    TemplateUtils.describeObjectClassesForTemplate(templatePath, root);

    root.put("context", getContext());

    return TemplateUtils.toString(templatePath, root);
  }

  private void send(Mail mail) {
    org.restlet.Response response = null;
    final String url = RIAPUtils.getRiapBase() + getSettings().getString(Consts.APP_MAIL_ADMIN_URL);
    try {
      Request request = new Request(Method.POST, url, new ObjectRepresentation<Mail>(mail));
      response = getContext().getClientDispatcher().handle(request);
    }
    catch (Exception e) {
      getLogger().warning("Failed to post message to user");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    if (response.getStatus().isError()) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Server Error sending email to user.");
    }
  }


  private boolean isValid(Contact contact) {
    boolean isValid = false;
    if (contact != null) {
      String name = contact.getName();
      String email = contact.getEmail();
      String body = contact.getBody();
      isValid = StringUtils.isNotBlank(name) && StringUtils.isNotBlank(email) && StringUtils.isNotBlank(body);
    }
    return isValid;
  }

  @Override
  protected void describePut(MethodInfo info, String path) {
    info.setDocumentation("Method to put contact sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }
}
