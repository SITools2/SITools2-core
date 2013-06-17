     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.plugins.filters.core;

import java.util.HashMap;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.geometry.GeometryObject;
import fr.cnes.sitools.dataset.model.geometry.GeometryType;
import fr.cnes.sitools.dataset.model.geometry.Point;

/**
 * Filter for Bbox parameters
 * <p>
 * Note : Create predicates only for postgis datasource
 * </p>
 * 
 * @author m.gond
 */
public class BboxFilter extends AbstractFormFilter {

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** Map panel field type, boundary box */
    MAPPANEL,

  }

  /**
   * BboxFilter constructor
   */
  public BboxFilter() {
    super();
    this.setName("BboxFilter");
    this.setDescription("Required when using MapPanel component");

    this.setClassAuthor("AKKA Technologies");
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataset.filter.business.AbstractFilter# getRequestParamsDescription()
   */
  @Override
  public HashMap<String, ParameterInfo> getRequestParamsDescription() {
    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();
    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "MAPPANEL|columnAlias|value");
    rpd.put("0", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "MAPPANEL|dictionaryName,conceptName|value");
    rpd.put("1", paramInfo);
    this.setRequestParamsDescription(rpd);
    return rpd;
  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    DataSetApplication dsApplication = null;
    DataSet ds = null;
    boolean isConcept = true;
    Form params = request.getResourceRef().getQueryAsForm();
    boolean filterExists = true;
    int i = 0;
    // Build predicat for filters param
    while (filterExists) {
      // first check if the filter is applied on a Concept or not
      String index = TEMPLATE_PARAM_CONCEPT.replace("#", Integer.toString(i));
      String formParam = params.getFirstValue(index);
      if (formParam == null) {
        isConcept = false;
        index = TEMPLATE_PARAM.replace("#", Integer.toString(i));
        formParam = params.getFirstValue(index);
      }
      i++;
      if (formParam != null) {
        String[] parameters = formParam.split("\\|");
        TYPE_COMPONENT[] types = TYPE_COMPONENT.values();
        Boolean trouve = false;
        for (TYPE_COMPONENT typeCmp : types) {
          if (typeCmp.name().equals(parameters[TYPE])) {
            trouve = true;
          }
        }
        if (trouve) {
          if (dsApplication == null) {
            dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
            ds = dsApplication.getDataSet();
          }
          String columnAlias = null;
          if (parameters.length >= VALUES) {
            columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
            if (columnAlias != null) {
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null && col.getFilter() != null && col.getFilter()) {
                String value = parameters[VALUES];
                if (value != null) {
                  String[] boxCoord = value.split(",");
                  if (boxCoord.length == 4) {

                    double minX = new Double(boxCoord[0]);
                    double minY = new Double(boxCoord[1]);
                    double maxX = new Double(boxCoord[2]);
                    double maxY = new Double(boxCoord[3]);

                    // String point1 = minX + " " + minY;
                    // String point2 = maxX + " " + minY;
                    // String point3 = maxX + " " + maxY;
                    // String point4 = minX + " " + maxY;
                    //
                    // String geomStr = "POLYGON((" + point1 + "," + point2 + "," + point3 + "," + point4 + "," + point1
                    // + "))";

                    Point point1 = new Point(minX, minY);
                    Point point2 = new Point(maxX, minY);
                    Point point3 = new Point(maxX, maxY);
                    Point point4 = new Point(minX, maxY);

                    GeometryObject geom = new GeometryObject(GeometryType.POLYGON);
                    geom.getPoints().add(point1);
                    geom.getPoints().add(point2);
                    geom.getPoints().add(point3);
                    geom.getPoints().add(point4);

                    Predicat pred = new Predicat();
                    pred.setLeftAttribute(col);
                    pred.setCompareOperator(Operator.GEO_OVERLAP);
                    // pred.setRightValue("ST_GeomFromText('" + geomStr + "', 4326)");
                    pred.setRightValue(geom);
                    predicats.add(pred);
                  }
                }
              }
            }
          }
        }
      }
      else {
        filterExists = false;
      }
    }
    return predicats;
  }

  @Override
  public Validator<?> getValidator() {
    // TODO Auto-generated method stub
    return null;
  }

}
