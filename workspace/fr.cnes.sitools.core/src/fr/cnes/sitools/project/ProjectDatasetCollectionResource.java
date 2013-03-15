package fr.cnes.sitools.project;

import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.project.model.Project;

/**
 * Resource for adding dataset into the project Used by dynamic resources.
 * 
 * @author jp.boignard
 */
public class ProjectDatasetCollectionResource extends AbstractProjectResource {
  @Override
  public void sitoolsDescribe() {
    setNegotiated(false);
    setName("ProjectDatasetCollectionResource");
    setDescription("Resource for adding dataset into the project Used by dynamic resources.");
  }

  /**
   * Update / Validate existing project
   * 
   * @param representation
   *          Project representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newDataset(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "PROJECT_REPRESENTATION_REQUIRED");
    }
    try {
      Response response = null;
      // Parse object representation
      Resource resourceInput = getObjectResource(representation, variant);

      // Business service
      Project projectOutput = getStore().retrieve(getProjectId());

      if (projectOutput.getDataSets() == null) {
        projectOutput.setDataSets(new ArrayList<Resource>());
      }
      projectOutput.getDataSets().add(resourceInput);
      getStore().update(projectOutput);

      // Register Project as observer of datasets resources
      registerObserver(projectOutput);

      // Response
      response = new Response(true, resourceInput, Resource.class, "resource");

      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(projectOutput.getId());
      notification.setStatus("UPDATED");
      notification.setEvent("PROJECT_UPDATED");
      notification.setMessage("project datasets updated.");
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to add a new dataset to the project.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
