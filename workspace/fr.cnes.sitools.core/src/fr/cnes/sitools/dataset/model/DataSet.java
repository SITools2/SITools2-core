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
package fr.cnes.sitools.dataset.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.properties.model.SitoolsProperty;
import fr.cnes.sitools.util.Property;

/**
 * Class defining a DataSet The DataSet is shared by all resources of the DataSetApplication. Do not modify the element
 * 
 * TODO : voir comment faire un dataset plugin qui permet d'accéder au dataset uniquement en lecture
 * 
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("dataset")
public final class DataSet implements IResource, Serializable {

  /** serialVersionUID */
  private static final long serialVersionUID = 3386484051740374876L;

  /** Identifier of the DataSet */
  private String id;

  /** DataSet name */
  private String name;

  /** DataSet description */
  private String description;

  /**
   * Total number of results for information
   */
  private int nbRecords;

  /**
   * Resource image attached to DataSet TODO URL externe ou interne (fichier uploaded)
   */
  private Resource image;

  /** Reference to DataSource */
  private Resource datasource;

  /** DataSet activation status (activated or not) */
  private String status;

  /** List of URLs (attachments /...) of the DataSet on the server */
  private List<Resource> expositions = null;

  /**
   * Attachment for users exposition TODO >> Provisoire en attendant les expositions
   */
  private String sitoolsAttachementForUsers = null;

  /** List of columns defining the DataSet */
  @XStreamAlias("columnModel")
  private List<Column> columnModel = new ArrayList<Column>();

  /** List of structures Tables / Views / ... */
  @XStreamAlias("structures")
  private List<Structure> structures = new ArrayList<Structure>();

  /** Date d'expiration */
  @XStreamAlias("expirationDate")
  private Date expirationDate;

  /**
   * If the dataset is visible even if it is not authorized
   */
  private boolean visible;

  /**
   * If the dataset is authorized ( DTO attribute, only for communication with the client )
   */
  private boolean authorized;

  /**
   * The datasetView
   */
  private DatasetView datasetView;

  /**
   * Structure of the dataset
   */
  private SitoolsStructure structure;

  // private SitoolsStructure structure;

  // /** Liste des formulaires */
  // XStreamAlias("forms")
  // private ArrayList<Form> forms = new ArrayList<Form>();

  /**
   * WHERE clauses attached to the DataSet :
   * 
   * values : W : Wizard S : SQL
   */
  @XStreamAlias("queryType")
  private String queryType;

  /** Text representation of the WHERE clause of the DataSet */
  @XStreamAlias("sqlQuery")
  private String sqlQuery;

  /** Structured definition of the WHERE clause of the DataSet */
  @XStreamAlias("predicat")
  private List<Predicat> predicat = new ArrayList<Predicat>();

  /** HTML Description of the DataSet **/
  private String descriptionHTML;

  /** True if a dictionary used by this dataset have been updated **/
  private Boolean dirty;

  /** Dictionary Mappings */
  private List<DictionaryMapping> dictionaryMappings;

  /** List of columns defining the DataSet */
  @XStreamAlias("properties")
  private List<SitoolsProperty> properties = new ArrayList<SitoolsProperty>();

  /** List of dataset View Config */
  @XStreamAlias("datasetViewConfig")
  private List<Property> datasetViewConfig = new ArrayList<Property>();

  /**
   * Constructor : return an initialized DataSet, depending of the Id
   * 
   * @param id
   *          identifier
   */
  public DataSet(String id) {
    // super();
    this.id = id;
  }

  /**
   * Full constructor
   * 
   * @param id
   *          identifier
   * @param name
   *          name
   * @param description
   *          description
   */
  public DataSet(String id, String name, String description) {
    // super();
    this.id = id;
    this.name = name;
    this.description = description;
  }

  /** Default constructor */
  public DataSet() {
    super();
  }

  /**
   * Add a column to DataSet
   * 
   * @param column
   *          Column
   */
  public void addColumn(Column column) {
    this.columnModel.add(column);
  }

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the number of results for the DataSet
   * 
   * @return the number of results
   */
  public int getNbRecords() {
    return nbRecords;
  }

  /**
   * Sets the number of results
   * 
   * @param nbRecords
   *          number of records
   */
  public void setNbRecords(int nbRecords) {
    this.nbRecords = nbRecords;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the image value
   * 
   * @return the image
   */
  public Resource getImage() {
    return image;
  }

  /**
   * Sets the value of image
   * 
   * @param image
   *          the image to set
   */
  public void setImage(Resource image) {
    this.image = image;
  }

  /**
   * Gets the DataSource value
   * 
   * @return the DataSource
   */
  public Resource getDatasource() {
    return datasource;
  }

  /**
   * Sets the value of DataSource
   * 
   * @param datasource
   *          the DataSource to set
   */
  public void setDatasource(Resource datasource) {
    this.datasource = datasource;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the expositions value
   * 
   * @return the expositions
   */
  public List<Resource> getExpositions() {
    return expositions;
  }

  /**
   * Sets the value of expositions
   * 
   * @param expositions
   *          the expositions to set
   */
  public void setExpositions(List<Resource> expositions) {
    this.expositions = expositions;
  }

  /**
   * Gets the sitoolsAttachementForUsers value
   * 
   * @return the sitoolsAttachementForUsers
   */
  public String getSitoolsAttachementForUsers() {
    return sitoolsAttachementForUsers;
  }

  /**
   * Sets the value of sitoolsAttachementForUsers
   * 
   * @param sitoolsAttachementForUsers
   *          the sitoolsAttachementForUsers to set
   */
  public void setSitoolsAttachementForUsers(String sitoolsAttachementForUsers) {
    this.sitoolsAttachementForUsers = sitoolsAttachementForUsers;
  }

  /**
   * Gets the columnModel value
   * <p>
   * Warning : do not modify the List
   * 
   * @return the columnModel
   */
  public List<Column> getColumnModel() {
    return columnModel;
  }

  /**
   * Sets the value of columnModel
   * <p>
   * Warning : do not modify the List
   * 
   * @param columnModel
   *          the columnModel to set
   */
  public void setColumnModel(List<Column> columnModel) {
    this.columnModel = columnModel;
  }

  /**
   * Gets the structures value
   * 
   * @return the structures
   */
  public List<Structure> getStructures() {
    return structures;
  }

  /**
   * Sets the value of structures
   * 
   * @param structures
   *          the structures to set
   */
  public void setStructures(List<Structure> structures) {
    this.structures = structures;
  }

  /**
   * Gets the queryType value
   * 
   * @return the queryType
   */
  public String getQueryType() {
    return queryType;
  }

  /**
   * Sets the value of queryType
   * 
   * @param queryType
   *          the queryType to set
   */
  public void setQueryType(String queryType) {
    this.queryType = queryType;
  }

  /**
   * Gets the sqlQuery value
   * 
   * @return the sqlQuery
   */
  public String getSqlQuery() {
    return sqlQuery;
  }

  /**
   * Sets the value of sqlQuery
   * 
   * @param sqlQuery
   *          the sqlQuery to set
   */
  public void setSqlQuery(String sqlQuery) {
    this.sqlQuery = sqlQuery;
  }

  /**
   * Gets the predicate value
   * 
   * @return the predicate
   */
  public List<Predicat> getPredicat() {
    return predicat;
  }

  /**
   * Sets the value of predicate
   * 
   * @param predicat
   *          the predicate to set
   */
  public void setPredicat(List<Predicat> predicat) {
    this.predicat = predicat;
  }

  /**
   * Gets the expirationDate value
   * 
   * @return the expirationDate
   */
  public Date getExpirationDate() {
    return expirationDate;
  }

  /**
   * Sets the value of expirationDate
   * 
   * @param expirationDate
   *          the expirationDate to set
   */
  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  /**
   * Sets the value of descriptionHTML
   * 
   * @param descriptionHTML
   *          the descriptionHTML to set
   */
  public void setDescriptionHTML(String descriptionHTML) {
    this.descriptionHTML = descriptionHTML;
  }

  /**
   * Gets the descriptionHTML value
   * 
   * @return the descriptionHTML
   */
  public String getDescriptionHTML() {
    return descriptionHTML;
  }

  /**
   * Gets the visible value
   * 
   * @return the visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the value of visible
   * 
   * @param visible
   *          the visible to set
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Gets the authorized value
   * 
   * @return the authorized
   */
  public boolean isAuthorized() {
    return authorized;
  }

  /**
   * Sets the value of authorized
   * 
   * @param authorized
   *          the authorized to set
   */
  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }

  /**
   * Gets the dirty value
   * 
   * @return the dirty
   */
  public Boolean getDirty() {
    return dirty;
  }

  /**
   * Sets the value of dirty
   * 
   * @param dirty
   *          the dirty to set
   */
  public void setDirty(Boolean dirty) {
    this.dirty = dirty;
  }

  /**
   * Gets the structure value
   * 
   * @return the structure
   */
  public SitoolsStructure getStructure() {
    return structure;
  }

  /**
   * Sets the value of structure
   * 
   * @param structure
   *          the structure to set
   */
  public void setStructure(SitoolsStructure structure) {
    this.structure = structure;
  }

  /**
   * Gets the datasetView value
   * 
   * @return the datasetView
   */
  public DatasetView getDatasetView() {
    return datasetView;
  }

  /**
   * Sets the value of datasetView
   * 
   * @param datasetView
   *          the datasetView to set
   */
  public void setDatasetView(DatasetView datasetView) {
    this.datasetView = datasetView;
  }

  /**
   * Gets the column with a header
   * 
   * @param columnAlias
   *          the columnAlias looked at
   * @return Column
   */
  public Column findByColumnAlias(String columnAlias) {
    Column column = null;
    Column result = null;
    for (Iterator<Column> it = this.columnModel.iterator(); it.hasNext();) {
      column = it.next();
      if (columnAlias.equals(column.getColumnAlias())) {
        result = column;
      }
    }
    return result;

  }

  /**
   * Get the list of default column set to visible
   * 
   * @return List<Column> a list of column
   */
  public List<Column> getDefaultColumnVisible() {
    List<Column> results = new ArrayList<Column>();
    List<Column> model = getColumnModel();
    for (Column column : model) {
      if ((column.isVisible() != null && column.isVisible())
          || (column.isPrimaryKey() || BehaviorEnum.noClientAccess.equals(column.getColumnRenderer()))) {
        if (column.getSpecificColumnType() == SpecificColumnType.DATABASE
            || column.getSpecificColumnType() == SpecificColumnType.SQL) {
          results.add(column);
        }
      }
    }
    return results;
  }

  // /**
  // * Refresh notions of dictionaries
  // *
  // * @param context
  // * the restlet context
  // * @param appDictionaries
  // * the dictionary application name
  // */
  // public void refreshNotion(Context context, String appDictionaries) {
  // List<Column> columns = this.getColumnModel();
  // for (Column column : columns) {
  // if (column.getNotion() != null) {
  // Notion notion = column.getNotion();
  // Dictionary newDico = RIAPUtils.getObject(notion.getDictionaryId(), appDictionaries, context);
  // Boolean finded = false;
  // if (newDico != null) {
  // for (Notion newNotion : newDico.getNotions()) {
  // if (newNotion.getId().equals(notion.getId())) {
  // column.setNotion(newNotion);
  // finded = true;
  // break;
  // }
  // }
  // if (!finded) {
  // column.setNotion(null);
  // }
  //
  // }
  // else {
  // column.setNotion(null);
  // }
  // }
  //
  // }
  // this.setDirty(false);
  // }

  /**
   * Get the list of columns "order-by"-ed
   * 
   * @return the list of columns where the order by is defined
   */
  public List<Column> getColumnOrderBy() {
    List<Column> colOrderBy = new ArrayList<Column>();
    for (Column col : columnModel) {
      if (col.getOrderBy() != null && !"".equals(col.getOrderBy())) {
        colOrderBy.add(col);
      }
    }
    return colOrderBy;
  }

  /**
   * Sets the value of dictionaryMappings
   * 
   * @param dictionaryMappings
   *          the dictionaryMappings to set
   */
  public void setDictionaryMappings(List<DictionaryMapping> dictionaryMappings) {
    this.dictionaryMappings = dictionaryMappings;
  }

  /**
   * Gets the dictionaryMappings value
   * 
   * @return the dictionaryMappings
   */
  public List<DictionaryMapping> getDictionaryMappings() {
    return dictionaryMappings;
  }

  /**
   * Gets the list of dictionary identifiers involved in a mapping
   * 
   * @return the list of dictionary identifiers
   */
  public List<String> getDictionaryIds() {
    List<String> ids = new ArrayList<String>();
    if (this.getDictionaryMappings() != null) {
      for (Iterator<DictionaryMapping> iterator = this.getDictionaryMappings().iterator(); iterator.hasNext();) {
        DictionaryMapping mapping = iterator.next();
        ids.add(mapping.getDictionaryId());
      }
    }
    return ids;
  }

  /**
   * Gets the DictionaryMapping corresponding to the given dictionaryId
   * 
   * @param dictionaryId
   *          the id of the dictionary
   * @return the DictionaryMapping corresponding to the given dictionaryId
   */
  public DictionaryMapping getDictionaryMapping(String dictionaryId) {
    DictionaryMapping mappingRes = null;
    if (this.getDictionaryMappings() != null) {
      for (Iterator<DictionaryMapping> iterator = this.getDictionaryMappings().iterator(); iterator.hasNext();) {
        DictionaryMapping mapping = iterator.next();
        if (mapping.getDictionaryId().equals(dictionaryId)) {
          mappingRes = mapping;
          break;
        }
      }
    }
    return mappingRes;
  }

  /**
   * Gets the properties value
   * 
   * @return the properties
   */
  public List<SitoolsProperty> getProperties() {
    return properties;
  }

  /**
   * Sets the value of properties
   * 
   * @param properties
   *          the properties to set
   */
  public void setProperties(List<SitoolsProperty> properties) {
    this.properties = properties;
  }

  /**
   * Get a {@link SitoolsProperty} for a given propertyName
   * 
   * @param propertyName
   *          the name of the property
   * @return the DataSetProperty with the given name or null if it is not found
   */
  public SitoolsProperty getProperty(String propertyName) {
    SitoolsProperty property = null;
    if (this.properties != null) {
      for (SitoolsProperty prop : this.properties) {
        if (prop.getName().equals(propertyName)) {
          property = prop;
          break;
        }
      }
    }
    return property;

  }

  /**
   * Gets the datasetViewConfig value
   * @return the datasetViewConfig
   */
  public List<Property> getDatasetViewConfig() {
    return datasetViewConfig;
  }

  /**
   * Sets the value of datasetViewConfig
   * @param datasetViewConfig the datasetViewConfig to set
   */
  public void setDatasetViewConfig(List<Property> datasetViewConfig) {
    this.datasetViewConfig = datasetViewConfig;
  }

}
