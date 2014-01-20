CREATE TABLE "sitools"."USERS" (
  identifier varchar(64)  NOT NULL,
  firstname varchar(32)  DEFAULT NULL,
  lastname varchar(32)  DEFAULT NULL,
  email varchar(128)  DEFAULT NULL,
  secret varchar(40)  DEFAULT NULL,
  PRIMARY KEY (identifier)
);

CREATE TABLE IF NOT EXISTS "sitools"."GROUPS" (
  name varchar(32) NOT NULL,
  description varchar(128)  DEFAULT NULL,
  PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS "sitools"."USER_GROUP" (
  identifier varchar(64)  NOT NULL,
  name varchar(32)  NOT NULL,
  PRIMARY KEY (identifier,name),
  FOREIGN KEY (name) REFERENCES "sitools"."GROUPS" (name),
  FOREIGN KEY (identifier) REFERENCES "sitools"."USERS" (identifier)
);

CREATE TABLE IF NOT EXISTS "sitools"."USER_PROPERTIES" (
  key varchar(64)  NOT NULL,
  value varchar(256)  DEFAULT NULL,
  identifier varchar(64)  DEFAULT NULL,
  scope varchar(8)  DEFAULT NULL,
  PRIMARY KEY (key)
);