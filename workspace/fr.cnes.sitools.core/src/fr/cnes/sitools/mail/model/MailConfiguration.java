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
package fr.cnes.sitools.mail.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Sending / Receiving mail configuration.
 * 
 * <code>
 * Message.getProperties("Starter.mail.send.debug"); // default=true
 * Message.getProperties("Starter.mail.send.tls"); // default=false
 * Message.getProperties("Starter.mail.send.identifier"); // default=
 * Message.getProperties("Starter.mail.send.secret"); // default=
 * Message.getProperties("Starter.mail.send.server"); // default=smtp://smtp.silogic.fr
 * Message.getProperties("Starter.mail.send.encoding"); // default=UTF-8
 * Message.getProperties("Starter.mail.send.admin"); // default=jp.boignard@akka.eu
 * </code>
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@XStreamAlias("mailconfig")
public final class MailConfiguration {

  /** mode debug */
  private String debug = "false";

  /** ssl */
  private boolean tls;

  /** login for mail */
  private String mailIdentifier;

  /** password for mail - TODO must be encrypted */
  private String mailSecret;

  /** smtp / smtps server URL. Example : "smtp://smtp.silogic.fr"; */
  private String mailServer;

  /** server port */
  private String mailPort;

  /** default sitools administration email */
  private String admin;

  /**
   * Default constructor
   */
  public MailConfiguration() {
  }

  /**
   * Gets the debug value
   * 
   * @return the debug
   */
  public String getDebug() {
    return debug;
  }

  /**
   * Sets the value of debug
   * 
   * @param debug
   *          the debug to set
   */
  public void setDebug(String debug) {
    this.debug = debug;
  }

  /**
   * Gets the tls value
   * 
   * @return the tls
   */
  public boolean isTls() {
    return tls;
  }

  /**
   * Sets the value of tls
   * 
   * @param tls
   *          the tls to set
   */
  public void setTls(boolean tls) {
    this.tls = tls;
  }

  /**
   * Gets the mailIdentifier value
   * 
   * @return the mailIdentifier
   */
  public String getMailIdentifier() {
    return mailIdentifier;
  }

  /**
   * Sets the value of mailIdentifier
   * 
   * @param mailIdentifier
   *          the mailIdentifier to set
   */
  public void setMailIdentifier(String mailIdentifier) {
    this.mailIdentifier = mailIdentifier;
  }

  /**
   * Gets the mailSecret value
   * 
   * @return the mailSecret
   */
  public String getMailSecret() {
    return mailSecret;
  }

  /**
   * Sets the value of mailSecret
   * 
   * @param mailSecret
   *          the mailSecret to set
   */
  public void setMailSecret(String mailSecret) {
    this.mailSecret = mailSecret;
  }

  /**
   * Gets the mailServer value
   * 
   * @return the mailServer
   */
  public String getMailServer() {
    return mailServer;
  }

  /**
   * Sets the value of mailServer
   * 
   * @param mailServer
   *          the mailServer to set
   */
  public void setMailServer(String mailServer) {
    this.mailServer = mailServer;
  }

  /**
   * Gets the mailPort value
   * 
   * @return the mailPort
   */
  public String getMailPort() {
    return mailPort;
  }

  /**
   * Sets the value of mailPort
   * 
   * @param mailPort
   *          the mailPort to set
   */
  public void setMailPort(String mailPort) {
    this.mailPort = mailPort;
  }

  /**
   * Gets the admin value
   * 
   * @return the admin
   */
  public String getAdmin() {
    return admin;
  }

  /**
   * Sets the value of admin
   * 
   * @param admin
   *          the admin to set
   */
  public void setAdmin(String admin) {
    this.admin = admin;
  }

}
