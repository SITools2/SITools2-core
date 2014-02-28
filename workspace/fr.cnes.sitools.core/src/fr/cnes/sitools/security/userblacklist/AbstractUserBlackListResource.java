package fr.cnes.sitools.security.userblacklist;

import java.util.List;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

public abstract class AbstractUserBlackListResource extends SitoolsResource {

  /** parent application */
  private UserBlackListApplication application = null;

  /** store */
  private SitoolsStore<UserBlackListModel> store = null;

  /** user identifier parameter */
  private String userId = null;

  /**
   * Default constructor
   */
  public AbstractUserBlackListResource() {
    super();
  }

  @Override
  public final void doInit() {
    super.doInit();

    application = (UserBlackListApplication) getApplication();
    store = application.getStore();

    userId = (String) this.getRequest().getAttributes().get("userId");
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public UserBlackListApplication getUserBlackListApplication() {
    return application;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<UserBlackListModel> getStore() {
    return store;
  }

  /**
   * Gets the userId value
   * 
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  protected boolean userExists(String userId) {
    String url = getSettings().getString(Consts.APP_SECURITY_URL) + "/users";
    User user = RIAPUtils.getObject(userId, url, getContext());
    return user != null;
  }

  
}
