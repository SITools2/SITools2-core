/*******************************************************************************
 * Copyright 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.security.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.util.Util;

/**
 * Optimized implementation of StringContainer
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class IPBlackListTreeSet implements StringContainer {

  /** in memory list of banished IP */
  private Set<String> treeSet = Collections.synchronizedSet(new TreeSet<String>());

  /** settings */
  private SitoolsSettings settings = null;

  /** context */
  private Context context = null;

  /**
   * Constructor
   * 
   * @param context
   *          Context
   */
  public IPBlackListTreeSet(Context context) {
    this.context = context;
    settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    if (settings == null) {
      return;
    }

    String banishedIP = settings.getString("Security.filter.blacklist");
    if (Util.isNotEmpty(banishedIP)) {
      String[] ip = banishedIP.split("\\|");
      treeSet.addAll(Arrays.asList(ip));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.filter.StringContainer#contains(java.lang.String)
   */
  @Override
  public boolean contains(String ip) {
    return (treeSet != null) ? treeSet.contains(ip) : false;
  }

  /**
   * Black list a new ip address
   * 
   * @param ip
   *          String IP address (X.X.X.X with X between 0 and 255)
   */
  public void backList(String ip) {
    if (treeSet != null) {
      treeSet.add(ip);
    }
  }

  /**
   * Getter of Restlet context
   * @return Context
   */
  public Context getContext() {
    return context;
  }

  
  
}
