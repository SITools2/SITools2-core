package fr.cnes.sitools.security.filter;

/**
 * Interface to count requests.
 * 
 * @author m.gond
 */
public interface RequestCounter {

  /**
   * Gets the number of requests for a specific identifier
   * 
   * @param id
   *          the id
   * @return the number of requests
   */
  int getNumberOfRequests(String id);

  /**
   * Adds the request.
   * 
   * @param id
   *          the id
   */
  void addRequest(String id);

  /**
   * Removes the.
   * 
   * @param id
   *          the id
   */
  void remove(String id);

  /**
   * Inits the number of request.
   * 
   * @param id
   *          the id
   * @param nbRequests
   *          the nb requests
   */
  void initNumberOfRequest(String id, int nbRequests);

}
