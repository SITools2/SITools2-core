package fr.cnes.sitools.security.userblacklist;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.common.store.SitoolsStoreXML;

/**
 * The Class UserBlackListStoreXML.
 * 
 * @author m.gond
 */
@Deprecated
public class UserBlackListStoreXML extends SitoolsStoreXML<UserBlackListModel> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "userBlacklist";

  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(UserBlackListStoreXML.class.getName());

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public UserBlackListStoreXML(File location, Context context) {
    super(UserBlackListModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public UserBlackListStoreXML(Context context) {
    super(UserBlackListModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public UserBlackListModel update(UserBlackListModel blacklistModel) {
    UserBlackListModel result = null;
    for (Iterator<UserBlackListModel> it = getRawList().iterator(); it.hasNext();) {
      UserBlackListModel current = it.next();
      if (current.getId().equals(blacklistModel.getId())) {
        log.info("Updating UserBlacklist details");

        result = current;
        current.setId(blacklistModel.getId());
        current.setUsername(blacklistModel.getUsername());
        current.setName(blacklistModel.getName());
        current.setDescription(blacklistModel.getDescription());
        current.setDate(blacklistModel.getDate());
        current.setIpAddress(blacklistModel.getIpAddress());
        current.setUserExists(blacklistModel.getUserExists());

        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  @Override
  public List<UserBlackListModel> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("userBlackListModel", UserBlackListModel.class);
    this.init(location, aliases);
  }

}
