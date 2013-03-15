package fr.cnes.sitools.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.model.Resource;

/**
 * 
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class JobOnCollection {

  /**
   * @param args
   */
  public static void main(String[] args) {

    List<Future<JobStatus>> listFutureJobStatus = new ArrayList<Future<JobStatus>>();
    HashMap<String, JobStatus> listJobStatus = new HashMap<String, JobStatus>();

    Collection datasetCollection = new Collection();

    datasetCollection.setDataSets(new ArrayList<Resource>());

    for (int i = 1; i < 10; i++) {
      Resource datasetResource = new Resource();
      datasetResource.setId(String.valueOf(i));
      datasetCollection.getDataSets().add(datasetResource);
    }

    ExecutorService pooledExecutor = Executors.newFixedThreadPool(datasetCollection.getDataSets().size());
    for (Iterator iterator = datasetCollection.getDataSets().iterator(); iterator.hasNext();) {
      Resource datasetResource = (Resource) iterator.next();

      JobOnDataset job = new JobOnDataset(datasetResource);

      Future<JobStatus> future = pooledExecutor.submit(job);
      listFutureJobStatus.add(future);
    }

    // This will make the executor accept no new threads
    // and finish all existing threads in the queue
    pooledExecutor.shutdown();

    while (!pooledExecutor.isTerminated()) {
      // et timeout non depasse ...

      System.out.println("All threads not finished.");
      try {
        Thread.sleep(1000);

        for (Future<JobStatus> status : listFutureJobStatus) {
          try {

            if (status.isDone()) {
              JobStatus jobstatus = status.get();
              listJobStatus.put(jobstatus.getJobId(), jobstatus);
            }

          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
          catch (ExecutionException e) {
            e.printStackTrace();
          }
        }

      }
      catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    //
    if (listFutureJobStatus.size() != listJobStatus.size()) {
      // throw new RuntimeException("Double-entries!!!");
      System.out.println("Error Pas le même nombre de jobs lancés et terminés.");

    }

    System.out.println("Finished all threads");

  }
}
