DROP TABLE IF EXISTS "sitools"."USER_GROUP";
DROP TABLE IF EXISTS "sitools"."USERS";
DROP TABLE IF EXISTS "sitools"."GROUPS";
DROP TABLE IF EXISTS "sitools"."USER_PROPERTIES";

DROP SCHEMA IF EXISTS "sitools";
CREATE SCHEMA "sitools";

DROP USER sitools;

CREATE USER sitools PASSWORD sitools ADMIN;
ALTER USER sitools SET INITIAL SCHEMA "sitools";

CREATE TABLE IF NOT EXISTS "sitools"."USERS" (
  identifier varchar(64)  NOT NULL,
  firstname varchar(32)  DEFAULT NULL,
  lastname varchar(32)  DEFAULT NULL,
  email varchar(128)  DEFAULT NULL,
  secret varchar(40)  DEFAULT NULL,
  PRIMARY KEY (identifier)
);

INSERT INTO "sitools"."USERS" (identifier, firstname, lastname, email, secret) VALUES
('admin', 'firstname', 'lastname', 'admin-sitools@cnes.fr', 'admin');

CREATE TABLE IF NOT EXISTS "sitools"."GROUPS" (
  name varchar(32) NOT NULL,
  description varchar(128)  DEFAULT NULL,
  PRIMARY KEY (name)
);

--
-- Contenu de la table 'GROUPS'
--

INSERT INTO "sitools"."GROUPS" (name, description) VALUES
('register', 'Group of registered persons'),
('administrator', 'Group of persons managing the archive system');


CREATE TABLE IF NOT EXISTS "sitools"."USER_GROUP" (
  identifier varchar(64)  NOT NULL,
  name varchar(32)  NOT NULL,
  PRIMARY KEY (identifier,name),
  FOREIGN KEY (name) REFERENCES "sitools"."GROUPS" (name),
  FOREIGN KEY (identifier) REFERENCES "sitools"."USERS" (identifier)
);

--
-- Contenu de la table 'USER_GROUP'
--

INSERT INTO "sitools"."USER_GROUP" (identifier, name) VALUES
('admin', 'administrator');

CREATE TABLE IF NOT EXISTS "sitools"."USER_PROPERTIES" (
  key varchar(64)  NOT NULL,
  value varchar(256)  DEFAULT NULL,
  identifier varchar(64)  DEFAULT NULL,
  scope varchar(8)  DEFAULT NULL,
  PRIMARY KEY (key)
);

DROP USER sa;
