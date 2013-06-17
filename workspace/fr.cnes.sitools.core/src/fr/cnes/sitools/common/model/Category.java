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

/**
 * Enumeration for Application category designed for separating usage between 
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public enum Category {

    /** System applications are mandatory for Sitools2 start */
    SYSTEM,
    /** Administration applications allows to modify state of Sitools2 after starts */
    ADMIN,
    /** User applications allows to use all functions available in Sitools2  */
    USER,
    /** Dynamic system  applications (e.g. DBExplorer) */
    SYSTEM_DYNAMIC,
    /** Dynamic admin-side  applications (e.g. Datasources) */
    ADMIN_DYNAMIC,
    /** Dynamic user-side  applications (e.g. datasets) */
    USER_DYNAMIC,
    /** Public applications with low security (no bad credentials check) */
    PUBLIC
}

