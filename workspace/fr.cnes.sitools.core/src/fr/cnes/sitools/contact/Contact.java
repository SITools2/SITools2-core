package fr.cnes.sitools.contact;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Contact")
public class Contact {

  private String name;
  
  private String email;
  
  private String body;
  
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}
