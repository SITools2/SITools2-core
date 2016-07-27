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
package fr.cnes.sitools.dataset.model;

/**
 * Specific type sfor columns
 * 
 * @author AKKA
 * 
 */
public enum BehaviorEnum {
  /**
   * The column is an url locally accessible
   */
  localUrl,
  /**
   * The column is an url externally accessible, supposed to be opened in a new tab
   */
  extUrlNewTab,
  /**
   * The column is an url externally accessible, supposed to be opened in the desktop
   */
  extUrlDesktop,
  /**
   * The column is an image to be displayed in the desktop
   */
  ImgNoThumb,
  /**
   * The column is an image to be displayed directly in the dataview
   */
  ImgAutoThumb,
  /**
   * The column is an image to be displayed directly in the dataview, but displayed in the desktop when clicking on it
   */
  ImgThumbSQL,
  /**
   * Column is a datasetLink
   */
  datasetLink,
  /**
   * Column is a datasetIconLink
   */
  datasetIconLink,
  /**
   * Column can not be displayed to the client
   */
  noClientAccess,
  /**
   * Column is a datasetIconLinkColumn
   */
  datasetLinkColumn,
  /**
   * Column is a datasetIconLinkColumnIcon
   */
  datasetLinkColumnIcon

}
