package fr.cnes.sitools.plugins.guiservices.implement;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;

/**
 * Resource to manage collection of guiservices on a specific parent id
 * 
 * 
 * @author m.gond
 */
public class GuiServicePluginCollectionResource extends AbstractGuiServicePluginResource {

  @Override
  public void sitoolsDescribe() {
    setName("GuiServicePluginCollectionResource");
    setDescription("Resource to deal with collection of GuiService plugin");
    setNegotiated(false);
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of GuiServices plugin for a specific parent Id");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Create / attach a new resource to an application
   * 
   * @param representation
   *          The representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newGuiServicePluginPlugin(Representation representation, Variant variant) {
    try {

      GuiServicePluginModel guiServicePluginInput = getObject(representation);

      // Business service
      guiServicePluginInput.setParent(getParentId());

      // Response
      // fillParametersMap(resourceInput);

      GuiServicePluginModel guiServicePluginOutput = getStore().create(guiServicePluginInput);

//      // Notify observers
//      Notification notification = new Notification();
//      notification.setObservable(getGuiServicePluginId());
//      notification.setEvent("GUI_SERVICE_PLUGIN_ADDED");
//      notification.setMessage("guiserviceplugin.add.success");
//      notification.setEventSource(guiServicePluginOutput);
//      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      Response response = new Response(true, guiServicePluginOutput, GuiServicePluginModel.class, "guiServicePlugin");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      e.printStackTrace();
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new GuiService sending its representation.");
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
