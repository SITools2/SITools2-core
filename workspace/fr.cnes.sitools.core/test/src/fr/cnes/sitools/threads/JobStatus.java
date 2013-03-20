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
