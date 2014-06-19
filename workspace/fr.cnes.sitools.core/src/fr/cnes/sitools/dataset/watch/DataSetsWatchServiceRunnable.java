package fr.cnes.sitools.dataset.watch;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.engine.Engine;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.dataset.DataSetAdministration;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.DataSetStoreInterface;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.service.watch.SitoolsWatchServiceRunnableInterface;

/**
 * The Class UserAndGroupWatchServiceRunnable.
 * 
 * @author m.gond (AKKA Technologies)
 */
public class DataSetsWatchServiceRunnable implements SitoolsWatchServiceRunnableInterface {

  /** The settings. */
  private SitoolsSettings settings;

  /** DataSetAdministration */
  private DataSetAdministration datasetApp;

  /**
   * Instantiates a new user and group watch service runnable.
   * 
   * @param settings
   *          the settings
   */
  public DataSetsWatchServiceRunnable(SitoolsSettings settings, DataSetAdministration app) {
    this.settings = settings;
    this.datasetApp = app;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.service.watch.SitoolsWatchServiceRunnableInterface#execute()
   */
  @Override
  public void execute() {
    try {
      File appFileEvent = this.datasetApp.getEventFile();

      long lastDataSetRefresh = this.datasetApp.getLastRefresh();

      if (appFileEvent != null && appFileEvent.lastModified() > lastDataSetRefresh) {
        System.out.println("c'est parti pour le refresh des datasets");

        Map<String, DataSetApplication> datasetApps = getAllDatasetApplication();

        DataSetStoreInterface store = this.datasetApp.getStore();
        List<DataSet> datasets = store.getList();
        for (DataSet dataset : datasets) {
          System.out.println("DATASET : " + dataset.getName() + " dataset.getLastStatusUpdate() : "
              + dataset.getLastStatusUpdate() + " à comparer avec : " + lastDataSetRefresh);
          if (dataset.getLastStatusUpdate() != null && dataset.getLastStatusUpdate().getTime() > lastDataSetRefresh) {

            if ("ACTIVE".equals(dataset.getStatus())) {
              System.out.println("ACTIVE " + dataset.getName());
              datasetApp.detachDataSet(dataset, true);
              datasetApp.attachDataSet(dataset, true);
            }
            else {
              System.out.println("INACTIVE " + dataset.getName());
              datasetApp.detachDataSet(dataset, true);
            }
          }
          // remove the handled datasets application (dataset still exists)
          datasetApps.remove(dataset.getId());
        }
        this.datasetApp.setLastRefresh(appFileEvent.lastModified());

        // remove the datasets that doesn't exists anymore
        for (DataSetApplication app : datasetApps.values()) {
          System.out.println("DETACH DEFINITIVELY " + app.getDataSet().getName());
          datasetApp.detachDataSetDefinitif(app.getDataSet(), true);
        }
      }

    }
    catch (Exception e) {
      Engine.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,
          "Error while watching for changing files", e);
    }

  }

  private Map<String, DataSetApplication> getAllDatasetApplication() {
    Map<String, SitoolsApplication> allApps = getSettings().getAppRegistry().getApplications();
    Map<String, DataSetApplication> datasetApps = new HashMap<String, DataSetApplication>();
    for (SitoolsApplication app : allApps.values()) {
      if (app instanceof DataSetApplication) {
        datasetApps.put(app.getId(), (DataSetApplication) app);
      }
    }
    return datasetApps;
  }

  /**
   * Gets the settings.
   * 
   * @return the settings
   */
  private SitoolsSettings getSettings() {
    return settings;
  }

}
