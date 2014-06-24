package fr.cnes.sitools.registry.watch;

import java.io.File;
import java.util.logging.Level;

import org.restlet.engine.Engine;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.service.watch.SitoolsWatchServiceRunnableInterface;

/**
 * The Class AppRegistryWatchServiceRunnable.
 * 
 * @author m.gond (AKKA Technologies)
 */
public class AppRegistryWatchServiceRunnable implements SitoolsWatchServiceRunnableInterface {

  /** The settings. */
  private SitoolsSettings settings;

  /** AppRegistryApplication */
  private AppRegistryApplication registryApp;

  /**
   * Instantiates a new user and group watch service runnable.
   * 
   * @param settings
   *          the settings
   */
  public AppRegistryWatchServiceRunnable(SitoolsSettings settings, AppRegistryApplication app) {
    this.settings = settings;
    this.registryApp = app;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.service.watch.SitoolsWatchServiceRunnableInterface#execute()
   */
  @Override
  public void execute() {
    try {
      File eventFile = registryApp.getEventFile();
      
//      System.out.println("Last modified date : " + eventFile.lastModified());
//      System.out.println("Last authorizations update : " + registryApp.getAuthorizationsLastModified());

      if (eventFile != null && eventFile.lastModified() > registryApp.getAuthorizationsLastModified()) {
        System.out.println("EVENT DETECTED : reattach all applications");
        registryApp.reattachAllApplications();
        registryApp.setAuthorizationsLastModified(eventFile.lastModified());
      }

    }
    catch (Exception e) {
      Engine.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,
          "Error while watching for changing files", e);
    }

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
