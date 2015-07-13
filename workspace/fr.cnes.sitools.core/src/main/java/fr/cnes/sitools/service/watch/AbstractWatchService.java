package fr.cnes.sitools.service.watch;

import java.util.Map;

import org.restlet.Component;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.service.SitoolsService;

public abstract class AbstractWatchService implements SitoolsService {

  private SitoolsWatchServiceRunnableInterface runnable;

  public AbstractWatchService(SitoolsWatchServiceRunnableInterface runnable) {
    this.runnable = runnable;
  }

  protected SitoolsWatchServiceRunnableInterface getRunnable() {
    return this.runnable;
  }

  @Override
  public Component getComponent() {
    return SitoolsSettings.getInstance().getComponent();
  }

  @Override
  public SitoolsSettings getSettings() {
    return SitoolsSettings.getInstance();
  }

  @Override
  public Map<String, Object> getProperties() {
    return null;
  }

}
