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
