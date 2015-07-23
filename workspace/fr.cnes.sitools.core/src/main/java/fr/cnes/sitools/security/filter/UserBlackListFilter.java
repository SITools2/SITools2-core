package fr.cnes.sitools.security.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.MailUtils;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * The Class UserBlackListFilter.
 * 
 * Blacklist users when too many requests with bad credentials are performed
 * 
 * @author m.gond
 */
public class UserBlackListFilter extends Filter {

  /** The counter. */
  private RequestCounter counter;

  /** The nb of allowed requests before blacklist. */
  private int nbAllowedRequestBeforeBlacklist;

  /** The store. */
  private SitoolsStore<UserBlackListModel> store;
  /** The settings */
  private SitoolsSettings settings;

  /**
   * Instantiates a new user black list filter.
   * 
   * @param context
   *          the context
   */
  @SuppressWarnings("unchecked")
  public UserBlackListFilter(Context context) {
    super(context);

    settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);

    nbAllowedRequestBeforeBlacklist = settings.getInt("Starter.NB_ALLOWED_REQ_BEFORE_BLACKLIST");
    store = (SitoolsStore<UserBlackListModel>) settings.getStores().get(Consts.APP_STORE_USER_BLACKLIST);

    counter = (RequestCounter) (context.getAttributes().get(Consts.SECURITY_FILTER_USER_BLACKLIST_CONTAINER));
    if (counter == null) {
      counter = (RequestCounter) settings.getStores().get(Consts.SECURITY_FILTER_USER_BLACKLIST_CONTAINER);

      if (counter == null) {
        counter = new RequestCounterHashSet();
        initCounter(store, counter, nbAllowedRequestBeforeBlacklist);
        settings.getStores().put(Consts.SECURITY_FILTER_USER_BLACKLIST_CONTAINER, counter);
      }
    }

  }

  /**
   * Initialize the counter.
   * 
   * @param theStore
   *          the store containing the already blacklisted users
   * @param theCounter
   *          the counter to initialize
   * @param nbRequests
   *          the number of requests to initialize each blacklisted user found in the store
   */
  private void initCounter(SitoolsStore<UserBlackListModel> theStore, RequestCounter theCounter, int nbRequests) {
    List<UserBlackListModel> list = theStore.getList();
    for (UserBlackListModel userBlackListModel : list) {
      theCounter.initNumberOfRequest(userBlackListModel.getUsername(), nbRequests);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#beforeHandle(org.restlet.Request, org.restlet.Response)
   */
  @Override
  protected int beforeHandle(Request request, Response response) {

    if (request.getChallengeResponse() != null) {
      String id = request.getChallengeResponse().getIdentifier();

      int nb = counter.getNumberOfRequests(id);
      if (nb >= nbAllowedRequestBeforeBlacklist) {
        if (store.retrieve(id) == null) {
          store.create(wrapUserBlacklist(id, request));
          sendMailToAdmin(id);
        }

        response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Blacklisted user");
        log(request, response, id);
        return STOP;
      }

    }
    return CONTINUE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#afterHandle(org.restlet.Request, org.restlet.Response)
   */
  @Override
  protected void afterHandle(Request request, Response response) {
    super.afterHandle(request, response);

    if (request.getChallengeResponse() != null) {
      String id = request.getChallengeResponse().getIdentifier();

      if (userNotAuthenticated(id, request) || statusError(response)) {
        counter.addRequest(id);
      }
      else {
        counter.remove(id);
      }
    }

  }

  /**
   * Return true if the status of the response is an authentication error.
   * 
   * @param response
   *          the response
   * @return true, if true if the status of the response is an authentication error, false otherwise
   */
  private boolean statusError(Response response) {
    return response.getStatus().equals(Status.CLIENT_ERROR_FORBIDDEN)
        || response.getStatus().equals(Status.CLIENT_ERROR_UNAUTHORIZED);
  }

  /**
   * Wrap an id and some request information into a {@link UserBlackListModel} object.
   * 
   * @param id
   *          the identifier of the User
   * @param request
   *          the {@link Request}
   * @return a {@link UserBlackListModel}
   */
  private UserBlackListModel wrapUserBlacklist(String id, Request request) {

    UserBlackListModel model = new UserBlackListModel();
    model.setId(id);
    model.setUsername(id);
    model.setIpAddress(request.getClientInfo().getUpstreamAddress());
    model.setDate(request.getDate());
    model.setName(id);

    return model;
  }

  /**
   * Log.
   * 
   * @param request
   *          the request
   * @param response
   *          the response
   * @param id
   *          the user id
   */
  private void log(Request request, Response response, String id) {

    String message = "Request to: " + request.getResourceRef().getPath() + " forbidden, user: " + id
        + " blacklisted for too many requests with bad credentials";

    LogRecord record = new LogRecord(Level.WARNING, message);
    response.getAttributes().put("LOG_RECORD", record);

  }

  /**
   * User not authenticated.
   * 
   * @param id
   *          the id
   * @param request
   *          the request
   * @return true, if successful
   */
  private boolean userNotAuthenticated(String id, Request request) {
    return (request.getClientInfo() != null && !request.getClientInfo().isAuthenticated() && id != null && !id
        .isEmpty());
  }

  /**
   * Send the new password by mail to an user
   * 
   * @param id
   *          the user identifier
   */
  private void sendMailToAdmin(String id) {

    String adminMail = settings.getAdminMail();

    if (adminMail == null) {
      getLogger().info("No email address for administrator, cannot send inscription email");
      return;
    }

    String[] toList = new String[] {adminMail};
    Mail mailToAdmin = new Mail();
    mailToAdmin.setToList(Arrays.asList(toList));

    // Object
    mailToAdmin.setSubject("SITools2 - User account blocked");

    // Body
    mailToAdmin.setBody(String.format("The account of the user %s has been blocked", id));

    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.account.blocked.ftl";
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("userId", id);
    MailUtils.addDefaultParameters(root, settings, mailToAdmin);

    TemplateUtils.describeObjectClassesForTemplate(templatePath, root);

    root.put("context", getContext());

    String body = TemplateUtils.toString(templatePath, root);
    if (Util.isNotEmpty(body)) {
      mailToAdmin.setBody(body);
    }

    org.restlet.Response sendMailResponse = null;
    try {
      // riap request to MailAdministration application
      Request request = new Request(Method.POST, RIAPUtils.getRiapBase()
          + settings.getString(Consts.APP_MAIL_ADMIN_URL), new ObjectRepresentation<Mail>(mailToAdmin));

      sendMailResponse = getContext().getClientDispatcher().handle(request);
    }
    catch (Exception e) {
      getApplication().getLogger().warning("Failed to post message to user");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    if (sendMailResponse.getStatus().isError()) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Server Error sending email to user.");
    }
  }
}
