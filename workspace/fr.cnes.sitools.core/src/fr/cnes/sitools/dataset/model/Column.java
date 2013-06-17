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
package fr.cnes.sitools.dataset.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.units.dimension.model.SitoolsUnit;

/**
 * Class for column definition of a DataSet
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("column")
public final class Column {
  /**
   * Column ID
   */
  private String id;

  /**
   * dataIndex : - contains the field's Name for a DATABASE Column, - contains the SQL definition for a SQL Column.
   */
  private String dataIndex;

  /**
   * Column Header
   */
  private String header;

  /**
   * Tooltip
   */
  private String toolTip;

  /**
   * Column Width
   */
  private int width;

  /**
   * Can the column be sorted
   */
  private boolean sortable;

  /**
   * Is the column visible ?
   */
  private Boolean visible;

  /**
   * Can the column be filtered ?
   */
  private Boolean filter;

  /**
   * The schema of the table
   */
  private String schema;

  /**
   * table Name of the column
   */
  private String tableName;

  /**
   * Table Alias
   */
  private String tableAlias;

  /**
   * Is it a primary key of the DataSet (must be unique)
   */
  private boolean primaryKey;

  /**
   * Column renderer URL -> external Link Preview -> Pop-up with an image, datasetRequestUrl -> internal link to another
   * DataSet
   */
  private ColumnRenderer columnRenderer;

  /**
   * SQL type
   */
  private String sqlColumnType;

  /**
   * Specific Type : (SQL, VIRTUAL, DATABASE)
   */
  private SpecificColumnType specificColumnType;

  /**
   * Column Alias : Must be unique, this is the value used to map the Client Store
   */
  private String columnAlias;

  /**
   * java.sql.Types column type
   */
  private short javaSqlColumnType;

  /**
   * The Sitools Dimension Id
   */
  private String dimensionId;

  /**
   * The orderBy
   */
  private String orderBy;

  /**
   * The Sitools Unit
   */
  private SitoolsUnit unit;

  /**
   * The format to render dates
   */
  private String format;

  /**
   * Complete constructor
   * 
   * @param id
   *          column ID
   * @param dataIndex
   *          data index of the column
   * @param header
   *          header of the column
   * @param width
   *          width of the column
   * @param sortable
   *          set if the column can be sorted
   * @param visible
   *          set if the column if visible
   * @param sqlColumnType
   *          set the column type
   */
  public Column(String id, String dataIndex, String header, int width, boolean sortable, boolean visible,
    String sqlColumnType) {
    super();
    this.id = id;
    this.dataIndex = dataIndex;
    this.header = header;
    this.width = width;
    this.sortable = sortable;
    this.visible = visible;
    this.sqlColumnType = sqlColumnType;
  }

  /**
   * Default constructor
   */
  public Column() {
    super();
  }

  /**
   * Gets the value of visible
   * 
   * @return the visible
   */
  public Boolean isVisible() {
    return visible;
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
   * Gets the dataIndex value
   * 
   * @return the dataIndex
   */
  public String getDataIndex() {
    return dataIndex;
  }

  /**
   * Sets the value of dataIndex
   * 
   * @param dataIndex
   *          the dataIndex to set
   */
  public void setDataIndex(String dataIndex) {
    this.dataIndex = dataIndex;
  }

  /**
   * Gets the header value
   * 
   * @return the header
   */
  public String getHeader() {
    return header;
  }

  /**
   * Sets the value of header
   * 
   * @param header
   *          the header to set
   */
  public void setHeader(String header) {
    this.header = header;
  }

  /**
   * Gets the toolTip value
   * 
   * @return the toolTip
   */
  public String getToolTip() {
    return toolTip;
  }

  /**
   * Sets the value of toolTip
   * 
   * @param toolTip
   *          the toolTip to set
   */
  public void setToolTip(String toolTip) {
    this.toolTip = toolTip;
  }

  /**
   * Gets the width value
   * 
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets the value of width
   * 
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Tells if it can be sorted
   * 
   * @return true if it can be sorted
   */
  public boolean isSortable() {
    return sortable;
  }

  /**
   * Sets the value to tell if it can be sorted
   * 
   * @param sortable
   *          true if it can be sorted
   */
  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }

  /**
   * Gets the filterType value
   * 
   * @return the filterType
   */
  public Boolean getFilter() {
    return filter;
  }

  /**
   * Sets the value of filterType
   * 
   * @param filterType
   *          the filterType to set
   */
  public void setFilter(Boolean filterType) {
    this.filter = filterType;
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
   * Gets the schema value
   * 
   * @return the schema
   */
  public String getSchema() {
    return schema;
  }

  /**
   * Sets the value of schema
   * 
   * @param schema
   *          the schema to set
   */
  public void setSchema(String schema) {
    this.schema = schema;
  }

  /**
   * Gets the tableName value
   * 
   * @return the tableName
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * Sets the value of tableName
   * 
   * @param tableName
   *          the tableName to set
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   * Gets the tableAlias value
   * 
   * @return the tableAlias
   */
  public String getTableAlias() {
    return tableAlias;
  }

  /**
   * Sets the value of tableAlias
   * 
   * @param tableAlias
   *          the tableAlias to set
   */
  public void setTableAlias(String tableAlias) {
    this.tableAlias = tableAlias;
  }

  /**
   * Returns if the column is a primary key
   * 
   * @return the primaryKey
   */
  public boolean isPrimaryKey() {
    return primaryKey;
  }

  /**
   * Set if the column is a primary key
   * 
   * @param primaryKey
   *          the primaryKey to set
   */
  public void setPrimaryKey(boolean primaryKey) {
    this.primaryKey = primaryKey;
  }

  /**
   * Get the sql column type
   * 
   * @return the sqlColumnType
   */
  public String getSqlColumnType() {
    return sqlColumnType;
  }

  /**
   * Set the column type
   * 
   * @param sqlColumnType
   *          the sqlColumnType to set
   */
  public void setSqlColumnType(String sqlColumnType) {
    this.sqlColumnType = sqlColumnType;
  }

  /**
   * Get the columnRenderer
   * 
   * @return the columnRenderer
   */
  public ColumnRenderer getColumnRenderer() {
    return columnRenderer;
  }

  /**
   * Set the columnRenderer
   * 
   * @param columnRenderer
   *          the columnRenderer to set
   */
  public void setColumnRenderer(ColumnRenderer columnRenderer) {
    this.columnRenderer = columnRenderer;
  }

  /**
   * Get the getSpecificColumnType
   * 
   * @return the specificColumnType
   */
  public SpecificColumnType getSpecificColumnType() {
    return specificColumnType;
  }

  /**
   * Set the setSpecificColumnType
   * 
   * @param specificColumnType
   *          the specificColumnType to set
   */
  public void setSpecificColumnType(SpecificColumnType specificColumnType) {
    this.specificColumnType = specificColumnType;
  }

  /**
   * Get the columnAlias
   * 
   * @return the columnAlias
   */
  public String getColumnAlias() {
    return columnAlias;
  }

  /**
   * Set the columnAlias
   * 
   * @param columnAlias
   *          the columnAlias to set
   */
  public void setColumnAlias(String columnAlias) {
    this.columnAlias = columnAlias;
  }

  /**
   * Sets the value of javaSqlColumnType
   * 
   * @param javaSqlColumnType
   *          the javaSqlColumnType to set
   */
  public void setJavaSqlColumnType(short javaSqlColumnType) {
    this.javaSqlColumnType = javaSqlColumnType;
  }

  /**
   * Gets the javaSqlColumnType value
   * 
   * @return the javaSqlColumnType
   */
  public short getJavaSqlColumnType() {
    return javaSqlColumnType;
  }

  /**
   * Gets the dimensionId value
   * 
   * @return the dimensionId
   */
  public String getDimensionId() {
    return dimensionId;
  }

  /**
   * Sets the value of dimensionId
   * 
   * @param dimensionId
   *          the dimensionId to set
   */
  public void setDimensionId(String dimensionId) {
    this.dimensionId = dimensionId;
  }

  /**
   * Gets the unitName value
   * 
   * @return the unitName
   */
  public SitoolsUnit getUnit() {
    return unit;
  }

  /**
   * Sets the value of unitName
   * 
   * @param unit
   *          the unitName to set
   */
  public void setUnit(SitoolsUnit unit) {
    this.unit = unit;
  }

  /**
   * Gets the orderBy value
   * 
   * @return the orderBy
   */
  public String getOrderBy() {
    return orderBy;
  }

  /**
   * Sets the value of orderBy
   * 
   * @param orderBy
   *          the orderBy to set
   */
  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  /**
   * Gets the format value
   * 
   * @return the format
   */
  public String getFormat() {
    return format;
  }

  /**
   * Sets the value of format
   * 
   * @param format
   *          the format to set
   */
  public void setFormat(String format) {
    this.format = format;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Column other = (Column) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }

    if (columnAlias == null) {
      if (other.columnAlias != null) {
        return false;
      }
    }
    else if (!columnAlias.equals(other.columnAlias)) {
      return false;
    }
    return true;
  }

}
