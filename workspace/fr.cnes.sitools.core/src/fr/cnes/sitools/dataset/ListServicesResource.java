package fr.cnes.sitools.dataset;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to get the list of services on a Dataset
 * 
 * 
 * @author m.gond
 */
public class ListServicesResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("ListServicesResource");
    setDescription("Gets the list of services for the dataset");
  }

  /**
   * Get the list of services for the dataset
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of resources
   */
  @Get
  public Representation getResourcesList(Variant variant) {

    DataSetApplication application = (DataSetApplication) getApplication();
    SitoolsSettings settings = application.getSettings();
    DataSet dataset = application.getDataSet();

    String url = settings.getString(Consts.APP_DATASETS_URL) + "/" + dataset.getId()
        + settings.getString(Consts.APP_SERVICES_URL);
    ServiceCollectionModel serviceCollection = RIAPUtils.getObject(url, getContext());

    Response response = new Response(true, serviceCollection, ServiceCollectionModel.class, "ServiceCollectionModel");

    return getRepresentation(response, variant);
  }
}
