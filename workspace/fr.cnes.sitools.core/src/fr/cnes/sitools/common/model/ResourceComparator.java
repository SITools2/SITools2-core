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
package fr.cnes.sitools.common.model;

import java.util.Comparator;

/**
 * Generic class for sorting SITools objects based on ResourceCollectionFilter
 * definition.
 * 
 * @param <T>
 *          class of SITools resources to be sorted
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ResourceComparator<T> implements Comparator<T> {

  /** Decreasing comparison*/
  public static final String DESC = "DESC";
  
  /** Ascending comparison*/
  public static final String ASC = "ASC";

  /** ID of the field*/
  public static final String FIELD_ID = "id";
  
  /** Name of the field*/
  public static final String FIELD_NAME = "name";
  
  /** Description of the field*/
  public static final String FIELD_DESCRIPTION = "description";

  /** Resource filter parameters */
  private ResourceCollectionFilter filter = null;

  /**
   * Constructor with ResourceFilter
   * 
   * @param filter
   *          ResourceFilter
   */
  public ResourceComparator(ResourceCollectionFilter filter) {
    this.filter = filter;
  }

  /**
   * Gets the filter value
   * 
   * @return the filter
   */
  public final ResourceCollectionFilter getFilter() {
    return filter;
  }

  /**
   * Sets the value of filter
   * 
   * @param filter
   *          the filter to set
   */
  public final void setFilter(ResourceCollectionFilter filter) {
    this.filter = filter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(T o1, T o2) {
    return compare((IResource) o1, (IResource) o2);
  }

  /**
   * Compare two resources
   * @param o1 resource 1
   * @param o2 resource 2
   * @return 1 of r1 > r2, 0 if r1 = r2, -1 if r1 < r2
   */
  public final int compare(IResource o1, IResource o2) {
    if (o1.getName() == null) {
      return 1;
    }
    if (o2.getName() == null) {
      return -1;
    }
    String s1 = (String) o1.getName();
    String s2 = (String) o2.getName();

    if ((getFilter() != null)
        && (getFilter().getSort() != null)
        && (getFilter().getOrder() != null)
        && (getFilter().getOrder().equals(DESC) && (getFilter().getSort() != null) && (getFilter().getSort()
            .equals(FIELD_NAME)))) {

      return s2.toLowerCase().compareTo(s1.toLowerCase());
    }
    else {
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
  }

  /**
   * Compare two String objects according to filter parameters
   * 
   * @param o1
   *          String
   * @param o2
   *          String
   * @return int
   */
  public final int compare(String o1, String o2) {
    if (o1 == null) {
      return 1;
    }
    if (o2 == null) {
      return -1;
    }

    if ((getFilter() != null) && (getFilter().getOrder() != null) && (getFilter().getOrder().equals(DESC))) {
      return o1.toLowerCase().compareTo(o2.toLowerCase());
    }
    else {
      return o2.toLowerCase().compareTo(o1.toLowerCase());
    }
  }

}
