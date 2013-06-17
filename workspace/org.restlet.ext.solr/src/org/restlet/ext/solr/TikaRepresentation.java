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
package org.restlet.ext.solr;

import java.io.IOException;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.restlet.engine.util.DateUtils;
import org.restlet.ext.xml.SaxRepresentation;
import org.restlet.representation.Representation;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Representation that parses another wrapped representation using Lucene Tika
 * metadata extraction engine.
 * 
 * Tika can be configured to indicates a specific parser to used, otherwise a
 * special auto-detect parser is used. Tike metadata can also be customized if
 * wanted.
 * 
 * @author Jerome Louvel
 */
public class TikaRepresentation extends SaxRepresentation {

    /** The Tika metadata used for both input and output. */
    private Metadata metadata;

    /** The wrapped representation to analyze. */
    private Representation representation;

    /** The optional Tika configuration. */
    private TikaConfig tikaConfig;

    /** The optional Tika parser. */
    private Parser tikaParser;

    /**
     * Constructor.
     * 
     * @param representation
     *            The wrapped representation to analyze.
     */
    public TikaRepresentation(Representation representation) {
        this(representation, null);
    }

    /**
     * Constructor.
     * 
     * @param representation
     *            The wrapped representation to analyze.
     * @param tikaConfig
     *            The optional Tika configuration.
     */
    public TikaRepresentation(Representation representation,
            TikaConfig tikaConfig) {
        this(representation, tikaConfig, null);
    }

    /**
     * Constructor.
     * 
     * @param representation
     *            The wrapped representation to analyze.
     * @param tikaConfig
     *            The optional Tika configuration.
     * @param tikaParser
     *            The optional Tika parser.
     */
    public TikaRepresentation(Representation representation,
            TikaConfig tikaConfig, Parser tikaParser) {
        super((representation == null) ? null : representation.getMediaType());
        this.tikaConfig = tikaConfig;
        this.representation = representation;
        this.metadata = new Metadata();
    }

    /**
     * Returns the Tika metadata used for both input and output.
     * 
     * @return The Tika metadata used for both input and output.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Returns the optional Tika configuration.
     * 
     * @return The Tika configuration or null.
     */
    public TikaConfig getTikaConfig() {
        return tikaConfig;
    }

    /**
     * Returns the optional Tika parser.
     * 
     * @return The Tika parser or null.
     */
    public Parser getTikaParser() {
        return tikaParser;
    }

    /**
     * Parsed the wrapped representation with Tika to extract the useful
     * metadata and produce structural SAX events (in XHTML format) and send
     * them to the given SAX content handler.
     * 
     * @param contentHandler
     *            The target SAX handler.
     */
    @Override
    public void parse(ContentHandler contentHandler) throws IOException {
        if (this.representation != null) {
            try {
                // Add common HTTP metadata
                if (this.representation.getDisposition() != null) {
                    String name = this.representation.getDisposition()
                            .getFilename();
                    if (name != null) {
                        getMetadata().set(TikaMetadataKeys.RESOURCE_NAME_KEY,
                                name);
                        getMetadata()
                                .set(HttpHeaders.CONTENT_DISPOSITION, name);
                    }
                }

                getMetadata().set(HttpHeaders.CONTENT_TYPE,
                        this.representation.getMediaType().toString());

                if (this.representation.getSize() != UNKNOWN_SIZE) {
                    getMetadata().set(HttpHeaders.CONTENT_LENGTH,
                            Long.toString(this.representation.getSize()));
                }

                if (this.representation.getModificationDate() != null) {
                    getMetadata().set(
                            HttpHeaders.LAST_MODIFIED,
                            DateUtils.format(this.representation
                                    .getModificationDate()));
                }

                // Prepare the Tika parser
                Parser parser = (getTikaParser() != null) ? getTikaParser()
                        : (getTikaConfig() != null) ? new AutoDetectParser(
                                getTikaConfig()) : new AutoDetectParser();

                // Parse the wrapped representation
                parser.parse(this.representation.getStream(), contentHandler,
                        getMetadata(), new ParseContext());
            } catch (SAXException e) {
                throw new IOException("SAX exception: "
                        + e.getLocalizedMessage());
            } catch (TikaException e) {
                throw new IOException("Tika exception: "
                        + e.getLocalizedMessage());
            }
        } else {
            throw new IOException("No wrapped representation to parse.");
        }
    }

    /**
     * The Tika metadata used for both input and output.
     * 
     * @param metadata
     *            The Tika metadata.
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Sets the optional Tika configuration.
     * 
     * @param tikaConfig
     *            The Tika configuration.
     */
    public void setTikaConfig(TikaConfig tikaConfig) {
        this.tikaConfig = tikaConfig;
    }

    /**
     * Sets the optional Tika parser.
     * 
     * @param tikaParser
     *            The Tika parser.
     */
    public void setTikaParser(Parser tikaParser) {
        this.tikaParser = tikaParser;
    }
}
