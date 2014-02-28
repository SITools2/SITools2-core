package fr.cnes.sitools.security.filter;

public interface RequestCounter {

  public int getNumberOfRequests(String id);

  public void addRequest(String id);
  
  public void remove(String id);
  
  public void initNumberOfRequest(String id, int nbRequests);

}
