package fr.cnes.sitools.security.userblacklist;

import org.restlet.data.MediaType;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsCommonDateConverter;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;

/**
 * Abstract resource for UserBlacklist handling
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractUserBlackListResource extends SitoolsResource {

  /** parent application */
  private UserBlackListApplication application = null;

  /** store */
  private UserBlackListStoreInterface store = null;

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
  public UserBlackListStoreInterface getStore() {
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
