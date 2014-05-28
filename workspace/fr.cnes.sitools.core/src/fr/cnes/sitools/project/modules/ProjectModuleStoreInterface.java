package fr.cnes.sitools.project.modules;

import java.util.List;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;

/**
 * Interface for ProjectModuleModel persistence
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public interface ProjectModuleStoreInterface extends SitoolsStore<ProjectModuleModel> {
  /**
   * Sort the list according to criteria
   * 
   * @param list
   *          the list
   * @param filter
   *          the filter
   */
  void sort(List<ProjectModuleModel> list, ResourceCollectionFilter filter);
}
