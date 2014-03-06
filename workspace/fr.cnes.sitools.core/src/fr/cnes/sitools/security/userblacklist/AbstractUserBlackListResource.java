package fr.cnes.sitools.security.userblacklist;

import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsCommonDateConverter;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
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

  /**
   * Encode a response into a Representation according to the given media type.
   * 
   * @param response
   *          Response
   * @param media
   *          Response
   * @return Representation
   */
  public Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.registerConverter(new SitoolsCommonDateConverter());

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
