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
package org.restlet.ext.solr.internal;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.common.util.ContentStream;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequestBase;
import org.restlet.Request;

/**
 * Solr query request wrapping a Restlet request.
 * 
 * @author Remi Dewitte <remi@gide.net>
 */
public class SolrRestletQueryRequest extends SolrQueryRequestBase {

  /**
   * Constructor.
   * 
   * @param request
   *          The Restlet request to wrap.
   * @param core
   *          The Solr core.
   */
  public SolrRestletQueryRequest(Request request, SolrCore core) {
    super(core, new SolrRestletParams(request));
    getContext().put("path", request.getResourceRef().getPath());
    ArrayList<ContentStream> _streams = new ArrayList<ContentStream>(1);
    _streams.add(new SolrRepresentationContentStream(request.getEntity()));
    setContentStreams(_streams);
  }

}