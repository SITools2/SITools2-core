     /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.database;

import java.util.List;

import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Multisort;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.jdbc.model.Structure;

/**
 * Object to store the parameters for a particular Database request
 * 
 * @author m.gond (AKKA Technologies)
 * 
 *         <a
 *         href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3355053&group_id=531341">[#3355053]</a><br/>
 *         2011/07/06 d.arpin {Change the name and the return of the method getColumnFromQuery ->
 *         getColumnFromDistinctQuery} <br/>
 * 
 * 
 */
public class DatabaseRequestParameters {

  /** Constant used when returning first page ( no limit specified ) */
  public static final int RETURN_FIRST_PAGE = -2;
  /** Return datasource limit ( limit=-1 specified ) */
  public static final int RETURN_ALL = -1;

  /** The DataSource */
  private SitoolsDataSource db;
  /** The startIndex */
  private int startIndex;
  /** true if the request must return distinct records. Used for auto-completion. */
  private Boolean distinct;
  /** Indicates if the count is done */
  private boolean isCountDone;
  /** The DataSet */
  private DataSet dataset;
  /** The datasetId */
  private String datasetId;
  /** The list of predicates */
  private List<Predicat> predicats;
  /** the list of structures */
  private List<Structure> structures;
  /** orderBy */
  private Multisort orderBy;
  /** the from column list in case of a distinct Query */
  private Column columnFromDistinctQuery;
  /** The list of SQL visible column */
  private List<Column> sqlVisibleColumns;
  /** the pagination needed */
  private int paginationExtend;

  /** max results to send */
  private int maxrows;

  /** the base reference */
  private volatile String baseRef;

  /** List of Ids needed */
  private String[] idList;

  /** list of ranges needed */
  private List<Range> ranges;

  /** list of all visible columns */
  private List<Column> allVisibleColumns;

  /**
   * default parameter
   */
  public DatabaseRequestParameters() {
  }

  /**
   * Create a new DatabaseRequestParameters
   * 
   * @param db
   *          the DataSource
   * @param startIndex
   *          the start index
   * @param distinct
   *          true if the request must return distinct records. Used for auto-completion.
   * @param isCountDone
   *          Indicates if the count is done
   * @param dataset
   *          The DataSet
   * @param predicats
   *          The list of predicates
   * @param structures
   *          the list of structures
   * @param orderBy
   *          orderBy
   * @param columnFromDistinctQuery
   *          the from column list
   * @param sqlVisibleColumns
   *          the visible column list
   * @param paginationExtend
   *          the pagination needed
   * @param baseRef
   *          baseRef
   * @param idList
   *          the list of ids needed
   * @param ranges
   *          the list of range needed
   * @param allVisibleColumns
   *          list of all visible columns
   */
  public DatabaseRequestParameters(SitoolsDataSource db, int startIndex, Boolean distinct, boolean isCountDone,
      DataSet dataset, List<Predicat> predicats, List<Structure> structures, Multisort orderBy,
      Column columnFromDistinctQuery, List<Column> sqlVisibleColumns, int paginationExtend, String baseRef,
      String[] idList, List<Range> ranges, List<Column> allVisibleColumns) {
    this.db = db;
    this.startIndex = startIndex;
    this.distinct = distinct;
    this.isCountDone = isCountDone;
    this.dataset = dataset;
    this.predicats = predicats;
    this.structures = structures;
    this.orderBy = orderBy;
    this.columnFromDistinctQuery = columnFromDistinctQuery;
    this.sqlVisibleColumns = sqlVisibleColumns;
    this.paginationExtend = paginationExtend;
    this.baseRef = baseRef;

    this.datasetId = dataset.getId();
    this.setIdList(idList);

    this.setRanges(ranges);
    this.setAllVisibleColumns(allVisibleColumns);

  }

  /**
   * Gets the DataSource value
   * 
   * @return the DataSource
   */
  public final SitoolsDataSource getDb() {
    return db;
  }

  /**
   * Sets the value of DataSource
   * 
   * @param db
   *          the DataSource to set
   */
  public final void setDb(SitoolsDataSource db) {
    this.db = db;
  }

  /**
   * Gets the startIndex value
   * 
   * @return the startIndex
   */
  public final int getStartIndex() {
    return startIndex;
  }

  /**
   * Sets the value of startIndex
   * 
   * @param startIndex
   *          the startIndex to set
   */
  public final void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  /**
   * Gets the distinct value
   * 
   * @return the distinct
   */
  public final Boolean getDistinct() {
    return distinct;
  }

  /**
   * Sets the value of distinct
   * 
   * @param distinct
   *          the distinct to set
   */
  public final void setDistinct(Boolean distinct) {
    this.distinct = distinct;
  }

  /**
   * Gets the isCountDone value
   * 
   * 
   * @return the isCountDone
   */
  public final boolean isCountDone() {
    return isCountDone;
  }

  /**
   * Sets the value of isCountDone
   * <p>
   * Set it to true to do a count before the request, false otherwise
   * </p>
   * 
   * @param isCountDone
   *          the isCountDone to set
   */
  public final void setCountDone(boolean isCountDone) {
    this.isCountDone = isCountDone;
  }

  /**
   * Gets the DataSet value
   * 
   * @return the DataSet
   */
  public final DataSet getDataset() {
    return dataset;
  }

  /**
   * Sets the value of DataSet
   * 
   * @param dataset
   *          the DataSet to set
   */
  public final void setDataset(DataSet dataset) {
    this.dataset = dataset;
  }

  /**
   * Gets the predicates value
   * 
   * @return the predicates
   */
  public final List<Predicat> getPredicats() {
    return predicats;
  }

  /**
   * Sets the value of predicates
   * 
   * @param predicats
   *          the predicates to set
   */
  public final void setPredicats(List<Predicat> predicats) {
    this.predicats = predicats;
  }

  /**
   * Gets the structures value
   * 
   * @return the structures
   */
  public final List<Structure> getStructures() {
    return structures;
  }

  /**
   * Sets the value of structures
   * 
   * @param structures
   *          the structures to set
   */
  public final void setStructures(List<Structure> structures) {
    this.structures = structures;
  }

  /**
   * Gets the orderBy value
   * 
   * @return the orderBy
   */
  public final Multisort getOrderBy() {
    return orderBy;
  }

  /**
   * Sets the value of orderBy
   * 
   * @param orderBy
   *          the orderBy to set
   */
  public final void setOrderBy(Multisort orderBy) {
    this.orderBy = orderBy;
  }

  /**
   * Get a Column of the specified ColumnALias
   * 
   * @return a column
   */
  public final Column getColumnFromDistinctQuery() {
    return columnFromDistinctQuery;
  }

  /**
   * Sets the value of columnFromDistinctQuery
   * 
   * @param columnFromDistinctQuery
   *          the columnFromDistinctQuery to set
   */
  public final void setColumnFromDisctinctQuery(Column columnFromDistinctQuery) {
    this.columnFromDistinctQuery = columnFromDistinctQuery;
  }

  /**
   * Gets the list of the sql visible columns used in the database request
   * 
   * @return the sqlVisibleColumns
   */
  public List<Column> getSqlVisibleColumns() {
    return sqlVisibleColumns;
  }

  /**
   * Sets the list of the sql visible columns used in the database request
   * 
   * @param sqlVisibleColumns
   *          the sqlVisibleColumn to set
   */
  public void setSqlVisibleColumns(List<Column> sqlVisibleColumns) {
    this.sqlVisibleColumns = sqlVisibleColumns;
  }

  /**
   * Gets the paginationExtend value
   * 
   * @return the paginationExtend
   */
  public final int getPaginationExtend() {
    return paginationExtend;
  }

  /**
   * Sets the value of paginationExtend
   * 
   * @param paginationExtend
   *          the paginationExtend to set
   */
  public final void setPaginationExtend(int paginationExtend) {
    this.paginationExtend = paginationExtend;
  }

  /**
   * Gets the baseRef value
   * 
   * @return the baseRef
   */
  public final String getBaseRef() {
    return baseRef;
  }

  /**
   * Sets the value of baseRef
   * 
   * @param baseRef
   *          the baseRef to set
   */
  public final void setBaseRef(String baseRef) {
    this.baseRef = baseRef;
  }

  /**
   * Sets the value of datasetId
   * 
   * @param datasetId
   *          the datasetId to set
   */
  public final void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  /**
   * Gets the datasetId value
   * 
   * @return the datasetId
   */
  public final String getDatasetId() {
    return datasetId;
  }

  /**
   * Gets the max rows value
   * 
   * @return the max rows
   */
  public final int getMaxrows() {
    return maxrows;
  }

  /**
   * Sets the value of max rows
   * 
   * @param maxrows
   *          the max rows to set
   */
  public final void setMaxrows(int maxrows) {
    this.maxrows = maxrows;
  }

  /**
   * Sets the value of idList
   * 
   * @param idList
   *          the idList to set
   */
  public final void setIdList(String[] idList) {
    if (idList != null) {
      this.idList = idList.clone();
    }
  }

  /**
   * Gets the idList value
   * 
   * @return the idList
   */
  public final String[] getIdList() {
    return idList;
  }

  /**
   * Sets the value of ranges
   * 
   * @param ranges
   *          the ranges to set
   */
  public void setRanges(List<Range> ranges) {
    this.ranges = ranges;
  }

  /**
   * Gets the ranges value
   * 
   * @return the ranges
   */
  public List<Range> getRanges() {
    return ranges;
  }

  /**
   * Gets the list of all the visible columns in the request set to visible or asked (SQL + Virtual) NoClientAccess
   * column are not added
   * 
   * @return the allVisibleColumns
   */
  public List<Column> getAllVisibleColumns() {
    return allVisibleColumns;
  }

  /**
   * Sets the list of all the visible columns in the request (SQL + Virtual)
   * 
   * @param allVisibleColumns
   *          the allVisibleColumns to set
   */
  public void setAllVisibleColumns(List<Column> allVisibleColumns) {
    this.allVisibleColumns = allVisibleColumns;
  }

}
