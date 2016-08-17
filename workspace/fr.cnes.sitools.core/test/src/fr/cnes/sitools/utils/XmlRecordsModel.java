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
package fr.cnes.sitools.utils;

import java.util.List;

import fr.cnes.sitools.datasource.jdbc.model.Record;

public class XmlRecordsModel {

  private Integer total;

  private Integer count;

  private Integer offset;

  private List<Record> records;

  /**
   * Gets the total value
   * 
   * @return the total
   */
  public Integer getTotal() {
    return total;
  }

  /**
   * Sets the value of total
   * 
   * @param total
   *          the total to set
   */
  public void setTotal(Integer total) {
    this.total = total;
  }

  /**
   * Gets the count value
   * 
   * @return the count
   */
  public Integer getCount() {
    return count;
  }

  /**
   * Sets the value of count
   * 
   * @param count
   *          the count to set
   */
  public void setCount(Integer count) {
    this.count = count;
  }

  /**
   * Gets the offset value
   * 
   * @return the offset
   */
  public Integer getOffset() {
    return offset;
  }

  /**
   * Sets the value of offset
   * 
   * @param offset
   *          the offset to set
   */
  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  /**
   * Gets the records value
   * 
   * @return the records
   */
  public List<Record> getRecords() {
    return records;
  }

  /**
   * Sets the value of records
   * 
   * @param records
   *          the records to set
   */
  public void setRecords(List<Record> records) {
    this.records = records;
  }

}
