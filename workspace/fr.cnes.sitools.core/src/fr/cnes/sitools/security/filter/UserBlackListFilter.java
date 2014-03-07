package fr.cnes.sitools.security.filter;

import java.util.List;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.server.Consts;

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

  /**
   * Instantiates a new user black list filter.
   * 
   * @param context
   *          the context
   */
  @SuppressWarnings("unchecked")
  public UserBlackListFilter(Context context) {
    super(context);

    // get the SitoolsSettings
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);

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
        }

        response.setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Blacklisted user");
        log(request, id);
        return STOP;
      }

      if (userAuthenticated(id, request)) {
        counter.remove(id);
      }
      if (userNotAuthenticated(id, request)) {
        counter.addRequest(id);
      }
    }
    return CONTINUE;
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
   * @param id
   *          the id
   */
  private void log(Request request, String id) {
    getLogger().log(
        Level.INFO,
        "SECURTIY ACCESS ERROR : Request to : " + request.getResourceRef().getPath() + " forbidden, user : " + id
            + "blacklisted for too many requests with bad credentials");
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
   * User authenticated.
   * 
   * @param id
   *          the id
   * @param request
   *          the request
   * @return true, if successful
   */
  private boolean userAuthenticated(String id, Request request) {
    return (request.getClientInfo() != null && request.getClientInfo().isAuthenticated());
  }
}
