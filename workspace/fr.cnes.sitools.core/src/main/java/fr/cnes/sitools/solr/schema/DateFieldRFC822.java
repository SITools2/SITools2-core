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
package fr.cnes.sitools.solr.schema;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.TrieDateField;
import org.restlet.engine.util.DateUtils;

/**
 * DateField for RFC822 date format
 * 
 * @author m.gond
 */
public class DateFieldRFC822 extends TrieDateField {

  /** Valid RFC_822 format */
  private static final String RFC_882_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

  /**
   * Thread safe DateFormat that can <b>format</b> in the canonical RFC_882 date format
   */
  private static final ThreadLocalDateFormat FMT_THREAD_LOCAL = new ThreadLocalDateFormat(new SimpleDateFormat(
      RFC_882_FORMAT, Locale.US));

  /**
   * ThreadLocalDateFormat
   * 
   * @author m.gond
   */
  private static class ThreadLocalDateFormat extends ThreadLocal<DateFormat> {
    /**
     * DateFormat
     */
    private DateFormat proto;

    /**
     * Default constructor
     * 
     * @param d
     *          DateFormat
     */
    public ThreadLocalDateFormat(DateFormat d) {
      super();
      proto = d;
    }

    @Override
    protected DateFormat initialValue() {
      return (DateFormat) proto.clone();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.solr.schema.DateField#toExternal(java.util.Date)
   */
  @Override
  public String toExternal(Date d) {
    // TODO Auto-generated method stub
    return FMT_THREAD_LOCAL.get().format(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.solr.schema.DateField#toExternal(org.apache.lucene.document.Fieldable)
   */
  @Override
  public String toExternal(IndexableField f) {
    Date date = DateUtils.parse(f.stringValue() + "Z", DateUtils.FORMAT_RFC_3339);

    if (date == null) {
      System.out.println(f.stringValue() + " not externalised");
      return "";
    }
    return FMT_THREAD_LOCAL.get().format(date);
  }

}
