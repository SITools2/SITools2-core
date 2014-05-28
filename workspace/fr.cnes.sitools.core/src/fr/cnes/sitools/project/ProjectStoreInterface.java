package fr.cnes.sitools.project;

import java.util.List;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.project.model.Project;

/**
 * Interface for Project persistence
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public interface ProjectStoreInterface extends SitoolsStore<Project> {
  /**
   * Sort the list according to criteria
   * 
   * @param list
   *          the list
   * @param filter
   *          the filter
   */
  void sort(List<Project> list, ResourceCollectionFilter filter);
}
