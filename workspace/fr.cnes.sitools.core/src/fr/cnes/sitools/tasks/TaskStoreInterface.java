package fr.cnes.sitools.tasks;

import java.util.List;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.tasks.model.TaskModel;

/**
 * Interface for Task persistence
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public interface TaskStoreInterface extends SitoolsStore<TaskModel> {
  /**
   * Sort the list according to criteria
   * 
   * @param list
   *          the list
   * @param filter
   *          the filter
   */
  void sort(List<TaskModel> list, ResourceCollectionFilter filter);
}
