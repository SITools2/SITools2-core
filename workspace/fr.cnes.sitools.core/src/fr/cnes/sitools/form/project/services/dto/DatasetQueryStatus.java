package fr.cnes.sitools.form.project.services.dto;

/**
 * Enum for status during a multidataset query
 * 
 * 
 * @author m.gond
 */
public enum DatasetQueryStatus {
  /** The request is done */
  REQUEST_DONE,
  /** The request is pending */
  REQUEST_PENDING,
  /** The request has errors */
  REQUEST_ERROR,
  /** The request is not allowed */
  UNAUTHORIZED

}
