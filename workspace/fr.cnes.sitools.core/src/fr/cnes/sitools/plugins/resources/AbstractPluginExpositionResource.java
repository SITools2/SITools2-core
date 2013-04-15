package fr.cnes.sitools.plugins.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.util.DateUtils;

public abstract class AbstractPluginExpositionResource extends SitoolsResource {

  /**
   * Apply some regExp on the given list of parameters
   * <p>
   * It is in charge of changing a template list $date[<date_format>] in the current date formated with <date_format>
   * </p>
   * 
   * @param parameters
   *          the list of parameters to use
   */
  protected void applyRegExpOnParameters(List<ResourceParameter> parameters) {
    for (ResourceParameter resourceParameter : parameters) {
      if ("xs:template".equals(resourceParameter.getValueType())) {
        String value = resourceParameter.getValue();
        if (value.contains("${date:")) {
          int beginTemplateIndex = value.indexOf("${date:");
          int endTemplateIndex = value.indexOf("}", beginTemplateIndex);
          String dateTemplate = value.substring(beginTemplateIndex + "${date:".length(), endTemplateIndex);
          Date date = new Date();
          // TODO voir une fois que les dates seront un peu plus claire
          String dateFormated = DateUtils.format(date, dateTemplate);
          resourceParameter.setValue(value.replace("${date:" + dateTemplate + "}", dateFormated));
        }
      }
    }
  }

  /**
   * Get a ResourceModelDTO from a ResourceModel
   * 
   * @param resource
   *          the ResourceModel
   * @return a ResourceModelDTO
   */
  protected ResourceModelDTO getResourceModelDTO(ResourceModel resource) {
    ResourceModelDTO current = new ResourceModelDTO();
    current.setName(resource.getName());
    current.setDescription(resource.getDescription());
    current.setParameters(getResourceParameters(resource.getParametersMap().values()));
    current.setDescriptionAction(resource.getDescriptionAction());
    current.setDataSetSelection(resource.getDataSetSelection());
    current.setBehavior(resource.getBehavior());
    return current;
  }

  /**
   * Create a new {@link List} of {@link ResourceParameter} which are copies of the given {@link List}
   * 
   * @param values
   *          the {@link List} of {@link ResourceParameter} to copy
   * @return new {@link List} of {@link ResourceParameter}
   */
  private List<ResourceParameter> getResourceParameters(Collection<ResourceParameter> values) {
    List<ResourceParameter> parameters = new ArrayList<ResourceParameter>();
    for (ResourceParameter resourceParameter : values) {
      parameters.add(new ResourceParameter(resourceParameter));
    }
    return parameters;
  }

}
