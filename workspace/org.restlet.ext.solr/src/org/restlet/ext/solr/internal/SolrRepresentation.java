/**
 * Copyright 2005-2011 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package org.restlet.ext.solr.internal;

import java.io.IOException;
import java.io.Writer;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.JSONResponseWriter;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.response.XMLResponseWriter;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

/**
 * Representation wrapping a Solr query and exposing its response either as XML
 * or JSON.
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
	 *            The media type.
	 * @param solrQueryRequest
	 *            The wrapped Solr query request.
	 * @param solrQueryResponse
	 *            The wrapped Solr query response.
	 */
	public SolrRepresentation(MediaType mediaType,
			SolrQueryRequest solrQueryRequest,
			SolrQueryResponse solrQueryResponse) {
		super(mediaType);
		setCharacterSet(CharacterSet.UTF_8);
		this.solrQueryRequest = solrQueryRequest;
		this.solrQueryResponse = solrQueryResponse;
	}

	/**
	 * Constructor.
	 * 
	 * @param solrQueryRequest
	 *            The wrapped Solr query request.
	 * @param solrQueryResponse
	 *            The wrapped Solr query response.
	 * @see #SolrRepresentation(MediaType, SolrQueryRequest, SolrQueryResponse)
	 */
	public SolrRepresentation(SolrQueryRequest solrQueryRequest,
			SolrQueryResponse solrQueryResponse) {
		this(null, solrQueryRequest, solrQueryResponse);
	}

	/**
	 * Constructor.
	 * 
	 * @param solrQueryRequest
	 *            The wrapped Solr query request.
	 * @param solrQueryResponse
	 *            The wrapped Solr query response.
	 * @see #SolrRepresentation(MediaType, SolrQueryRequest, SolrQueryResponse)
	 */
	public SolrRepresentation(SolrQueryRequest solrQueryRequest,
			SolrQueryResponse solrQueryResponse, SolrCore core) {
		this(null, solrQueryRequest, solrQueryResponse);
		this.core = core;
	}

	@Override
	public void write(Writer writer) throws IOException {
		QueryResponseWriter qrWriter;
		if (core == null) {

			if (MediaType.APPLICATION_JSON.isCompatible(getMediaType())
					|| MediaType.APPLICATION_JAVASCRIPT
							.isCompatible(getMediaType())) {
				qrWriter = new JSONResponseWriter();
			} else {
				qrWriter = new XMLResponseWriter();
			}
		} else {
			qrWriter = core.getQueryResponseWriter(solrQueryRequest);
		}

		qrWriter.write(writer, solrQueryRequest, solrQueryResponse);
	}

}