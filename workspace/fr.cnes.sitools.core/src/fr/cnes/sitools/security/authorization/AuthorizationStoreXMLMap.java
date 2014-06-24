package fr.cnes.sitools.security.authorization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;
import org.restlet.data.MediaType;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;

public class AuthorizationStoreXMLMap extends XmlMapStore<ResourceAuthorization> implements AuthorizationStoreInterface {

  /** Default location */
  private static final String COLLECTION_NAME = "authorizations";

  /**
   * DataSetStoreXMLMap
   * 
   * @param cl
   *          Class<DataSet>
   * @param context
   *          Context
   */
  public AuthorizationStoreXMLMap(Class<ResourceAuthorization> cl, Context context) {
    super(cl, context);
  }

  /**
   * DataSetStoreXMLMap
   * 
   * @param cl
   *          Class<DataSet>
   * @param location
   *          File
   * @param context
   *          Context
   */
  public AuthorizationStoreXMLMap(Class<ResourceAuthorization> cl, File location, Context context) {
    super(cl, location, context);
  }

  /**
   * Constructor with file location
   * 
   * @param location
   *          the file location
   * @param context
   *          the Restlet Context
   */
  public AuthorizationStoreXMLMap(File location, Context context) {
    super(ResourceAuthorization.class, location, context);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public List<ResourceAuthorization> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("resourceAuthorization", ResourceAuthorization.class);
    this.init(location, aliases);
  }

  /**
   * Get XStream parser configured.
   * 
   * @return XStream
   */
  public XStream getParser() {
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    boolean strict = !settings.isStartWithMigration();

    XStream localXstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, context, strict);
    localXstream.autodetectAnnotations(true);
    localXstream.alias("resourceAuthorization", ResourceAuthorization.class);
    localXstream.alias("authorize", RoleAndMethodsAuthorization.class);
    localXstream.aliasField("ALL", ResourceAuthorization.class, "allMethod");
    localXstream.aliasField("POST", ResourceAuthorization.class, "postMethod");
    localXstream.aliasField("GET", ResourceAuthorization.class, "getMethod");
    localXstream.aliasField("PUT", ResourceAuthorization.class, "putMethod");
    localXstream.aliasField("DELETE", ResourceAuthorization.class, "deleteMethod");
    localXstream.aliasField("OPTIONS", ResourceAuthorization.class, "optionsMethod");
    localXstream.aliasField("HEAD", ResourceAuthorization.class, "headMethod");
    return localXstream;
  }

  @Override
  public List<ResourceAuthorization> getListByType(ResourceCollectionFilter filter, String type) {
    List<ResourceAuthorization> listFiltered = getList(filter);
    List<ResourceAuthorization> result = new ArrayList<ResourceAuthorization>();
    if ((listFiltered != null) && (listFiltered.size() > 0)) {

      for (ResourceAuthorization resourceAuth : listFiltered) {
        if (resourceAuth.getType() != null && resourceAuth.getType().equals(type)) {
          result.add(resourceAuth);
        }
      }
    }

    // Tri
    sort(result, null);

    return result;
  }

  @Override
  public ResourceAuthorization update(ResourceAuthorization authorization) {

    ResourceAuthorization result = null;

    Map<String, ResourceAuthorization> map = getMap();
    ResourceAuthorization current = map.get(authorization.getId());

    getLog().finest("Updating Authorization for resource " + current.getId());

    result = current;
    current.setName(authorization.getName());
    current.setDescription(authorization.getDescription());
    current.setUrl(authorization.getUrl());
    current.setAuthorizations(authorization.getAuthorizations());

    // authorizer la creation sur le PUT
    if (result != null) {
      map.put(result.getId(), result);
    }
    return result;
  }

}
