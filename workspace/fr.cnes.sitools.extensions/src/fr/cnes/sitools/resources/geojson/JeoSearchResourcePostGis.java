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
package fr.cnes.sitools.resources.geojson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.representation.Representation;

import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.resources.geojson.representations.GeoJsonRepresentation;

/**
 * Classic implementation of JeoSearch resource
 * 
 * 
 * @author m.gond
 */
public class JeoSearchResourcePostGis extends AbstractJeoSearchResource {

  @Override
  public Representation getRepresentation(DatabaseRequestParameters params, String geometryColName,
      ConverterChained converterChained, DataSet dataset, Context context) {

    // modify the sql visible columns to return some geoJSON
    List<Column> col = new ArrayList<Column>(params.getSqlVisibleColumns());
    List<Column> columnModel = params.getDataset().getColumnModel();
    for (Iterator<Column> iterator = columnModel.iterator(); iterator.hasNext();) {
      Column column = iterator.next();
      if (column.getColumnAlias().equals(geometryColName)) {
        Column geoCol = new Column();
        // remove the coordinates column from the visible columns,
        // to have only the geoJson column
        boolean found = false;
        for (Iterator<Column> itVisible = col.iterator(); itVisible.hasNext() && !found;) {
          Column columnVisible = itVisible.next();
          if (columnVisible.getColumnAlias().equals(geometryColName)) {
            itVisible.remove();
            found = true;
          }
        }
        // then add the column with the GeoJSON export
        geoCol.setColumnAlias(column.getColumnAlias());
        // Ne doit pas servir normalement
        // geoCol.setColumnAliasDetail(column.getColumnAliasDetail());
        geoCol.setDataIndex("St_asgeojson(" + column.getTableName() + "." + column.getDataIndex() + ")");
        geoCol.setSpecificColumnType(SpecificColumnType.SQL);
        geoCol.setJavaSqlColumnType(column.getJavaSqlColumnType());
        geoCol.setVisible(true);
        col.add(geoCol);
      }
    }
    params.setSqlVisibleColumns(col);

    return new GeoJsonRepresentation(params, geometryColName, converterChained, dataset, getContext());
  }

}
