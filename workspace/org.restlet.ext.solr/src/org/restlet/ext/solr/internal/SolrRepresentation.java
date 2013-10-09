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
package org.restlet.ext.solr.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.BinaryResponseWriter;
import org.apache.solr.response.JSONResponseWriter;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.response.XMLResponseWriter;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

/**
 * Representation wrapping a Solr query and exposing its response either as XML or JSON.
 * 
 * @author Remi Dewitte <remi@gide.net>
 */
public class SolrRepresentation extends WriterRepresentation {

  /** The wrapped Solr query request. */
  protected SolrQueryRequest solrQueryRequest;

  /** The wrapped Solr query response. */
  protected SolrQueryResponse solrQueryResponse;

  protected SolrCore core = null;

  /**
   * Constructor. Note that the character set is UTF-8 by default.
   * 
   * @param mediaType
   *          The media type.
   * @param solrQueryRequest
   *          The wrapped Solr query request.
   * @param solrQueryResponse
   *          The wrapped Solr query response.
   */
  public SolrRepresentation(MediaType mediaType, SolrQueryRequest solrQueryRequest, SolrQueryResponse solrQueryResponse) {
    super(mediaType);
    setCharacterSet(CharacterSet.UTF_8);
    this.solrQueryRequest = solrQueryRequest;
    this.solrQueryResponse = solrQueryResponse;
  }

  /**
   * Constructor.
   * 
   * @param solrQueryRequest
   *          The wrapped Solr query request.
   * @param solrQueryResponse
   *          The wrapped Solr query response.
   * @see #SolrRepresentation(MediaType, SolrQueryRequest, SolrQueryResponse)
   */
  public SolrRepresentation(SolrQueryRequest solrQueryRequest, SolrQueryResponse solrQueryResponse) {
    this(null, solrQueryRequest, solrQueryResponse);
  }

  /**
   * Constructor.
   * 
   * @param solrQueryRequest
   *          The wrapped Solr query request.
   * @param solrQueryResponse
   *          The wrapped Solr query response.
   * @see #SolrRepresentation(MediaType, SolrQueryRequest, SolrQueryResponse)
   */
  public SolrRepresentation(SolrQueryRequest solrQueryRequest, SolrQueryResponse solrQueryResponse, SolrCore core) {
    this(null, solrQueryRequest, solrQueryResponse);
    this.core = core;
  }

  @Override
  public void write(Writer writer) throws IOException {
    QueryResponseWriter qrWriter;
    if (core == null) {

      if (MediaType.APPLICATION_JSON.isCompatible(getMediaType())
          || MediaType.APPLICATION_JAVASCRIPT.isCompatible(getMediaType())) {
        qrWriter = new JSONResponseWriter();
      }
      else {
        qrWriter = new XMLResponseWriter();
      }
    }
    else {
      // core.getQueryResponseWriter(solrQueryRequest)
      qrWriter = core.getQueryResponseWriter(solrQueryRequest);

    }

    qrWriter.write(writer, solrQueryRequest, solrQueryResponse);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.representation.WriterRepresentation#write(java.io.OutputStream)
   */
  @Override
  public void write(OutputStream outputStream) throws IOException {
    QueryResponseWriter qrWriter;
    Writer writer = null;

    if (core == null) {

      if (MediaType.APPLICATION_JSON.isCompatible(getMediaType())
          || MediaType.APPLICATION_JAVASCRIPT.isCompatible(getMediaType())) {
        qrWriter = new JSONResponseWriter();
      }
      else {
        qrWriter = new XMLResponseWriter();
      }
    }
    else {
      // core.getQueryResponseWriter(solrQueryRequest)
      qrWriter = core.getQueryResponseWriter(solrQueryRequest);
      if (qrWriter instanceof BinaryResponseWriter) {
        ((BinaryResponseWriter) qrWriter).write(outputStream, solrQueryRequest, solrQueryResponse);
        return;
      }
    }

    if (getCharacterSet() != null) {
      writer = new OutputStreamWriter(outputStream, getCharacterSet().getName());
    }
    else {
      // Use the default HTTP character set
      writer = new OutputStreamWriter(outputStream, CharacterSet.ISO_8859_1.getName());
    }

    write(writer);
    writer.flush();

    super.write(outputStream);
  }

}