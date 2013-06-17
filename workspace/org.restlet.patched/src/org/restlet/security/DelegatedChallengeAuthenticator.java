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
package org.restlet.security;

import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;

/**
 * To allow a single inherited class encapsulating a ChallengeAuthenticator
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class DelegatedChallengeAuthenticator extends ChallengeAuthenticator {

  /** Delegated authenticator */
  private ChallengeAuthenticator innerAuthenticator = null;

  /**
   * Private constructor to prevent standard creation of a DelegatedChallengeAuthenticator without a delegated
   * authenticator
   * 
   * @param context
   *          Context
   * @param optional
   *          boolean
   * @param challengeScheme
   *          ChallengeScheme
   * @param realm
   *          String
   * @param verifier
   *          Verifier
   */
  private DelegatedChallengeAuthenticator(Context context, boolean optional, ChallengeScheme challengeScheme,
      String realm, Verifier verifier) {
    super(context, optional, challengeScheme, realm, verifier);
    this.innerAuthenticator = this;
  }

  /**
   * Private constructor to prevent standard creation of a DelegatedChallengeAuthenticator without a delegated
   * authenticator
   * 
   * @param context
   *          Context
   * 
   * @param optional
   *          boolean
   * 
   * @param challengeScheme
   *          ChallengeScheme
   * 
   * @param realm
   *          String
   */
  private DelegatedChallengeAuthenticator(Context context, boolean optional, ChallengeScheme challengeScheme,
      String realm) {
    super(context, optional, challengeScheme, realm);
    this.innerAuthenticator = this;
  }

  /**
   * Private constructor to prevent standard creation of a DelegatedChallengeAuthenticator without a delegated
   * authenticator
   * 
   * @param context
   *          Context
   * 
   * @param challengeScheme
   *          ChallengeScheme
   * 
   * @param realm
   *          String
   */
  private DelegatedChallengeAuthenticator(Context context, ChallengeScheme challengeScheme, String realm) {
    super(context, challengeScheme, realm);
    this.innerAuthenticator = this;
  }

  /**
   * Private constructor to prevent standard creation of a DelegatedChallengeAuthenticator without a delegated
   * authenticator
   * 
   * @param context
   *          Context
   * @param optional
   *          boolean
   * @param challengeScheme
   *          ChallengeScheme
   * @param realm
   *          String
   * @param verifier
   *          Verifier
   * @param innerAuthenticator
   *          ChallengeAuthenticator
   */
  private DelegatedChallengeAuthenticator(Context context, boolean optional, ChallengeScheme challengeScheme,
      String realm, Verifier verifier, ChallengeAuthenticator innerAuthenticator) {
    super(context, optional, challengeScheme, realm, verifier);
    if (innerAuthenticator == null) {
      this.innerAuthenticator = this;
    }
    else {
      this.innerAuthenticator = innerAuthenticator;
    }
  }

  /**
   * Constructor
   * 
   * @param context
   *          Context
   * @param optional
   *          boolean
   * @param challengeScheme
   *          ChallengeScheme
   * @param realm
   *          String
   * @param innerAuthenticator
   *          ChallengeAuthenticator
   */
  public DelegatedChallengeAuthenticator(Context context, boolean optional, ChallengeScheme challengeScheme,
      String realm, ChallengeAuthenticator innerAuthenticator) {
    super(context, optional, challengeScheme, realm);
    if (innerAuthenticator == null) {
      this.innerAuthenticator = this;
    }
    else {
      this.innerAuthenticator = innerAuthenticator;
    }
  }

  /**
   * Constructor
   * 
   * @param context
   *          Context
   * @param challengeScheme
   *          ChallengeScheme
   * @param realm
   *          String
   * @param innerAuthenticator
   *          ChallengeAuthenticator
   */
  public DelegatedChallengeAuthenticator(Context context, ChallengeScheme challengeScheme, String realm,
      ChallengeAuthenticator innerAuthenticator) {
    super(context, challengeScheme, realm);
    if (innerAuthenticator == null) {
      this.innerAuthenticator = this;
    }
    else {
      this.innerAuthenticator = innerAuthenticator;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    if (innerAuthenticator != null) {
      return innerAuthenticator.hashCode();
    }
    else {
      return super.hashCode();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    return innerAuthenticator.equals(obj);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#getApplication()
   */
  @Override
  public Application getApplication() {
    return innerAuthenticator.getApplication();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#getAuthor()
   */
  @Override
  public String getAuthor() {
    return innerAuthenticator.getAuthor();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#getContext()
   */
  @Override
  public Context getContext() {
    return innerAuthenticator.getContext();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#getDescription()
   */
  @Override
  public String getDescription() {
    return innerAuthenticator.getDescription();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#getLogger()
   */
  @Override
  public Logger getLogger() {
    return innerAuthenticator.getLogger();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#getName()
   */
  @Override
  public String getName() {
    return innerAuthenticator.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#getOwner()
   */
  @Override
  public String getOwner() {
    return innerAuthenticator.getOwner();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#getNext()
   */
  @Override
  public Restlet getNext() {
    return innerAuthenticator.getNext();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.Authenticator#getEnroler()
   */
  @Override
  public Enroler getEnroler() {
    return innerAuthenticator.getEnroler();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.Authenticator#isOptional()
   */
  @Override
  public boolean isOptional() {
    return innerAuthenticator.isOptional();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.Authenticator#setEnroler(org.restlet.security.Enroler)
   */
  @Override
  public void setEnroler(Enroler enroler) {
    innerAuthenticator.setEnroler(enroler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#hasNext()
   */
  @Override
  public boolean hasNext() {
    return innerAuthenticator.hasNext();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#setNext(java.lang.Class)
   */
  @Override
  public void setNext(Class<?> targetClass) {
    innerAuthenticator.setNext(targetClass);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.Authenticator#setOptional(boolean)
   */
  @Override
  public void setOptional(boolean optional) {
    innerAuthenticator.setOptional(optional);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#isStarted()
   */
  @Override
  public boolean isStarted() {
    return innerAuthenticator.isStarted();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#isStopped()
   */
  @Override
  public boolean isStopped() {
    return innerAuthenticator.isStopped();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#setNext(org.restlet.Restlet)
   */
  @Override
  public void setNext(Restlet next) {
    innerAuthenticator.setNext(next);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#setAuthor(java.lang.String)
   */
  @Override
  public void setAuthor(String author) {
    innerAuthenticator.setAuthor(author);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#setContext(org.restlet.Context)
   */
  @Override
  public void setContext(Context context) {
    innerAuthenticator.setContext(context);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#start()
   */
  @Override
  public void start() throws Exception {
    innerAuthenticator.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#setDescription(java.lang.String)
   */
  @Override
  public void setDescription(String description) {
    innerAuthenticator.setDescription(description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    innerAuthenticator.setName(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @throws Exception
   * 
   * @see org.restlet.routing.Filter#stop()
   */
  @Override
  public void stop() throws Exception {
    innerAuthenticator.stop();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Restlet#setOwner(java.lang.String)
   */
  @Override
  public void setOwner(String owner) {
    innerAuthenticator.setOwner(owner);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (innerAuthenticator != null) {
      return innerAuthenticator.toString();
    }
    else {
      return super.toString();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#getRealm()
   */
  @Override
  public String getRealm() {
    return innerAuthenticator.getRealm();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#getScheme()
   */
  @Override
  public ChallengeScheme getScheme() {
    return innerAuthenticator.getScheme();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#getVerifier()
   */
  @Override
  public Verifier getVerifier() {
    return innerAuthenticator.getVerifier();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#isRechallenging()
   */
  @Override
  public boolean isRechallenging() {
    return innerAuthenticator.isRechallenging();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#setRealm(java.lang.String)
   */
  @Override
  public void setRealm(String realm) {
    innerAuthenticator.setRealm(realm);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#setRechallenging(boolean)
   */
  @Override
  public void setRechallenging(boolean rechallenging) {
    innerAuthenticator.setRechallenging(rechallenging);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#setVerifier(org.restlet.security.Verifier)
   */
  @Override
  public void setVerifier(Verifier verifier) {
    innerAuthenticator.setVerifier(verifier);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#challenge(org.restlet.Response, boolean)
   */
  @Override
  public void challenge(Response response, boolean stale) {
    innerAuthenticator.challenge(response, stale);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#forbid(org.restlet.Response)
   */
  @Override
  public void forbid(Response response) {
    innerAuthenticator.forbid(response);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.ChallengeAuthenticator#authenticate(org.restlet.Request, org.restlet.Response)
   */
  @Override
  public boolean authenticate(Request request, Response response) {
    return innerAuthenticator.authenticate(request, response);
  }

}
