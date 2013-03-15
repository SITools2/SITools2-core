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
package fr.cnes.sitools.dataset.database.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.datasource.mongodb.business.MongoDBRequestModel;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSource;

/**
 * Create a request NOSQL
 * 
 * @author m.gond (Akka Technologies
 * */
public class MongoDBDistinctDatabaseRequest extends MongoDBDatabaseRequest {
  /** The result if the query is distinct */
  protected List<Object> listDistinct = null;

  /** The iterator on the results */
  private Iterator<Object> rs = null;

  /** The datasource */
  private SitoolsMongoDBDataSource datasource = null;

  /** Resource logger Context / Application ? ok */
  private Logger logger = Context.getCurrentLogger();

  /** The column of the distinct */
  private Column distinctColumn = null;

  /** Number of total results */
  private int nbTotalResults = 0;

  /**
   * Create a {@code SQLDatabaseRequest}
   * 
   * @param params
   *          the request parameters
   */
  public MongoDBDistinctDatabaseRequest(DatabaseRequestParameters params) {
    super(params);
    datasource = (SitoolsMongoDBDataSource) params.getDb();
  }

  /**
   * Get the column used for the distinct query. If no or more than one column found, return null
   * 
   * @return the column used for the distinct query, null if no or more than one column found
   * 
   */
  private Column getDistinctColumn() {
    ArrayList<Column> columnsQuery = new ArrayList<Column>();
    columnsQuery.add(params.getColumnFromDistinctQuery());
    if (columnsQuery.size() == 1) {
      return (columnsQuery.get(0));
    }
    else {
      return null;
    }
  }

  @Override
  public void createDistinctRequest() throws SitoolsException {

    try {
      distinctColumn = getDistinctColumn();
      if (distinctColumn == null) {
        return;
      }

      String collectionName = params.getStructures().get(0).getName();

      MongoDBRequestModel mongoRequest = new MongoDBRequestModel();
      mongoRequest.setCollectionName(collectionName);

      String jsonFilter = getRequestAsString();
      mongoRequest.setFilterString(jsonFilter);

      if (distinctColumn != null) {
        this.logger.log(Level.INFO, "DISTINCT JSON = " + jsonFilter + "\n ON KEY : " + distinctColumn.getDataIndex());

        listDistinct = datasource.distinctQuery(distinctColumn.getDataIndex(), mongoRequest);
        if (listDistinct == null) {
          throw new SitoolsException("Error while querying datasource");
        }
        rs = listDistinct.iterator();
        nbTotalResults = listDistinct.size();
      }
      else {
        throw new SitoolsException("Unsuported distinct request : only one column allowed");
      }

    }
    catch (NumberFormatException e) {
      throw new SitoolsException("ERROR " + e.getMessage(), e);
    }

  }

  @Override
  public int getTotalCount() {
    return nbTotalResults;
  }

  @Override
  public int getCount() {
    return nbResultsToSend;
  }

  @Override
  public boolean nextResult() throws SitoolsException {

    if (listDistinct == null) {
      return false;
    }
    if (rs.hasNext()) {
      return true;
    }
    return false;

  }

  @Override
  public Record getRecord() throws SitoolsException {
    Record record = new Record(buildURI());
    List<AttributeValue> value = new ArrayList<AttributeValue>();

    Object distinctValue = getFormatedValue(rs.next());

    value.add(new AttributeValue(distinctColumn.getColumnAlias(), distinctValue));
    record.setAttributeValues(value);
    return record;

  }

  /**
   * Close the current request
   * 
   * @throws SitoolsException
   *           if there is an error while closing the JDBC request
   */
  @Override
  public void close() throws SitoolsException {
  }

}
