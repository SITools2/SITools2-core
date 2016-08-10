package fr.cnes.sitools.contact;

import java.util.Arrays;
import java.util.logging.Level;

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

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

public class ContactResource extends AbstractContactResource {

  private final static String BR = "<BR />";
  
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
    mail.setBody(buildBody(contact));
    String[] toList = new String[] {recipient};
    mail.setToList(Arrays.asList(toList));
    
    return mail;
  }
  
  private String buildBody(Contact contact) {
    StringBuilder message = new StringBuilder();
    message.append("Dear administrator,").append(BR);
    message.append(contact.getName() + " has sent you a message using the contact form:").append(BR).append(BR);
    message.append(contact.getBody()).append(BR);
    message.append(contact.getName()).append(" ("+contact.getEmail()+")").append(BR);
    return message.toString();
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
