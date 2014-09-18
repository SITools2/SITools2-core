package fr.cnes.sitools.security.authorization;

import java.util.List;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;

public interface AuthorizationStoreInterface extends SitoolsStore<ResourceAuthorization> {

  public List<ResourceAuthorization> getListByType(ResourceCollectionFilter filter, String type);
  
}
