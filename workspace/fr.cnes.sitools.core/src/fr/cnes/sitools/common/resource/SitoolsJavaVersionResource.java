package fr.cnes.sitools.common.resource;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;

/**
 * Returns the Java version
 * 
 * 
 * @author m.gond
 */
public class SitoolsJavaVersionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("SitoolsJavaVersionResource");
    setDescription("Gets the java version used");
  }

  /**
   * Get the Java Version used
   * 
   * @param variant
   *          the variant needed
   * @return the Java version as a {@link StringRepresentation}
   */
  @Get
  public Representation getJavaVersion(Variant variant) {
    return new StringRepresentation(System.getProperty("java.version"));
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the jetty properties");
    info.setIdentifier("retrieve_jetty_properties");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}
