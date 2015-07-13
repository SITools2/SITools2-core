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
package fr.cnes.sitools.dataset.opensearch.runnables;

import org.restlet.Context;

import fr.cnes.sitools.dataset.opensearch.OpenSearchApplication;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreInterface;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;

/**
 * Abstract class for OpensearchRunnable object
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
public abstract class OpensearchRunnable implements Runnable {

  /** Opensearch object */
  protected Opensearch os;
  /** opensearch store */
  protected OpenSearchStoreInterface store;
  /** SolrUrl */
  protected String solrUrl;
  /** Context */
  protected Context context;
  /** OpensearchApplication */
  protected OpenSearchApplication application;
}
