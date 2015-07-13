package fr.cnes.sitools.security.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link RequestCounter} implementation based on a HashMap
 * 
 * 
 * @author m.gond
 */
public class RequestCounterHashSet implements RequestCounter {

  /** in memory list of banished IP */
  private Map<String, Integer> hashMap = Collections.synchronizedMap(new HashMap<String, Integer>());

  @Override
  public int getNumberOfRequests(String id) {
    Integer nb = hashMap.get(id);
    if (nb == null) {
      return 0;
    }
    else {
      return nb;
    }
  }

  @Override
  public void addRequest(String id) {
    Integer nb = hashMap.get(id);
    if (nb == null) {
      nb = 0;
    }
    nb++;
    hashMap.put(id, nb);
  }

  @Override
  public void remove(String id) {
    hashMap.remove(id);
  }

  @Override
  public void initNumberOfRequest(String id, int nbRequests) {
    hashMap.put(id, nbRequests);
  }

}
