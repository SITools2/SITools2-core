--
-- PostgreSQL database dump
--

-- Started on 2011-04-13 18:14:28

SET statement_timeout = 0;
--SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 8 (class 2615 OID 16572)
-- Name: sitools; Type: SCHEMA; Schema: -; Owner: @db_user_pgsql@
--
DROP SCHEMA IF EXISTS sitools CASCADE;
CREATE SCHEMA sitools;


ALTER SCHEMA sitools OWNER TO @db_user_pgsql@;

SET search_path = sitools, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1591 (class 1259 OID 16665)
-- Dependencies: 8
-- Name: GROUPS; Type: TABLE; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE "GROUPS" (
    name character varying(32) NOT NULL,
    description character varying(128)
);


ALTER TABLE sitools."GROUPS" OWNER TO @db_user_pgsql@;

--
-- TOC entry 1592 (class 1259 OID 16677)
-- Dependencies: 8
-- Name: USERS; Type: TABLE; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE "USERS" (
    identifier character varying(64) NOT NULL,
    firstname character varying(32),
    lastname character varying(32),
    email character varying(128),
    secret character varying(40)
);


ALTER TABLE sitools."USERS" OWNER TO @db_user_pgsql@;

--
-- TOC entry 1593 (class 1259 OID 16680)
-- Dependencies: 8
-- Name: USER_GROUP; Type: TABLE; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE "USER_GROUP" (
    identifier character varying(64) NOT NULL,
    name character varying(32) NOT NULL
);


ALTER TABLE sitools."USER_GROUP" OWNER TO @db_user_pgsql@;

--
-- TOC entry 1594 (class 1259 OID 16683)
-- Dependencies: 8
-- Name: USER_PROPERTIES; Type: TABLE; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE "USER_PROPERTIES" (
    key character varying(64) NOT NULL,
    value character varying(256),
    identifier character varying(64),
    scope character varying(8)
);


ALTER TABLE sitools."USER_PROPERTIES" OWNER TO @db_user_pgsql@;

--
-- TOC entry 1895 (class 0 OID 16665)
-- Dependencies: 1591
-- Data for Name: GROUPS; Type: TABLE DATA; Schema: sitools; Owner: @db_user_pgsql@
--

INSERT INTO "GROUPS" VALUES ('astronomes', 'Utilisateurs intéressés par les observations spatiales');
INSERT INTO "GROUPS" VALUES ('utilisateurs', 'Tous les utilisateurs');
INSERT INTO "GROUPS" VALUES ('administrateurs', 'Tous les administrateurs');
INSERT INTO "GROUPS" VALUES ('geographes', 'Utilisateurs intéressés par les observations terrestres');
INSERT INTO "GROUPS" VALUES ('testgroup', 'groupe de test');
--
-- TOC entry 1896 (class 0 OID 16677)
-- Dependencies: 1592
-- Data for Name: USERS; Type: TABLE DATA; Schema: sitools; Owner: @db_user_pgsql@
--

INSERT INTO "USERS" VALUES ('identifier', 'firstname', 'lastname', 'email', 'secret');
INSERT INTO "USERS" VALUES ('5', 'jc', 'jc', 'jc', 'jc');
INSERT INTO "USERS" VALUES ('4', 'Frédéric2', 'dufau', 'fred@akka.eu', '******');
INSERT INTO "USERS" VALUES ('1', 'jp', 'jp', 'jp@akka.eu', 'jpb');
INSERT INTO "USERS" VALUES ('u1', 'u1-fn', 'u1-ln', 'u1@akka.eu', 'u1');
INSERT INTO "USERS" VALUES ('u3', 'u3-fn', 'u3-ln', 'u3@akka.eu', 'u3');
INSERT INTO "USERS" VALUES ('u2', 'u2-fn', 'u2-ln', 'u2@akka.eu', 'u2');
INSERT INTO "USERS" VALUES ('login', 'testFirstName', 'lastName', 'email', 'sitools');
INSERT INTO "USERS" VALUES ('2', 'jc', 'jc', 'email', 'sitools');
INSERT INTO "USERS" VALUES ('3', 'firstNameMod', 'lastName', 'email', 'sitools');
INSERT INTO "USERS" VALUES ('pmartin', 'Martin', 'Pierre', 'p.martin@website.fr', 'MD5://e4fcc2e5ab4c32b542df83587f08dbed');
INSERT INTO "USERS" VALUES ('admin', 'prénom', 'nom', 'admin@akka.eu', 'admin');
--
-- TOC entry 1897 (class 0 OID 16680)
-- Dependencies: 1593
-- Data for Name: USER_GROUP; Type: TABLE DATA; Schema: sitools; Owner: @db_user_pgsql@
--

INSERT INTO "USER_GROUP" VALUES ('admin', 'administrateurs');
INSERT INTO "USER_GROUP" VALUES ('identifier', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('5', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('4', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('1', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('u1', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('u3', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('u2', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('login', 'testgroup');
INSERT INTO "USER_GROUP" VALUES ('2', 'testgroup');

--
-- TOC entry 1898 (class 0 OID 16683)
-- Dependencies: 1594
-- Data for Name: USER_PROPERTIES; Type: TABLE DATA; Schema: sitools; Owner: @db_user_pgsql@
--



--
-- TOC entry 1887 (class 2606 OID 16714)
-- Dependencies: 1591 1591
-- Name: PK_GROUP; Type: CONSTRAINT; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY "GROUPS"
    ADD CONSTRAINT "PK_GROUP" PRIMARY KEY (name);


--
-- TOC entry 1889 (class 2606 OID 16718)
-- Dependencies: 1592 1592
-- Name: PK_USER; Type: CONSTRAINT; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY "USERS"
    ADD CONSTRAINT "PK_USER" PRIMARY KEY (identifier);


--
-- TOC entry 1891 (class 2606 OID 16720)
-- Dependencies: 1593 1593 1593
-- Name: USER_GROUP_pkey; Type: CONSTRAINT; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "USER_GROUP_pkey" PRIMARY KEY (identifier, name);


--
-- TOC entry 1892 (class 1259 OID 16725)
-- Dependencies: 1594 1594
-- Name: IDX_PROPERTIES; Type: INDEX; Schema: sitools; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE INDEX "IDX_PROPERTIES" ON "USER_PROPERTIES" USING btree (identifier, key);


--
-- TOC entry 1893 (class 2606 OID 16726)
-- Dependencies: 1591 1886 1593
-- Name: FK_GROUP; Type: FK CONSTRAINT; Schema: sitools; Owner: @db_user_pgsql@
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "FK_GROUP" FOREIGN KEY (name) REFERENCES "GROUPS"(name) ON DELETE CASCADE;


--
-- TOC entry 1894 (class 2606 OID 16731)
-- Dependencies: 1593 1592 1888
-- Name: FK_USER; Type: FK CONSTRAINT; Schema: sitools; Owner: @db_user_pgsql@
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "FK_USER" FOREIGN KEY (identifier) REFERENCES "USERS"(identifier) ON DELETE CASCADE;


--
-- TOC entry 1901 (class 0 OID 0)
-- Dependencies: 8
-- Name: sitools; Type: ACL; Schema: -; Owner: @db_user_pgsql@
--

REVOKE ALL ON SCHEMA sitools FROM PUBLIC;
REVOKE ALL ON SCHEMA sitools FROM sitools;
GRANT ALL ON SCHEMA sitools TO @db_user_pgsql@;


--
-- TOC entry 1902 (class 0 OID 0)
-- Dependencies: 1591
-- Name: GROUPS; Type: ACL; Schema: sitools; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE "GROUPS" FROM PUBLIC;
REVOKE ALL ON TABLE "GROUPS" FROM sitools;
GRANT ALL ON TABLE "GROUPS" TO @db_user_pgsql@;
GRANT ALL ON TABLE "GROUPS" TO PUBLIC;


--
-- TOC entry 1903 (class 0 OID 0)
-- Dependencies: 1592
-- Name: USERS; Type: ACL; Schema: sitools; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE "USERS" FROM PUBLIC;
REVOKE ALL ON TABLE "USERS" FROM sitools;
GRANT ALL ON TABLE "USERS" TO @db_user_pgsql@;
GRANT ALL ON TABLE "USERS" TO PUBLIC;


--
-- TOC entry 1904 (class 0 OID 0)
-- Dependencies: 1593
-- Name: USER_GROUP; Type: ACL; Schema: sitools; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE "USER_GROUP" FROM PUBLIC;
REVOKE ALL ON TABLE "USER_GROUP" FROM sitools;
GRANT ALL ON TABLE "USER_GROUP" TO @db_user_pgsql@;
GRANT ALL ON TABLE "USER_GROUP" TO PUBLIC;


--
-- TOC entry 1905 (class 0 OID 0)
-- Dependencies: 1594
-- Name: USER_PROPERTIES; Type: ACL; Schema: sitools; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE "USER_PROPERTIES" FROM PUBLIC;
REVOKE ALL ON TABLE "USER_PROPERTIES" FROM sitools;
GRANT ALL ON TABLE "USER_PROPERTIES" TO @db_user_pgsql@;
GRANT ALL ON TABLE "USER_PROPERTIES" TO PUBLIC;


-- Completed on 2011-04-13 18:14:28

--
-- PostgreSQL database dump complete
--

