package fr.cnes.sitools.applications;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.routing.Router;
import org.restlet.security.Authenticator;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.login.LoginResource;
import fr.cnes.sitools.security.authentication.AuthenticatorFactory;
import fr.cnes.sitools.security.filter.UserBlackListFilter;

/**
 * The Class LoginApplication. Used to handle login
 * 
 * @author m.gond
 * 
 */
public class LoginApplication extends SitoolsApplication {

  /**
   * Instantiates a new login application.
   * 
   * @param appContext
   *          the app context
   */
  public LoginApplication(Context appContext) {
    super(appContext);
  }

  /**
   * Application description
   */
  public void sitoolsDescribe() {
    setCategory(Category.PUBLIC);
    setName("LoginApplication");
    setDescription("Login application only used to check user login"
        + "-> Public user must have at least GET authorizations on this application");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    if (getAuthenticationRealm() != null) {
      // Manually attach a new UserBlackListFilter only to the login-mandatory Resource
      UserBlackListFilter filter = new UserBlackListFilter(getContext());

      // "Basic Public Login Test"
      Authenticator authenticator = AuthenticatorFactory.getAuthenticator(getContext(), true, getSettings()
          .getAuthenticationDOMAIN(), getAuthenticationRealm());
      authenticator.setNext(LoginResource.class);
      
      filter.setNext(LoginResource.class);
      authenticator.setNext(filter);

      router.attach("/login", authenticator);


      // "Basic Public Login Test Mandatory to return credentials"
      Authenticator authenticatorMandatory = AuthenticatorFactory.getAuthenticator(getContext(), false, getSettings()
          .getAuthenticationDOMAIN(), getAuthenticationRealm());

      authenticatorMandatory.setNext(LoginResource.class);

      router.attach("/login-mandatory", authenticatorMandatory);
    }

    else {
      router.attach("/login", LoginResource.class);
      // Manually attach a new UserBlackListFilter only to the login-mandatory Resource
      UserBlackListFilter filter = new UserBlackListFilter(getContext());
      filter.setNext(LoginResource.class);
      router.attach("/login-mandatory", filter);

      router.attach("/login", LoginResource.class);
    }

    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for public resources used by other SITools2 client applications.");
    return appInfo;
  }
}
