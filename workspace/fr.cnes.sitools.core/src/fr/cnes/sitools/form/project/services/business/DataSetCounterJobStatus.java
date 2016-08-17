    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.form.project.services.business;

import java.util.UUID;

import fr.cnes.sitools.form.project.services.dto.DataSetQueryStatusDTO;

/**
 * Class to represent a Job Status during a Job of DataSet count
 * 
 * 
 * @author m.gond
 */
public class DataSetCounterJobStatus {
  /** The id of the Job */
  private String jobId;
  /** The result of the Job */
  private DataSetQueryStatusDTO result;
  /** Whether the Job was successful or not */
  private boolean success = false;

  /**
   * Constructor with JobId, success and result
   * 
   * @param jobId
   *          the id of the Job
   * @param success
   *          the success of the Job
   * @param jobResult
   *          the result of the Job
   */
  public DataSetCounterJobStatus(String jobId, boolean success, DataSetQueryStatusDTO jobResult) {
    super();
    this.jobId = jobId;
    this.success = success;
    this.setResult(jobResult);
  }

  /**
   * Constructor with success and JobResult, Id is generated
   * 
   * @param success
   *          the success of the Job
   * @param jobResult
   *          the result of the Job
   */
  public DataSetCounterJobStatus(boolean success, DataSetQueryStatusDTO jobResult) {
    super();
    this.jobId = UUID.randomUUID().toString();
    this.success = success;
    this.setResult(jobResult);
  }

  /**
   * Gets the jobId value
   * 
   * @return the jobId
   */
  public String getJobId() {
    return jobId;
  }

  /**
   * Sets the value of jobId
   * 
   * @param jobId
   *          the jobId to set
   */
  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  /**
   * Gets the success value
   * 
   * @return the success
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets the value of success
   * 
   * @param success
   *          the success to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Sets the value of result
   * 
   * @param result
   *          the result to set
   */
  public void setResult(DataSetQueryStatusDTO result) {
    this.result = result;
  }

  /**
   * Gets the result value
   * 
   * @return the result
   */
  public DataSetQueryStatusDTO getResult() {
    return result;
  }

}
