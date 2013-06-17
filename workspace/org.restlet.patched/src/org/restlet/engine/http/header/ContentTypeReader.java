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
package org.restlet.engine.http.header;

import java.io.IOException;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.util.Series;

/**
 * Content type header reader.
 * 
 * @author Jerome Louvel
 */
public class ContentTypeReader extends HeaderReader<ContentType> {

    /**
     * Constructor.
     * 
     * @param header
     *            The header to read.
     */
    public ContentTypeReader(String header) {
        super(header);
    }

    /**
     * Creates a content type.
     * 
     * @param mediaType
     *            The media type name.
     * @param parameters
     *            The parameters parsed.
     * @return The content type.
     */
    private ContentType createContentType(StringBuilder mediaType,
            Series<Parameter> parameters) {
        // Attempt to extract the character set
        CharacterSet characterSet = null;

        if (parameters != null) {
            String charSet = parameters.getFirstValue("charset");

            if (charSet != null) {
                parameters.removeAll("charset");
                characterSet = new CharacterSet(charSet);
            }

            return new ContentType(new MediaType(mediaType.toString(),
                    parameters), characterSet);
        }

        return new ContentType(new MediaType(mediaType.toString()), null);
    }

    @Override
    public ContentType readValue() throws IOException {
        ContentType result = null;
        
        boolean readingMediaType = true;
        boolean readingParamName = false;
        boolean readingParamValue = false;

        StringBuilder mediaTypeBuffer = new StringBuilder();
        StringBuilder paramNameBuffer = null;
        StringBuilder paramValueBuffer = null;

        Series<Parameter> parameters = null;
        String nextValue = readRawValue();
        int nextIndex = 0;

        if (nextValue != null) {
            int nextChar = nextValue.charAt(nextIndex++);

            while (result == null) {
                if (readingMediaType) {
                    if (nextChar == -1) {
                        if (mediaTypeBuffer.length() > 0) {
                            // End of metadata section
                            // No parameters detected
                            result = createContentType(mediaTypeBuffer, null);
                            paramNameBuffer = new StringBuilder();
                        } else {
                            // Ignore empty metadata name
                        }
                    } else if (nextChar == ';') {
                        if (mediaTypeBuffer.length() > 0) {
                            // End of mediaType section
                            // Parameters detected
                            readingMediaType = false;
                            readingParamName = true;
                            paramNameBuffer = new StringBuilder();
                            parameters = new Form();
                        } else {
                            throw new IOException(
                                    "Empty mediaType name detected.");
                        }
                    } else if (HeaderUtils.isSpace(nextChar)) {
                        // Ignore spaces
                    } else if (HeaderUtils.isText(nextChar)) {
                        mediaTypeBuffer.append((char) nextChar);
                    } else {
                        throw new IOException(
                                "The "
                                        + (char) nextChar
                                        + " character isn't allowed in a media type name.");
                    }
                } else if (readingParamName) {
                    if (nextChar == '=') {
                        if (paramNameBuffer.length() > 0) {
                            // End of parameter name section
                            readingParamName = false;
                            readingParamValue = true;
                            paramValueBuffer = new StringBuilder();
                        } else {
                            throw new IOException(
                                    "Empty parameter name detected.");
                        }
                    } else if (nextChar == -1) {
                        if (paramNameBuffer.length() > 0) {
                            // End of parameters section
                            parameters.add(Parameter.create(paramNameBuffer,
                                    null));
                            result = createContentType(mediaTypeBuffer,
                                    parameters);
                        } else {
                            throw new IOException(
                                    "Empty parameter name detected.");
                        }
                    } else if (nextChar == ';') {
                        // End of parameter
                        parameters.add(Parameter.create(paramNameBuffer, null));
                        paramNameBuffer = new StringBuilder();
                        readingParamName = true;
                        readingParamValue = false;
                    } else if (HeaderUtils.isSpace(nextChar)
                            && (paramNameBuffer.length() == 0)) {
                        // Ignore white spaces
                    } else if (HeaderUtils.isTokenChar(nextChar)) {
                        paramNameBuffer.append((char) nextChar);
                    } else {
                        throw new IOException(
                                "The \""
                                        + (char) nextChar
                                        + "\" character isn't allowed in a media type parameter name.");
                    }
                } else if (readingParamValue) {
                    if (nextChar == -1) {
                        if (paramValueBuffer.length() > 0) {
                            // End of parameters section
                            parameters.add(Parameter.create(paramNameBuffer,
                                    paramValueBuffer));
                            result = createContentType(mediaTypeBuffer,
                                    parameters);
                        } else {
                            throw new IOException(
                                    "Empty parameter value detected");
                        }
                    } else if (nextChar == ';') {
                        // End of parameter
                        parameters.add(Parameter.create(paramNameBuffer,
                                paramValueBuffer));
                        paramNameBuffer = new StringBuilder();
                        readingParamName = true;
                        readingParamValue = false;
                    } else if ((nextChar == '"')
                            && (paramValueBuffer.length() == 0)) {
                        // Parse the quoted string
                        boolean done = false;
                        boolean quotedPair = false;

                        while ((!done) && (nextChar != -1)) {
                            nextChar = (nextIndex < nextValue.length()) ? nextValue
                                    .charAt(nextIndex++)
                                    : -1;

                            if (quotedPair) {
                                // End of quoted pair (escape sequence)
                                if (HeaderUtils.isText(nextChar)) {
                                    paramValueBuffer.append((char) nextChar);
                                    quotedPair = false;
                                } else {
                                    throw new IOException(
                                            "Invalid character \""
                                                    + (char) nextChar
                                                    + "\" detected in quoted string. Please check your value");
                                }
                            } else if (HeaderUtils.isDoubleQuote(nextChar)) {
                                // End of quoted string
                                done = true;
                            } else if (nextChar == '\\') {
                                // Begin of quoted pair (escape sequence)
                                quotedPair = true;
                            } else if (HeaderUtils.isText(nextChar)) {
                                paramValueBuffer.append((char) nextChar);
                            } else {
                                throw new IOException(
                                        "Invalid character \""
                                                + (char) nextChar
                                                + "\" detected in quoted string. Please check your value");
                            }
                        }
                    //PATCH SITOOLS2 IN ORDER TO DEAL WITH WEIRD CONTENT TYPE SUCH AS : (text/xml; subtype=gml/2.1.2)
//                    } else if (HeaderUtils.isTokenChar(nextChar)) {
                    } else if (HeaderUtils.isText(nextChar)) {
                        paramValueBuffer.append((char) nextChar);
                    } else {
                        throw new IOException(
                                "The \""
                                        + (char) nextChar
                                        + "\" character isn't allowed in a media type parameter value.");
                    }
                }

                nextChar = (nextIndex < nextValue.length()) ? nextValue
                        .charAt(nextIndex++) : -1;
            }
        }

        return result;
    }

}
