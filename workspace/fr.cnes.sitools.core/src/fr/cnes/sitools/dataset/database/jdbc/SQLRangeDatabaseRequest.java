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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.Range;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.dbexplorer.DBResultSet;

/**
 * Execute a request for some records specified by a List of ranges
 * 
 * 
 * @author m.gond
 */
public class SQLRangeDatabaseRequest extends SQLDatabaseRequest {
  /** Error starting message */
  private static final String ERROR = "ERROR ";
  /** logger de la resource Context / Application ? ok */
  private Logger logger = Context.getCurrentLogger();

  /** the connection used */
  private Connection con;

  /** The list of ranges to query */
  private List<Range> ranges;

  /** The number of the current range in the ranges List */
  private int currentRangeIndex = 0;

  /** the prepared statement */
  private PreparedStatement prepStm;

  /** The nbTotalResults */
  private int nbTotalResults = 0;

  /** The Datasource **/
  private SitoolsSQLDataSource datasource;

  /**
   * Create a new SQLRangeDatabaseRequest with the given {@link DatabaseRequestParameters}
   * 
   * @param params
   *          the {@link DatabaseRequestParameters}
   */
  public SQLRangeDatabaseRequest(DatabaseRequestParameters params) {
    super(params);
    ranges = params.getRanges();
    datasource = (SitoolsSQLDataSource) params.getDb();
  }

  @Override
  public final void createRequest() throws SitoolsException {
    try {
      nbTotalResults = calculateTotalCount();
      // set is count done to false not to calculate the total number of records for the request
      params.setCountDone(false);
      String sql = this.getRequestAsString();

      sql += " LIMIT " + "?" + " OFFSET " + "?";

      con = datasource.getConnection();

      prepStm = con.prepareStatement(sql);

      executeRequest(ranges.get(currentRangeIndex));
    }
    catch (NumberFormatException e) {
      throw new SitoolsException(ERROR + e.getMessage(), e);
    }
    catch (SQLException e) {
      throw new SitoolsException(ERROR + e.getMessage(), e);
    }
  }

  @Override
  public void createDistinctRequest() throws SitoolsException {
    this.createRequest();
  }

  @Override
  public final boolean nextResult() throws SitoolsException {
    try {

      if (rs.next()) {
        return true;
      }
      else {
        currentRangeIndex++;
        if (currentRangeIndex < ranges.size()) {
          executeRequest(ranges.get(currentRangeIndex));
          rs.next();
          return true;
        }
        else {
          return false;
        }
      }
    }
    catch (SQLException e) {
      this.logger.log(Level.SEVERE, e.getMessage());
      throw new SitoolsException("ERROR_FETCHING_RESULTSET", e);
    }
  }

  /**
   * Execute the SQL request for a particular range
   * 
   * @param range
   *          the range
   * @throws SQLException
   *           if there is an error while executing the request
   */
  private void executeRequest(Range range) throws SQLException {
    int limit = range.getEnd() - range.getStart() + 1;
    int offset = range.getStart();
    prepStm.setInt(1, limit);
    prepStm.setInt(2, offset);

    logger.log(Level.INFO, "SQL WITH LIMIT = " + prepStm);
    rs = new DBResultSet(prepStm.executeQuery(), prepStm, con);
  }

  @Override
  public int getTotalCount() {
    return nbTotalResults;
  }

  @Override
  public int getCount() {
    return nbTotalResults;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataset.jdbc.SQLDatabaseRequest#calculateTotalCountFromBase()
   */
  @Override
  public int calculateTotalCountFromBase() throws SitoolsException {
    return calculateTotalCount();
  }

  /**
   * Calculate the number of records for the list of ranges
   * 
   * @return the total number of records to query
   */
  private int calculateTotalCount() {
    int totalCount = 0;
    for (Range range : ranges) {
      totalCount += range.getEnd() - range.getStart() + 1;
    }
    return totalCount;
  }

}
