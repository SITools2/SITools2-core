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
package fr.cnes.sitools.common.model;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Base class for filter parameters.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class ResourceCollectionFilter {

  /**
   * The embedded request.
   */
  private Request request = null;

  /**
   * query parameter.
   */
  private String query = null;

  /**
   * Pagination - number max of items required.
   */
  private int limit = 0;

  /**
   * Pagination - index of first item.
   */
  private int start = 0;

  /**
   * Sort - Field name.
   */
  private String sort = null;

  /**
   * Sort - Order ASC / DESC.
   */
  private String order = "ASC";

  /**
   * Parent.
   */
  private String parent = null;

  /**
   * FilterMode
   */
  private int filterMode;

  /**
   * totalCount OUT
   */
  private Integer totalCount = null;

  /**
   * searching mode (strict, startwith (default))
   */
  private String mode = "startwith";

  /**
   * Constructor with Request.
   * 
   * @param req
   *          request for the filter
   */
  public ResourceCollectionFilter(Request req) {
    super();
    this.setRequest(req);
    Form form = req.getResourceRef().getQueryAsForm();

    String pstart = form.getFirstValue("start");
    this.start = (pstart != null) ? Integer.parseInt(Reference.decode(pstart, CharacterSet.UTF_8)) : 0;

    String plimit = form.getFirstValue("limit");
    this.limit = (plimit != null) ? Integer.parseInt(Reference.decode(plimit, CharacterSet.UTF_8)) : 0;

    String pquery = form.getFirstValue("query");
    this.query = (pquery != null) ? Reference.decode(pquery, CharacterSet.UTF_8) : null;

    String psort = form.getFirstValue("sort");
    if (psort != null && psort.startsWith("[")) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        JsonNode rootNode = mapper.readValue(psort, JsonNode.class);
        JsonNode sortNode = rootNode.get(0);
        psort = sortNode.get("property").getTextValue();
        this.sort = (psort != null) ? Reference.decode(psort, CharacterSet.UTF_8) : null;
        String porder = sortNode.get("direction").getTextValue();
        this.order = (porder != null) ? Reference.decode(porder, CharacterSet.UTF_8) : null;
      }
      catch (Exception e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage(), e);
      }
    }
    else {
      this.sort = (psort != null) ? Reference.decode(psort, CharacterSet.UTF_8) : null;

      String porder = form.getFirstValue("dir");
      this.order = (porder != null) ? Reference.decode(porder, CharacterSet.UTF_8) : null;
    }

    String pmode = form.getFirstValue("mode");
    this.setMode((pmode != null) ? Reference.decode(pmode, CharacterSet.UTF_8) : null);

  }

  /**
   * Constructor with pagination and query string parameters.
   * 
   * @param startIndex
   *          index of first element
   * @param limitNum
   *          number of elements
   * @param queryStr
   *          a string term
   */
  public ResourceCollectionFilter(int startIndex, int limitNum, String queryStr) {
    this.limit = limitNum;
    this.start = startIndex;
    this.query = queryStr;
  }

  /**
   * Gets the filterMode value
   * 
   * @return the filterMode
   */
  public int getFilterMode() {
    return filterMode;
  }

  /**
   * Sets the value of filterMode
   * 
   * @param filterMode
   *          the filterMode to set
   */
  public void setFilterMode(int filterMode) {
    this.filterMode = filterMode;
  }

  /**
   * TODO design a jdbc.ResourceFilterWrapper wrapper to sql syntax.
   * 
   * @param req
   *          SQL request
   * @param fieldName
   *          the name of the field concerned with the "like" request
   * @return request with filter clauses
   */
  public String toSQL(String req, String fieldName) {

    if ((query != null) && !query.equals("")) {

      req += req.contains(" WHERE ") ? " AND " : " WHERE ";
      req += fieldName + " LIKE '" + query + "%'";
    }

    if (limit > 0) {
      req += " LIMIT " + limit;
    }
    if (start > 0) {
      req += " OFFSET " + start;
    }

    return req;
  }

  /**
   * To SQL count
   * 
   * @param req
   *          request
   * @param fieldName
   *          field name
   * @return request
   */
  public String toSqlCount(String req, String fieldName) {
    if ((query != null) && !query.equals("")) {

      req += req.contains(" WHERE ") ? " AND " : " WHERE ";
      req += fieldName + " LIKE '" + query + "%'";
    }

    int indexFROM = req.indexOf(" FROM ");
    String result = "SELECT COUNT(*) FROM " + req.substring(indexFROM + 6);

    return result;
  }

  /**
   * Gets the query value.
   * 
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * Sets the value of query.
   * 
   * @param que
   *          the query to set
   */
  public void setQuery(String que) {
    this.query = que;
  }

  /**
   * Gets the limit value.
   * 
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Sets the value of limit.
   * 
   * @param lim
   *          the limit to set
   */
  public void setLimit(int lim) {
    this.limit = lim;
  }

  /**
   * Gets the start value.
   * 
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * Sets the value of start.
   * 
   * @param startVal
   *          the start to set
   */
  public void setStart(int startVal) {
    this.start = startVal;
  }

  /**
   * Gets the parent value.
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

  /**
   * Sets the value of parent.
   * 
   * @param par
   *          the parent to set
   */
  public void setParent(String par) {
    this.parent = par;
  }

  /**
   * Gets the sort value.
   * 
   * @return the sort
   */
  public String getSort() {
    return sort;
  }

  /**
   * Sets the value of sort.
   * 
   * @param srt
   *          the sort to set
   */
  public void setSort(String srt) {
    this.sort = srt;
  }

  /**
   * Gets the order value.
   * 
   * @return the order
   */
  public String getOrder() {
    return order;
  }

  /**
   * Sets the value of order.
   * 
   * @param ord
   *          the order to set
   */
  public void setOrder(String ord) {
    this.order = ord;
  }

  /**
   * Gets the totalCount value
   * 
   * @return the totalCount
   */
  public Integer getTotalCount() {
    return totalCount;
  }

  /**
   * Sets the value of totalCount
   * 
   * @param totalCount
   *          the totalCount to set
   */
  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  /**
   * Sets the value of request
   * 
   * @param request
   *          the request to set
   */
  public void setRequest(Request request) {
    this.request = request;
  }

  /**
   * Gets the request value
   * 
   * @return the request
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Sets the value of mode
   * 
   * @param mode
   *          the mode to set
   */
  public void setMode(String mode) {
    this.mode = mode;
  }

  /**
   * Gets the mode value
   * 
   * @return the mode
   */
  public String getMode() {
    return mode;
  }

}
