package fr.cnes.sitools.dataset.services;

import java.util.List;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;

/**
 * Interface for ServiceCollectionModel persistence
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public interface ServiceStoreInterface extends SitoolsStore<ServiceCollectionModel> {
  /**
   * Sort the list according to criteria
   * 
   * @param list
   *          the list
   * @param filter
   *          the filter
   */
  void sort(List<ServiceCollectionModel> list, ResourceCollectionFilter filter);
}
