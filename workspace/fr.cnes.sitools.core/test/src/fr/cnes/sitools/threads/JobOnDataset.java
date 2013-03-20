package fr.cnes.sitools.threads;

import java.util.concurrent.Callable;

import fr.cnes.sitools.common.model.Resource;

/**
 * 
 *
 * @author jp.boignard (AKKA Technologies)
 */
public class JobOnDataset implements Callable<JobStatus> {

  /**
   * Resource
   * @param datasetResource
   */
  private Resource datasetResource = null;
  
  /**
   * Constructor
   * @param datasetResource
   */
  public JobOnDataset(Resource datasetResource) {
    super();
    this.datasetResource = datasetResource;
  }

  @Override
  public JobStatus call() throws Exception {
    
    System.out.println( "Job started on resource " + datasetResource.getId());
    
   // for tests... 
   int nbSec = Integer.parseInt(datasetResource.getId());
    
    try {
      // wait(nbSec + 1000);
      Thread.sleep(nbSec * 1000);
    }
    catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println( "Job finished on resource " + datasetResource.getId());

    return new JobStatus(true, nbSec);
  }

  
  
}
