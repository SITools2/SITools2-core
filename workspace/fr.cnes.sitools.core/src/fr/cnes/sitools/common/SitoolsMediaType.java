package fr.cnes.sitools.common;

import org.restlet.data.MediaType;

/**
 * Specific MediaTypes for Sitools2
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class SitoolsMediaType {

  /** used by riap sitools2 requests */
  public static final MediaType APPLICATION_JAVA_OBJECT_SITOOLS_MODEL = MediaType.register(
      "application/x-java-serialized-object+sitools-model", "MediaType for Internal Sitools Object Model");

  /** used by Resource datasets in projects */
  public static final MediaType APPLICATION_JAVA_OBJECT_SITOOLS_MODEL_RESOURCE = MediaType.register(
      "application/x-java-serialized-object+sitools-model-resource", "MediaType for Internal Sitools Object Model");

  /** used by client */
  public static final MediaType APPLICATION_SITOOLS_JSON_DATASET = MediaType.register(
      "application/json+sitools-dataset", "MediaType for Sitools DataSet Json Representation");

  /** used by client */
  public static final MediaType APPLICATION_SITOOLS_JSON_ORDER = MediaType.register(
      "application/json+sitools-order", "MediaType for Sitools Order Json Representation");
  
  /** used by client */
  public static final MediaType APPLICATION_SITOOLS_JSON_DIRECTORY = MediaType.register(
      "application/json+sitools-directory", "MediaType for Sitools Directory Json Representation");
 
  
  /**
   * private constructor
   */
  private SitoolsMediaType() {
    super();
  }

}
