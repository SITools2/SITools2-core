package fr.cnes.sitools.server;

import org.restlet.Server;
import org.restlet.data.Parameter;

import fr.cnes.sitools.common.SitoolsSettings;

/**
 * Utils class used to set Jetty server properties
 * 
 * 
 * @author m.gond
 */
public class JettyProperties {

  /** Maximal header buffer size for request */
  public static final int DEFAULT_REQUEST_HEADER_SIZE = 512 * 1024;
  /** Maximal header buffer size for request */
  public static final int DEFAULT_RESPONSE_HEADER_SIZE = 512 * 1024;
  /** Other properties */
  public static final int DEFAULT_MIN_THREADS = 1;
  public static final int DEFAULT_MAX_THREADS = 255;
  public static final int DEFAULT_THREAD_MAX_IDLE_TIME_MS = 60000;
  public static final int DEFAULT_LOW_RESOURCES_MAX_IDLE_TIME_MS = 2500;
  public static final int DEFAULT_ACCEPTOR_THREADS = 1;
  public static final int DEFAULT_ACCEPT_QUEUE_SIZE = 0;
  public static final int DEFAULT_REQUEST_BUFFER_SIZE = 8192;
  public static final int DEFAULT_RESPONSE_BUFFER_SIZE = 32768;
  public static final int DEFAULT_IO_MAX_IDLE_TIME_MS = 30000;
  public static final int DEFAULT_SO_LINGER_TIME = 1000;
  public static final int DEFAULT_GRACEFUL_SHUTDOWN = 0;

  private int requestHeaderSize;
  private int responseHeaderSize;
  private int minThreads;
  private int maxThreads;
  private int threadMaxIdleTimeMs;
  private int lowResourcesMaxIdleTimeMs;
  private int acceptorThreads;
  private int acceptQueueSize;
  private int requestBufferSize;
  private int responseBufferSize;
  private int ioMaxIdleTimeMs;
  private int soLingerTime;
  private int gracefulShutdown;

  /**
   * setValues
   * 
   * @param settings
   *          the sitools Settings object
   */
  public void setValues(SitoolsSettings settings) {

    try {
      setRequestHeaderSize(settings.getInt(Consts.REQUEST_HEADER_SIZE));
    }
    catch (Exception e) {
      setRequestHeaderSize(DEFAULT_REQUEST_HEADER_SIZE);
    }

    try {
      setResponseHeaderSize(settings.getInt(Consts.RESPONSE_HEADER_SIZE));
    }
    catch (Exception e) {
      setResponseHeaderSize(DEFAULT_RESPONSE_HEADER_SIZE);
    }

    try {
      setMinThreads(settings.getInt(Consts.MIN_THREADS));
    }
    catch (Exception e) {
      setMinThreads(DEFAULT_MIN_THREADS);
    }

    try {
      setMaxThreads(settings.getInt(Consts.MAX_THREADS));
    }
    catch (Exception e) {
      setMaxThreads(DEFAULT_MAX_THREADS);
    }

    try {
      setThreadMaxIdleTimeMs(settings.getInt(Consts.THREAD_MAX_IDLE_TIME_MS));
    }
    catch (Exception e) {
      setThreadMaxIdleTimeMs(DEFAULT_THREAD_MAX_IDLE_TIME_MS);
    }

    try {
      setLowResourcesMaxIdleTimeMs(settings.getInt(Consts.LOW_RESOURCES_MAX_IDLE_TIME_MS));
    }
    catch (Exception e) {
      setLowResourcesMaxIdleTimeMs(DEFAULT_LOW_RESOURCES_MAX_IDLE_TIME_MS);
    }

    try {
      setAcceptorThreads(settings.getInt(Consts.ACCEPTOR_THREADS));
    }
    catch (Exception e) {
      setAcceptorThreads(DEFAULT_ACCEPTOR_THREADS);
    }

    try {
      setAcceptQueueSize(settings.getInt(Consts.ACCEPT_QUEUE_SIZE));
    }
    catch (Exception e) {
      setAcceptQueueSize(DEFAULT_ACCEPT_QUEUE_SIZE);
    }

    try {
      setRequestBufferSize(settings.getInt(Consts.REQUEST_BUFFER_SIZE));
    }
    catch (Exception e) {
      setRequestBufferSize(DEFAULT_REQUEST_BUFFER_SIZE);
    }

    try {
      setResponseBufferSize(settings.getInt(Consts.RESPONSE_BUFFER_SIZE));
    }
    catch (Exception e) {
      setResponseBufferSize(DEFAULT_RESPONSE_BUFFER_SIZE);
    }

    try {
      setIoMaxIdleTimeMs(settings.getInt(Consts.IO_MAX_IDLE_TIME_MS));
    }
    catch (Exception e) {
      setIoMaxIdleTimeMs(DEFAULT_IO_MAX_IDLE_TIME_MS);
    }

    try {
      setSoLingerTime(settings.getInt(Consts.SO_LINGER_TIME));
    }
    catch (Exception e) {
      setSoLingerTime(DEFAULT_SO_LINGER_TIME);
    }

    try {
      setGracefulShutdown(settings.getInt(Consts.GRACEFUL_SHUTDOWN));
    }
    catch (Exception e) {
      setGracefulShutdown(DEFAULT_GRACEFUL_SHUTDOWN);
    }

  }

  /**
   * addParamsToServerContext
   * 
   * @param serverHTTP
   */
  public void addParamsToServerContext(Server serverHTTP) {

    addParam(serverHTTP, "requestHeaderSize", requestHeaderSize);
    addParam(serverHTTP, "responseHeaderSize", responseHeaderSize);
    addParam(serverHTTP, "minThreads", minThreads);
    addParam(serverHTTP, "maxThreads", maxThreads);
    addParam(serverHTTP, "threadMaxIdleTimeMs", threadMaxIdleTimeMs);
    addParam(serverHTTP, "lowResourcesMaxIdleTimeMs", lowResourcesMaxIdleTimeMs);
    addParam(serverHTTP, "acceptorThreads", acceptorThreads);
    addParam(serverHTTP, "acceptQueueSize", acceptQueueSize);
    addParam(serverHTTP, "requestBufferSize", requestBufferSize);
    addParam(serverHTTP, "responseBufferSize", responseBufferSize);
    addParam(serverHTTP, "ioMaxIdleTimeMs", ioMaxIdleTimeMs);
    addParam(serverHTTP, "soLingerTime", soLingerTime);
    addParam(serverHTTP, "gracefulShutdown", gracefulShutdown);

  }

  /**
   * addParam
   * 
   * @param serverHTTP
   * @param propName
   * @param propValue
   */
  private void addParam(Server serverHTTP, String propName, int propValue) {

    Parameter param = serverHTTP.getContext().getParameters().getFirst(propName);
    if (param != null) {
      param.setValue("" + propValue);
    }
    else {
      serverHTTP.getContext().getParameters().add(propName, "" + propValue);
    }

  }

  /** GETTERS and SETTERS */

  /**
   * Gets the requestHeaderSize value
   * 
   * @return the requestHeaderSize
   */
  public int getRequestHeaderSize() {
    return requestHeaderSize;
  }

  /**
   * Sets the value of requestHeaderSize
   * 
   * @param requestHeaderSize
   *          the requestHeaderSize to set
   */
  public void setRequestHeaderSize(int requestHeaderSize) {
    this.requestHeaderSize = requestHeaderSize;
  }

  /**
   * Gets the responseHeaderSize value
   * 
   * @return the responseHeaderSize
   */
  public int getResponseHeaderSize() {
    return responseHeaderSize;
  }

  /**
   * Sets the value of responseHeaderSize
   * 
   * @param responseHeaderSize
   *          the responseHeaderSize to set
   */
  public void setResponseHeaderSize(int responseHeaderSize) {
    this.responseHeaderSize = responseHeaderSize;
  }

  /**
   * Gets the minThreads value
   * 
   * @return the minThreads
   */
  public int getMinThreads() {
    return minThreads;
  }

  /**
   * Sets the value of minThreads
   * 
   * @param minThreads
   *          the minThreads to set
   */
  public void setMinThreads(int minThreads) {
    this.minThreads = minThreads;
  }

  /**
   * Gets the maxThreads value
   * 
   * @return the maxThreads
   */
  public int getMaxThreads() {
    return maxThreads;
  }

  /**
   * Sets the value of maxThreads
   * 
   * @param maxThreads
   *          the maxThreads to set
   */
  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }

  /**
   * Gets the threadMaxIdleTimeMs value
   * 
   * @return the threadMaxIdleTimeMs
   */
  public int getThreadMaxIdleTimeMs() {
    return threadMaxIdleTimeMs;
  }

  /**
   * Sets the value of threadMaxIdleTimeMs
   * 
   * @param threadMaxIdleTimeMs
   *          the threadMaxIdleTimeMs to set
   */
  public void setThreadMaxIdleTimeMs(int threadMaxIdleTimeMs) {
    this.threadMaxIdleTimeMs = threadMaxIdleTimeMs;
  }

  /**
   * Gets the lowResourcesMaxIdleTimeMs value
   * 
   * @return the lowResourcesMaxIdleTimeMs
   */
  public int getLowResourcesMaxIdleTimeMs() {
    return lowResourcesMaxIdleTimeMs;
  }

  /**
   * Sets the value of lowResourcesMaxIdleTimeMs
   * 
   * @param lowResourcesMaxIdleTimeMs
   *          the lowResourcesMaxIdleTimeMs to set
   */
  public void setLowResourcesMaxIdleTimeMs(int lowResourcesMaxIdleTimeMs) {
    this.lowResourcesMaxIdleTimeMs = lowResourcesMaxIdleTimeMs;
  }

  /**
   * Gets the acceptorThreads value
   * 
   * @return the acceptorThreads
   */
  public int getAcceptorThreads() {
    return acceptorThreads;
  }

  /**
   * Sets the value of acceptorThreads
   * 
   * @param acceptorThreads
   *          the acceptorThreads to set
   */
  public void setAcceptorThreads(int acceptorThreads) {
    this.acceptorThreads = acceptorThreads;
  }

  /**
   * Gets the acceptQueueSize value
   * 
   * @return the acceptQueueSize
   */
  public int getAcceptQueueSize() {
    return acceptQueueSize;
  }

  /**
   * Sets the value of acceptQueueSize
   * 
   * @param acceptQueueSize
   *          the acceptQueueSize to set
   */
  public void setAcceptQueueSize(int acceptQueueSize) {
    this.acceptQueueSize = acceptQueueSize;
  }

  /**
   * Gets the requestBufferSize value
   * 
   * @return the requestBufferSize
   */
  public int getRequestBufferSize() {
    return requestBufferSize;
  }

  /**
   * Sets the value of requestBufferSize
   * 
   * @param requestBufferSize
   *          the requestBufferSize to set
   */
  public void setRequestBufferSize(int requestBufferSize) {
    this.requestBufferSize = requestBufferSize;
  }

  /**
   * Gets the responseBufferSize value
   * 
   * @return the responseBufferSize
   */
  public int getResponseBufferSize() {
    return responseBufferSize;
  }

  /**
   * Sets the value of responseBufferSize
   * 
   * @param responseBufferSize
   *          the responseBufferSize to set
   */
  public void setResponseBufferSize(int responseBufferSize) {
    this.responseBufferSize = responseBufferSize;
  }

  /**
   * Gets the ioMaxIdleTimeMs value
   * 
   * @return the ioMaxIdleTimeMs
   */
  public int getIoMaxIdleTimeMs() {
    return ioMaxIdleTimeMs;
  }

  /**
   * Sets the value of ioMaxIdleTimeMs
   * 
   * @param ioMaxIdleTimeMs
   *          the ioMaxIdleTimeMs to set
   */
  public void setIoMaxIdleTimeMs(int ioMaxIdleTimeMs) {
    this.ioMaxIdleTimeMs = ioMaxIdleTimeMs;
  }

  /**
   * Gets the soLingerTime value
   * 
   * @return the soLingerTime
   */
  public int getSoLingerTime() {
    return soLingerTime;
  }

  /**
   * Sets the value of soLingerTime
   * 
   * @param soLingerTime
   *          the soLingerTime to set
   */
  public void setSoLingerTime(int soLingerTime) {
    this.soLingerTime = soLingerTime;
  }

  /**
   * Gets the gracefulShutdown value
   * 
   * @return the gracefulShutdown
   */
  public int getGracefulShutdown() {
    return gracefulShutdown;
  }

  /**
   * Sets the value of gracefulShutdown
   * 
   * @param gracefulShutdown
   *          the gracefulShutdown to set
   */
  public void setGracefulShutdown(int gracefulShutdown) {
    this.gracefulShutdown = gracefulShutdown;
  }

}
