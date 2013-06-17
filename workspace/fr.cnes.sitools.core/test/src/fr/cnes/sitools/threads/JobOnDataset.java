 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
