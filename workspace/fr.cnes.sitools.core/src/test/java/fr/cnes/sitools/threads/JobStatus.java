 /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.UUID;

public class JobStatus {
  
  private String jobId;
  private int count = 0;
  private boolean success = false;
  
  public int getCount() {
    return count;
  }
  public void setCount(int count) {
    this.count = count;
  }
  public boolean isSuccess() {
    return success;
  }
  public void setSuccess(boolean success) {
    this.success = success;
  }
  public String getJobId() {
    return jobId;
  }
  public void setJobId(String jobId) {
    this.jobId = jobId;
  }
  
  /**
   * 
   * @param jobId
   * @param success
   * @param count
   */
  public JobStatus(String jobId, boolean success, int count) {
    super();
    this.jobId = jobId;
    this.success = success;
    this.count = count;
  }
  
  /**
   * 
   * @param success
   * @param count
   */
  public JobStatus(boolean success, int count) {
    super();
    this.jobId = UUID.randomUUID().toString();
    this.success = success;
    this.count = count;
  }
  
  
  
  
}
