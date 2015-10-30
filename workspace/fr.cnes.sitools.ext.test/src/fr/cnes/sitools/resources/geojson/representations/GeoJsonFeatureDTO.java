package fr.cnes.sitools.resources.geojson.representations;

import java.util.HashMap;
import java.util.Map;

public class GeoJsonFeatureDTO {

  private String id;

  private Map<String, Object> properties = new HashMap<String, Object>();

  private Object geometry;

  private Map<String, Object> services = new HashMap<String, Object>();


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public Object getGeometry() {
    return geometry;
  }

  public void setGeometry(Object geometry) {
    this.geometry = geometry;
  }

  public Map<String, Object> getServices() {
    return services;
  }

  public void setServices(Map<String, Object> services) {
    this.services = services;
  }
}
