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
package fr.cnes.sitools.mail;

import java.io.IOException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.restlet.ext.javamail.RepresentationMessage;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.mail.model.Mail;

/**
 * Sitools representation message
 * 
 * @author AKKA Technologies
 * 
 */
public class SitoolsRepresentationMessage extends RepresentationMessage {

  /**
   * Class allowing connector restlet.ext.mail to accept a Mail SITools object instead of XML representation.
   * 
   * @param messageRepresentation
   *          mail message representation
   * @param session
   *          mail session
   * @throws IOException
   *           IOException when getting mail Object from representation
   * @throws MessagingException
   *           when messaging fails
   */
  public SitoolsRepresentationMessage(Representation messageRepresentation, Session session) throws IOException,
      MessagingException {
    super(session);
    if (messageRepresentation instanceof ObjectRepresentation<?>) {
      @SuppressWarnings("unchecked")
      Mail mail = ((ObjectRepresentation<Mail>) messageRepresentation).getObject();

      String from = mail.getFrom();
      if ((from == null) || from.equals("")) {
        from = (SitoolsSettings.getInstance().getString("Starter.mail.send.admin"));
      }

      String[] to = new String[mail.getToList().size()];
      for (int i = 0; i < mail.getToList().size(); i++) {
        to[i] = mail.getToList().get(i);
      }

      String[] cc = new String[mail.getCcList().size()];

      for (int i = 0; i < mail.getCcList().size(); i++) {
        cc[i] = mail.getCcList().get(i);
      }

      String[] bcc = new String[mail.getBccList().size()];

      for (int i = 0; i < mail.getBccList().size(); i++) {
        bcc[i] = mail.getBccList().get(i);
      }

      String text = mail.getBody();

      // Set the FROM and TO fields
      setFrom(new InternetAddress(from));

      for (String element : to) {
        addRecipient(Message.RecipientType.TO, new InternetAddress(element));
      }

      for (String element : cc) {
        addRecipient(Message.RecipientType.CC, new InternetAddress(element));
      }

      for (final String element : bcc) {
        addRecipient(Message.RecipientType.BCC, new InternetAddress(element));
      }

      // Set the subject and content text
      setSubject(mail.getSubject());
      // setText(text);
      // OR
      setContent(text, "text/html");

//      // ====================================
//      // This HTML mail with multipart
//      //
//      MimeMultipart multipart = new MimeMultipart("related");
//
//      // first part (the html)
//      BodyPart messageBodyPart = new MimeBodyPart();
//      String htmlText = "<H1>Hello</H1><img src=\"cid:image\">";
//      messageBodyPart.setContent(htmlText, "text/html");
//
//      // add it
//      multipart.addBodyPart(messageBodyPart);
//
//      // // second part (the image)
//      // messageBodyPart = new MimeBodyPart();
//      // DataSource fds = new FileDataSource
//      // ("C:\\images\\jht.gif");
//      // messageBodyPart.setDataHandler(new DataHandler(fds));
//      // messageBodyPart.setHeader("Content-ID","<image>");
//
//      // // add it
//      // multipart.addBodyPart(messageBodyPart);
//
//      // put everything together
//      setContent(multipart);

      setSentDate((mail.getSentDate() != null) ? mail.getSentDate() : new Date());
      saveChanges();
    }
    else {
      DomRepresentation dom = new DomRepresentation(messageRepresentation);
      Document email = dom.getDocument();
      Element root = (Element) email.getElementsByTagName("email").item(0);
      Element header = (Element) root.getElementsByTagName("head").item(0);
      String subject = header.getElementsByTagName("subject").item(0).getTextContent();
      String from = header.getElementsByTagName("from").item(0).getTextContent();

      NodeList toList = header.getElementsByTagName("to");
      String[] to = new String[toList.getLength()];

      for (int i = 0; i < toList.getLength(); i++) {
        to[i] = toList.item(i).getTextContent();
      }

      NodeList ccList = header.getElementsByTagName("cc");
      String[] cc = new String[ccList.getLength()];

      for (int i = 0; i < ccList.getLength(); i++) {
        cc[i] = ccList.item(i).getTextContent();
      }

      NodeList bccList = header.getElementsByTagName("bcc");
      String[] bcc = new String[bccList.getLength()];

      for (int i = 0; i < bccList.getLength(); i++) {
        bcc[i] = bccList.item(i).getTextContent();
      }

      String text = root.getElementsByTagName("body").item(0).getTextContent();

      // Set the FROM and TO fields
      setFrom(new InternetAddress(from));

      for (String element : to) {
        addRecipient(Message.RecipientType.TO, new InternetAddress(element));
      }

      for (String element : cc) {
        addRecipient(Message.RecipientType.CC, new InternetAddress(element));
      }

      for (final String element : bcc) {
        addRecipient(Message.RecipientType.BCC, new InternetAddress(element));
      }

      // Set the subject and content text
      setSubject(subject);
      setText(text);
      setSentDate(new Date());
      saveChanges();
    }
  }

  /**
   * Constructor
   * 
   * @param session
   *          mail session
   */
  public SitoolsRepresentationMessage(Session session) {
    super(session);
  }

}
