package fr.cnes.sitools.client;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.applications.PublicApplication;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.security.challenge.ChallengeToken;
import fr.cnes.sitools.server.Consts;

/**
 * Gets the Unlock account index page
 * 
 * 
 * @author m.gond
 */
public class UnlockAccountIndex extends SitoolsResource {

  /**
   * The PublicApplication
   */
  private PublicApplication application;
  /**
   * The token
   */
  private String token;

  @Override
  public void sitoolsDescribe() {
    setName("UnlockAccountIndex");
    setDescription("Resource to return the index.html page of the unlockAccount interface");
  }

  @Override
  protected void doInit() {
    super.doInit();
    application = (PublicApplication) this.getApplication();

    ChallengeToken challengeToken = application.getChallengeToken();

    token = getRequest().getResourceRef().getQueryAsForm().getFirstValue("cdChallengeMail", null);
    if (token == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "cdChallengeMail parameter mandatory");
    }

    if (!challengeToken.isValid(token)) {
      throw new ResourceException(
          Status.CLIENT_ERROR_GONE,
          "You asked to unlock your account, but the request is no longer available. Please ask again to unlock your password on SITools2");
    }
  }

  @Get
  @Override
  public Representation get() {

    getApplication().getLogger().fine("get UnlockAccountIndex");

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("appUrl", application.getSettings().getString(Consts.APP_URL));
    params.put("challengeToken", token);
    params.put("resourceUrl", "/unlockAccount");

    Reference ref = LocalReference.createFileReference(application.getResetPasswordIndexUrl());

    Representation unlockAccount = new ClientResource(ref).get();

    // Wraps the bean with a FreeMarker representation
    return new TemplateRepresentation(unlockAccount, params, MediaType.TEXT_HTML);
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL documentation info
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the unlockaccount page.");
    this.addStandardGetRequestInfo(info);
    ResponseInfo responseInfo = new ResponseInfo();
    RepresentationInfo representationInfo = new RepresentationInfo();
    representationInfo.setReference("html_freemarker");
    responseInfo.getRepresentations().add(representationInfo);
    info.getResponses().add(responseInfo);
  }

}
