package fr.cnes.sitools.service.watch;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadWatchService extends AbstractWatchService {

  private ScheduledThreadPoolExecutor scheduledExecutor;
  private int refreshPeriod;
  private TimeUnit refreshTimeUnit;
  private ScheduledFuture<?> future;

  public ScheduledThreadWatchService(SitoolsWatchServiceRunnableInterface runnable, int nbThreads, int refreshPeriod,
      TimeUnit refreshTimeUnit) {
    super(runnable);
    scheduledExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(nbThreads);
    this.refreshPeriod = refreshPeriod;
    this.refreshTimeUnit = refreshTimeUnit;
  }

  @Override
  public void start() {
    final SitoolsWatchServiceRunnableInterface runnable = getRunnable();
    future = scheduledExecutor.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        runnable.execute();
      }
    }, 0, this.refreshPeriod, this.refreshTimeUnit);
  }

  @Override
  public void stop() {
    if (future != null) {
      future.cancel(true);
    }
  }

  @Override
  public void restart() {
    stop();
    start();
  }

}
