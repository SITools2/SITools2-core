 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package primitive;

public class Config {

  private String server;
  private int port;
  private String browser;
  private String url;

  @Override
  public String toString() {
    return "server: " + server + ", port: " + port + ", browser: " + browser + ", url: " + url;
  }

  /**
   * Get Selenium server hostName
   * 
   * @return server hostnane or IP address
   */
  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  /**
   * Get Selenium server port
   * 
   * @return server port
   */
  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Get browser to launch
   * 
   * @return browser to launch (ex: *firefox)
   */
  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  /**
   * Get Web Application url to load
   * 
   * @return url to load
   */
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
