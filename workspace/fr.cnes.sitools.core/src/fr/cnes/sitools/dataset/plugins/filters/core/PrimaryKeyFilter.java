     /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Reference;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.util.SQLUtils;

/**
 * Dataset filter on primary key, used to get the details of a specific record identified by its primary key
 * 
 * 
 * @author m.gond
 */
public class PrimaryKeyFilter extends AbstractFilter {

  /**
   * Default constructor
   */
  public PrimaryKeyFilter() {

    super();
    this.setName("PrimaryKeyFilter");
    this.setDescription("Required when getting the description of a single record");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

  }

  @Override
  public Validator<?> getValidator() {
    return new Validator<AbstractFilter>() {
      @Override
      public Set<ConstraintViolation> validate(AbstractFilter item) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {

    // target : database, table, record
    Map<String, Object> attributes = request.getAttributes();
    String recordName = (attributes.get("record") != null) ? Reference.decode((String) attributes.get("record"),
        CharacterSet.UTF_8) : null;

    DataSetApplication application = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");

    // add some predicates for the record
    if (recordName != null && !"".equals(recordName)) {
      String[] keys = recordName.split(";");
      List<Column> columns = application.getDataSet().getColumnModel();
      int i = 0;
      for (Column column : columns) {
        if (column.isPrimaryKey()) {
          Predicat predicat = new Predicat();
          predicat.setLeftAttribute(column);
          predicat.setRightValue("'" + SQLUtils.escapeString(keys[i]) + "'");
          predicat.setCompareOperator(Operator.EQ);
          predicats.add(predicat);
          i++;
        }
      }
    }
    return predicats;
  }

}
