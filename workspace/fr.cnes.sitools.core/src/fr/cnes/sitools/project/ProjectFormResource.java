package fr.cnes.sitools.project;

import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Return the list of forms for a dataset
 * 
 * 
 * @author m.gond
 */
public class ProjectFormResource extends AbstractProjectResource {

  private ProjectApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("DataSetFormResource");
    setDescription("Resource that return a form for a given DataSet");
  }

  /**
   * Get the list of forms for the project
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of Forms
   */
  @Get
  public Representation getFormsList(Variant variant) {

    String formId = (String) getRequest().getAttributes().get("formId");

    Representation rep = null;

    application = (ProjectApplication) getApplication();
    String projectId = application.getProjectId();

    FormProject formOutput = getForm(projectId, formId);

    Response response = new Response(true, formOutput, FormProject.class, "formProject");
    rep = getRepresentation(response, variant);

    return rep;
  }

  /**
   * Get the list of forms for a dataset
   * 
   * @param id
   *          the id of the dataset
   * @return a Response containing the list of Forms
   */
  private FormProject getForm(String projectId, String formId) {
    List<FormProject> listForm = RIAPUtils.getListOfObjects(
        application.getSettings().getString(Consts.APP_PROJECTS_URL) + "/" + projectId
            + application.getSettings().getString(Consts.APP_FORMPROJECT_URL), getContext());

    FormProject out = null;
    for (FormProject FormProject : listForm) {
      if (FormProject.getId().equals(formId)) {
        out = FormProject;
        break;
      }
    }

    return out;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of forms associated to the dataset.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
