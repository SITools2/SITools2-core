/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.dataset.converter.ClientConverter;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.database.common.DataSetCountResource;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerResource;
import fr.cnes.sitools.dataset.dto.ColumnConceptMappingDTO;
import fr.cnes.sitools.dataset.dto.DictionaryMappingDTO;
import fr.cnes.sitools.dataset.filter.ClientFilter;
import fr.cnes.sitools.dataset.filter.business.FilterChained;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.dataset.opensearch.OpenSearch;
import fr.cnes.sitools.dataset.opensearch.OpensearchDescriptionResource;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceFactory;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.feeds.FeedsClientResource;
import fr.cnes.sitools.plugins.resources.ListPluginResource;
import fr.cnes.sitools.properties.PropertyFilterResource;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Application for exposing DataSets (one instance of DataSetApplication per DataSet)
 * 
 * TODO Constructor with all generic security configuration (Authenticator informations)
 * 
 * @author AKKA
 * 
 */
public final class DataSetApplication extends AbstractDataSetApplication {

  /** The DataSet model object */
  private DataSet dataSet = null;

  /** Converter associated to that DataSet */
  private ConverterChained converterChained = null;

  /** Converter associated to that DataSet */
  private FilterChained filterChained = null;

  /** List of dictionary mappings */
  private List<DictionaryMappingDTO> dictionaryMappings = null;

  /**
   * Constructor with a DataSet id
   * 
   * @param context
   *          RESTlet Host context
   * @param dataSetId
   *          DataSet identifier
   */
  public DataSetApplication(Context context, String dataSetId) {
    super(context);
    this.datasetId = dataSetId;
    dataSet = store.retrieve(datasetId);

    Context converterContext = context.createChildContext();
    converterContext.getAttributes().put("DataSetApplication", this);
    converterContext.getAttributes().put(ContextAttributes.SETTINGS, getSettings());

    ConverterChainedModel converterChainedModel = ClientConverter.getConverterChainedModel(converterContext,
        this.datasetId);
    if (converterChainedModel != null) {
      converterChained = ClientConverter.getConverterChained(converterContext, converterChainedModel);
    }

    Context filterContext = context.createChildContext();
    filterContext.getAttributes().put("DataSetApplication", this);
    filterContext.getAttributes().put(ContextAttributes.SETTINGS, getSettings());

    FilterChainedModel filterChainedModel = ClientFilter.getFilterChainedModel(filterContext, this.datasetId);
    if (filterChainedModel != null) {
      filterChained = ClientFilter.getFilterChained(filterContext, filterChainedModel);
    }

    // chargement des DTO de mapping dictionaires
    dictionaryMappings = loadDictionaryMapping();

    // let's put the properties in the Context of the application
    if (dataSet.getProperties() != null) {
      this.getContext().getAttributes().put(ContextAttributes.LIST_SITOOLS_PROPERTIES, dataSet.getProperties());
    }

    // Description de cette instance dataSetId d'application.
    sitoolsDescribe();
    register();
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    // si appelé par le constructeur parent => datasetId encore null ici
    if (dataSet == null) {
      setName("DataSetApplication");
      setDescription("Exposition of datasets");
    }
    else {
      setId(dataSet.getId());
      setName(dataSet.getName());
      setDescription("Exposition of the dataset " + dataSet.getName() + "\n"
          + "-> Administrator must have all authorizations on this application\n"
          + "-> Public user must have at least GET/PUT/DELETE authorizations on this application\n"
          + "PUT and DELETE authorizations are for SvaTasks managment");
    }
    setCategory(Category.USER_DYNAMIC);
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    router.attachDefault(DataSetExpositionResource.class);
    router.attach("/mappings", DataSetExpositionResource.class);
    router.attach("/mappings/{dictionaryId}", DataSetExpositionResource.class);

    router.attach("/records", DataSetExplorerResource.class);
    router.attach("/records/{record}", DataSetExplorerResource.class);
    router.attach("/count", DataSetCountResource.class);

    router.attach("/monitoring", DataSetMonitoringResource.class);

    // TODO Attacher la resource dynamiquement à l'activation de l'opensearch
    router.attach("/opensearch.xml", OpensearchDescriptionResource.class);
    router.attach("/opensearch/search", OpenSearch.class);
    router.attach("/opensearch/suggest", OpenSearch.class);

    router.attach("/rss.xml", DatasetRSSResource.class);
    router.attach("/clientFeeds/{feedsId}", FeedsClientResource.class);

    // List of forms
    router.attach("/forms", DataSetListFormsResource.class);

    // List of resources
    router.attach("/services", ListPluginResource.class);

    // List of feeds
    router.attach("/feeds", DataSetListFeedsResource.class);

    router.attach("/checkProperties", PropertyFilterResource.class);

    // attach dynamic resources
    attachParameterizedResources(router);

    return router;
  }

  /**
   * Gets the SitoolsDataSource
   * 
   * @return SitoolsDataSource
   */
  public SitoolsDataSource getDataSource() {
    // Ne pas enregistrer en variable - objet datasource recréé à chaque
    // activation
    String id = dataSet.getDatasource().getId();
    return SitoolsDataSourceFactory.getDataSource(id);
  }

  /**
   * Gets the DataSet object model
   * 
   * @return DataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  @Override
  public void attachDataSet(DataSet ds) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public void detachDataSet(DataSet ds) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  @Override
  public void detachDataSetDefinitif(DataSet ds) {
    // TODO Auto-generated method stub
    // NE PAS IMPLEMENTER >> uniquement via l'administration
  }

  /**
   * Gets the converterChained value
   * 
   * @return the converterChained
   */
  public ConverterChained getConverterChained() {
    return converterChained;
  }

  /**
   * Gets the filterChained value
   * 
   * @return the filterChained
   */
  public FilterChained getFilterChained() {
    return filterChained;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#start()
   */
  @Override
  public synchronized void start() throws Exception {
    super.start();
    if (isStarted()) {
      DataSet dataset = store.retrieve(getId());
      if (dataset != null) {
        dataset.setStatus("ACTIVE");
        store.update(dataset);
      }
    }
    else {
      getLogger().warning("DataSetApplication should be started.");
      DataSet dataset = store.retrieve(getId());
      if (dataset != null) {
        dataset.setStatus("INACTIVE");
        store.update(dataset);
      }
    }
  }

  @Override
  public synchronized void stop() throws Exception {
    super.stop();
    if (isStopped()) {
      DataSet dataset = store.retrieve(getId());
      if (dataset != null) {
        dataset.setStatus("INACTIVE");
        store.update(dataset);
      }
    }
    else {
      getLogger().warning("DataSetApplication should be stopped.");
      DataSet dataset = store.retrieve(getId());
      if (dataset != null) {
        dataset.setStatus("ACTIVE");
        store.update(dataset);
      }
    }

  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Dataset administration of this dataset");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

  /**
   * Load the dictionary Mapping of the Dataset
   * 
   * @return the list of DictionaryMapping
   */
  private List<DictionaryMappingDTO> loadDictionaryMapping() {
    ArrayList<DictionaryMappingDTO> mappings = new ArrayList<DictionaryMappingDTO>();

    getLogger().log(Level.INFO, "Load dictionary mapping for the dataset " + dataSet.getName());

    if (dataSet.getDictionaryMappings() == null) {
      return null;
    }

    // create columnMap for direct search
    HashMap<String, Column> colMap = new HashMap<String, Column>();
    for (Column col : dataSet.getColumnModel()) {
      colMap.put(col.getId(), col);
    }

    for (Iterator<DictionaryMapping> itDicoMapping = dataSet.getDictionaryMappings().iterator(); itDicoMapping
        .hasNext();) {
      DictionaryMapping dicoMapping = itDicoMapping.next();

      Dictionary dico = RIAPUtils.getObject(dicoMapping.getDictionaryId(),
          getSettings().getString(Consts.APP_DICTIONARIES_URL), getContext());

      if (dico == null) {
        getLogger().warning("Dictionary : " + dicoMapping.getDictionaryId() + " can't be found");
      }
      else {
        DictionaryMappingDTO dicoMapDTO = new DictionaryMappingDTO();
        dicoMapDTO.setDictionaryId(dicoMapping.getDictionaryId());
        dicoMapDTO.setDictionaryName(dico.getName());
        dicoMapDTO.setDefaultDico(dicoMapping.isDefaultDico());
        if (dico.getConcepts() != null) {

          for (Iterator<ColumnConceptMapping> itColConcept = dicoMapping.getMapping().iterator(); itColConcept
              .hasNext();) {
            ColumnConceptMapping colConcept = itColConcept.next();

            for (Iterator<Concept> itConcept = dico.getConcepts().iterator(); itConcept.hasNext();) {
              Concept concept = itConcept.next();
              if (colConcept.getConceptId().equals(concept.getId())) {
                dicoMapDTO.addMapping(createColConceptMappingDTO(concept, colConcept, colMap));
              }
            }

          }
        }
        mappings.add(dicoMapDTO);
      }
    }
    return mappings;

  }

  /**
   * Create a ColumnConceptMappingDTO from a Concept and a ColumnConceptMapping using a Map of column
   * 
   * @param concept
   *          the Concept
   * @param colConceptMap
   *          the ColumnConceptMapping
   * @param colMap
   *          the column Map
   * @return a new ColumnConceptMappingDTO
   */
  private ColumnConceptMappingDTO createColConceptMappingDTO(Concept concept, ColumnConceptMapping colConceptMap,
      HashMap<String, Column> colMap) {
    ColumnConceptMappingDTO colConceptDTO = new ColumnConceptMappingDTO();
    Column col = colMap.get(colConceptMap.getColumnId());
    colConceptDTO.setColumnAlias(col.getColumnAlias());
    colConceptDTO.setConcept(concept);
    return colConceptDTO;
  }

  /**
   * Sets the value of dictionaryMappings
   * 
   * @param dictionaryMappings
   *          the dictionaryMappings to set
   */
  public void setDictionaryMappings(List<DictionaryMappingDTO> dictionaryMappings) {
    this.dictionaryMappings = dictionaryMappings;
  }

  /**
   * Gets the dictionaryMappings value
   * 
   * @return the dictionaryMappings
   */
  public List<DictionaryMappingDTO> getDictionaryMappings() {
    return dictionaryMappings;
  }

  /**
   * Get the DictionaryMappingDTO from the mapping of this dataset from its dictionaryName
   * 
   * @param dictionaryName
   *          the name of the dictionary
   * @return the DictionaryMappingDTO if there is a DictionaryMappingDTO corresponding to the following dictionaryName
   *         or null otherwise
   */
  public DictionaryMappingDTO getColumnConceptMappingDTO(String dictionaryName) {
    DictionaryMappingDTO geoDicoMapping = null;
    if (this.getDictionaryMappings() != null) {
      for (Iterator<DictionaryMappingDTO> iterator = this.getDictionaryMappings().iterator(); iterator.hasNext()
          && geoDicoMapping == null;) {
        DictionaryMappingDTO mapping = iterator.next();
        if (mapping.getDictionaryName().equals(dictionaryName)) {
          geoDicoMapping = mapping;
        }
      }
    }
    return geoDicoMapping;
  }

}
