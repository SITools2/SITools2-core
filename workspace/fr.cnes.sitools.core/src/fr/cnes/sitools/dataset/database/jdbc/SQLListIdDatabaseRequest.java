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
package fr.cnes.sitools.dataset.database.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.dbexplorer.DBResultSet;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Create a request SQL with prepared statement
 * 
 * @author m.gond (AKKA Technologies)
 */
public class SQLListIdDatabaseRequest extends SQLDatabaseRequest {

  /** Error starting message */
  private static final String ERROR = "ERROR ";

  /** logger de la resource Context / Application ? ok */
  private Logger logger = Context.getCurrentLogger();

  /** list of ids */
  private String[] idList;

  /** the prepared statement */
  private PreparedStatement prepStm;

  /** the connection used */
  private Connection con;

  /** The nbResultsToSend */
  private int nbResultsToSend = 0;

  /** The primaryKey */
  private Column primaryKey;

  /** The Datasource **/
  private SitoolsSQLDataSource datasource;

  /**
   * Create a {@code AbstractDatabaseRequest}
   * 
   * @param params
   *          the request parameters
   * 
   * @param idList
   *          the list of ids
   */
  public SQLListIdDatabaseRequest(DatabaseRequestParameters params, String[] idList) {
    super(params);

    nbResultsToSend = idList.length;
    if (idList != null) {
      this.idList = idList.clone();
    }

    // change the request predicat to have only the dataset's and the id
    // selector

    DataSet ds = params.getDataset();

    this.setPredicats(params, ds.getPredicat());

    // look for the primary key column
    primaryKey = null;
    List<Column> columns = params.getDataset().getColumnModel();
    for (Column column : columns) {
      if (column.isPrimaryKey()) {
        primaryKey = column;
        break;
      }
    }
    // add the predicat to select on the primary key.
    Predicat pred = new Predicat();
    pred.setLogicOperator("AND");
    pred.setLeftAttribute(primaryKey);
    pred.setCompareOperator(Operator.EQ);
    pred.setRightValue("?");

    params.getPredicats().add(pred);

    // set count done to false not the get the total count
    // the request isn't ready to be executed yet
    // and the total is always one
    params.setCountDone(false);
    params.setMaxrows(1);

    datasource = (SitoolsSQLDataSource) params.getDb();

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataset.jdbc.DatabaseRequest#createDistinctRequest()
   */
  @Override
  public final void createDistinctRequest() throws SitoolsException {
    try {
      String sql = this.getDistinctRequestAsString();
      this.logger.log(Level.INFO, "SQL = " + sql);

      con = datasource.getConnection();

      prepStm = con.prepareStatement(sql);

    }
    catch (NumberFormatException e) {
      throw new SitoolsException(ERROR + e.getMessage(), e);
    }
    catch (SQLException e) {
      throw new SitoolsException(ERROR + e.getMessage(), e);
    }
  }

  @Override
  public final void createRequest() throws SitoolsException {
    try {
      String sql = this.getRequestAsString();
      this.logger.log(Level.INFO, "SQL = " + sql);

      con = datasource.getConnection();

      prepStm = con.prepareStatement(sql);

    }
    catch (NumberFormatException e) {
      throw new SitoolsException(ERROR + e.getMessage(), e);
    }
    catch (SQLException e) {
      throw new SitoolsException(ERROR + e.getMessage(), e);
    }
  }

  @Override
  public final int getTotalCount() {
    return this.idList.length;
  }

  @Override
  public final int getCount() {
    return this.idList.length;
  }

  @Override
  public final boolean nextResult() throws SitoolsException {
    try {
      if (idList.length > nbSendedResults) {
        // javaSQLType by default is VARCHAR,
        short javaSqlType = (primaryKey.getJavaSqlColumnType() == java.sql.Types.NULL) ? java.sql.Types.VARCHAR
            : primaryKey.getJavaSqlColumnType();

        prepStm.setObject(1, idList[nbSendedResults], javaSqlType);

        rs = new DBResultSet(prepStm.executeQuery(), prepStm, con);
        rs.next();
      }

      return nbSendedResults++ < nbResultsToSend;
    }
    catch (SQLException e) {
      this.logger.log(Level.SEVERE, e.getMessage());
      throw new SitoolsException("ERROR_FETCHING_RESULTSET", e);
    }
  }

  @Override
  @Deprecated
  public final boolean isLastResult() throws SitoolsException {
    return nbSendedResults >= nbResultsToSend;
  }

  @Override
  public final Record getRecord() throws SitoolsException {
    try {
      Record record = new Record(buildURI());
      setAttributeValues(record, rs);
      return record;
    }
    catch (SQLException e) {
      throw new SitoolsException("ERROR_BUILDING_RECORD", e);
    }
  }

  @Override
  public final void close() throws SitoolsException {
    try {
      if (rs != null) {
        rs.close();
      }
      else if (con != null) {
        con.close();
      }
    }
    catch (SQLException e) {
      throw new SitoolsException("ERROR_CLOSING_CONNECTION", e);
    }
  }

  /**
   * Set the given predicat list to the given DatabaseRequestParameters
   * 
   * @param params
   *          the DatabaseRequestParameters
   * @param predicat
   *          the predicat list
   */
  private void setPredicats(DatabaseRequestParameters params, List<Predicat> predicat) {
    params.getPredicats().clear();
    for (Iterator<Predicat> iterator = predicat.iterator(); iterator.hasNext();) {
      Predicat predicat2 = iterator.next();

      params.getPredicats().add(predicat2);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataset.jdbc.SQLDatabaseRequest#calculateTotalCountFromBase()
   */
  @Override
  public int calculateTotalCountFromBase() throws SitoolsException {
    return this.idList.length;
  }

}
