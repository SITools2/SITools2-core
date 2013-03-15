/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.portal.multidatasets.opensearch;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.portal.model.Portal;
import fr.cnes.sitools.portal.multidatasets.opensearch.dto.OpensearchDescriptionDTO;

/**
 * Representation used for multidataset opensearch RSS feeds
 * 
 * @author AKKA technologies
 * 
 * @version
 * 
 */
public final class MutliDsOsSearchRepresentation extends OutputRepresentation {
  
  /** OpenSearch URL */
  private static final String OPENSEARCH_URL = "http://a9.com/-/spec/opensearch/1.1/";
  
  /**
   * A MultiDatasetsOpensearchResource
   */
  private MutliDsOsResource resource;
  
  /**
   * Total number of results
   */
  private int totalResults = 0; 
  /**
   * The default constructor
   * 
   * @param mediaType
   *          The mediaType
   */
  public MutliDsOsSearchRepresentation(MediaType mediaType) {
    super(mediaType);
  }

  /**
   * A constructor with MultiDatasetsOpensearchResource param
   * 
   * @param mediaType
   *          The mediaType
   * @param res
   *          The MultiDatasetsOpensearchResource
   */
  public MutliDsOsSearchRepresentation(MediaType mediaType, MutliDsOsResource res) {
    super(mediaType);
    this.resource = res;
  }
  
  
  
  
  @Override
  public void write(OutputStream outputStream) throws IOException {

    List<OpensearchDescriptionDTO> osList = resource.getOsList();

    int nbOs = osList.size();
    int nbResults = resource.getNbResults();
    String searchQuery = resource.getSearchQuery();

    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    XMLStreamWriter writer = null;
    try {
      writer = outputFactory.createXMLStreamWriter(outputStream);
      // start the document
      writer.writeStartDocument();
      // start rss
      writer.writeStartElement("rss");
      writer.writeNamespace("opensearch", OPENSEARCH_URL);
      writer.writeNamespace("atom", "http://www.w3.org/2005/Atom");
      writer.writeAttribute("version", "2.0");

      // start channel
      writer.writeStartElement("channel");

      Portal portal = resource.getPortal();

      // rss header
      if (portal.getName() != null) {
        this.writeNode("title", portal.getName(), writer);
      }
      if (portal.getUrl() != null) {
        this.writeNode("link", portal.getUrl(), writer);
      }
      if (portal.getDescription() != null) {
        this.writeNode("description", portal.getDescription(), writer);
      }
      this.writeNode("language", "en_us", writer);
      
      if (nbOs > 0) {
        int div = nbResults / nbOs;
        
        int restant = nbResults - (div * nbOs);
        
        // request each opensearch
        int i = 0;

        for (Iterator<OpensearchDescriptionDTO> iterator = osList.iterator(); iterator.hasNext();) {
          // make the urlQuery
          OpensearchDescriptionDTO osDesc = iterator.next();
          if (i == nbOs - 1) {
            div += restant;
          }
          String query = "?q=" + searchQuery + "&rows=" + div;
          // query the opensearch
          String rssOs = resource.getOpensearchRSS(query, osDesc.getIdOs());
          if (rssOs != null) {
            extractItems(rssOs, writer);
            
          }

          i++;
        }
      }
      this.writeNodeNS("totalResults", String.valueOf(this.totalResults), writer, OPENSEARCH_URL);
      this.writeNodeNS("startIndex", "0", writer, OPENSEARCH_URL);
      this.writeNodeNS("itemsPerPage", String.valueOf(nbResults), writer, OPENSEARCH_URL);
      
      // close channel
      writer.writeEndElement();
      // close rss
      writer.writeEndDocument();
      writer.writeEndDocument();
      
    }
    catch (XMLStreamException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    finally {
      try {
        writer.close();
      }
      catch (XMLStreamException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
    }
  }

  /**
   * Write a node to the following XMLStreamWriter
   * 
   * @param nodeName
   *          The node name
   * @param nodeContent
   *          The node content
   * @param writer
   *          The writer
   * @throws XMLStreamException
   *           If a exception occur
   */
  private void writeNode(String nodeName, String nodeContent, XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(nodeName);
    writer.writeCharacters(nodeContent);
    writer.writeEndElement();
  }

  /**
   * Write a node to the following XMLStreamWriter with the following
   * namespaceURI
   * 
   * @param nodeName
   *          The node name
   * @param nodeContent
   *          The node content
   * @param writer
   *          The writer
   * @param namespaceUri
   *          The namespaceURI
   * @throws XMLStreamException
   *           If a exception occur
   */
  private void writeNodeNS(String nodeName, String nodeContent, XMLStreamWriter writer, String namespaceUri)
    throws XMLStreamException {
    writer.writeStartElement(namespaceUri, nodeName);
    writer.writeCharacters(nodeContent);
    writer.writeEndElement();
  }

  /**
   * Extract the items from the given rssOS String and write it to the following
   * writer
   * 
   * @param rssOs
   *          The rssOs String input
   * @param writer
   *          The XMLStreamWriter
   */
  private void extractItems(String rssOs, XMLStreamWriter writer) {

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = null;
    String items = "";
    try {
      reader = factory.createXMLStreamReader(new StringReader(rssOs));

      boolean finish = false;
      boolean inItems = false;
      boolean inTotalResults = false;

      while (reader.hasNext() && !finish) {
        int type = reader.next();
        switch (type) {
          case XMLStreamReader.START_ELEMENT:
            if (inItems) {
              writer.writeStartElement(reader.getLocalName());
            }
            if (reader.getLocalName().equals("item")) {
              inItems = true;
              items += "<item>";
              writer.writeStartElement(reader.getLocalName());
            }
            if (reader.getLocalName().equals("totalResults")) {
              inTotalResults = true;            
            }
            break;
          case XMLStreamReader.CHARACTERS:
            if (inItems) {
              writer.writeCharacters(reader.getText());
            }
            if (inTotalResults) {
              totalResults += Integer.valueOf(reader.getText());
            }
            break;
          case XMLStreamReader.END_ELEMENT:
            if (reader.getLocalName().equals("item")) {
              inItems = false;
              writer.writeEndElement();
            }
            if (reader.getLocalName().equals("totalResults")) {
              inTotalResults = false;
            }
            if (inItems) {
              writer.writeEndElement();
            }
            break;
          default:
            break;
        }
      }

    }
    catch (XMLStreamException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    finally {
      try {
        reader.close();
      }
      catch (XMLStreamException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
    }

  }

}
