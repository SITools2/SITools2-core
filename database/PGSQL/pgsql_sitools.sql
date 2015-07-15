--
-- PostgreSQL database dump
--

-- Started on 2011-04-13 18:14:28

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 8 (class 2615 OID 16572)
-- Name: sitools; Type: SCHEMA; Schema: -; Owner: sitools
--

CREATE SCHEMA sitools;


ALTER SCHEMA sitools OWNER TO sitools;

SET search_path = sitools, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1591 (class 1259 OID 16665)
-- Dependencies: 8
-- Name: GROUPS; Type: TABLE; Schema: sitools; Owner: sitools; Tablespace: 
--

CREATE TABLE "GROUPS" (
    name character varying(32) NOT NULL,
    description character varying(128)
);


ALTER TABLE sitools."GROUPS" OWNER TO sitools;

--
-- TOC entry 1592 (class 1259 OID 16677)
-- Dependencies: 8
-- Name: USERS; Type: TABLE; Schema: sitools; Owner: sitools; Tablespace: 
--

CREATE TABLE "USERS" (
    identifier character varying(64) NOT NULL,
    firstname character varying(32),
    lastname character varying(32),
    email character varying(128),
    secret character varying(40)
);


ALTER TABLE sitools."USERS" OWNER TO sitools;

--
-- TOC entry 1593 (class 1259 OID 16680)
-- Dependencies: 8
-- Name: USER_GROUP; Type: TABLE; Schema: sitools; Owner: sitools; Tablespace: 
--

CREATE TABLE "USER_GROUP" (
    identifier character varying(64) NOT NULL,
    name character varying(32) NOT NULL
);


ALTER TABLE sitools."USER_GROUP" OWNER TO sitools;

--
-- TOC entry 1594 (class 1259 OID 16683)
-- Dependencies: 8
-- Name: USER_PROPERTIES; Type: TABLE; Schema: sitools; Owner: sitools; Tablespace: 
--

CREATE TABLE "USER_PROPERTIES" (
    key character varying(64) NOT NULL,
    value character varying(256),
    identifier character varying(64),
    scope character varying(8)
);


ALTER TABLE sitools."USER_PROPERTIES" OWNER TO sitools;

--
-- TOC entry 1895 (class 0 OID 16665)
-- Dependencies: 1591
-- Data for Name: GROUPS; Type: TABLE DATA; Schema: sitools; Owner: sitools
--

INSERT INTO "GROUPS" (name, description) VALUES ('register', 'Group of registered persons');
INSERT INTO "GROUPS" (name, description) VALUES ('administrator', 'Group of persons managing the archive system');

--
-- TOC entry 1896 (class 0 OID 16677)
-- Dependencies: 1592
-- Data for Name: USERS; Type: TABLE DATA; Schema: sitools; Owner: sitools
--

INSERT INTO "USERS" (identifier, firstname, lastname, email, secret) VALUES ('admin', 'firstname', 'lastname', 'admin-sitools@cnes.fr', 'admin');

--
-- TOC entry 1897 (class 0 OID 16680)
-- Dependencies: 1593
-- Data for Name: USER_GROUP; Type: TABLE DATA; Schema: sitools; Owner: sitools
--

INSERT INTO "USER_GROUP" (identifier, name) VALUES ('admin', 'administrator');


--
-- TOC entry 1898 (class 0 OID 16683)
-- Dependencies: 1594
-- Data for Name: USER_PROPERTIES; Type: TABLE DATA; Schema: sitools; Owner: sitools
--



--
-- TOC entry 1887 (class 2606 OID 16714)
-- Dependencies: 1591 1591
-- Name: PK_GROUP; Type: CONSTRAINT; Schema: sitools; Owner: sitools; Tablespace: 
--

ALTER TABLE ONLY "GROUPS"
    ADD CONSTRAINT "PK_GROUP" PRIMARY KEY (name);


--
-- TOC entry 1889 (class 2606 OID 16718)
-- Dependencies: 1592 1592
-- Name: PK_USER; Type: CONSTRAINT; Schema: sitools; Owner: sitools; Tablespace: 
--

ALTER TABLE ONLY "USERS"
    ADD CONSTRAINT "PK_USER" PRIMARY KEY (identifier);


--
-- TOC entry 1891 (class 2606 OID 16720)
-- Dependencies: 1593 1593 1593
-- Name: USER_GROUP_pkey; Type: CONSTRAINT; Schema: sitools; Owner: sitools; Tablespace: 
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "USER_GROUP_pkey" PRIMARY KEY (identifier, name);


--
-- TOC entry 1892 (class 1259 OID 16725)
-- Dependencies: 1594 1594
-- Name: IDX_PROPERTIES; Type: INDEX; Schema: sitools; Owner: sitools; Tablespace: 
--

CREATE INDEX "IDX_PROPERTIES" ON "USER_PROPERTIES" USING btree (identifier, key);


--
-- TOC entry 1893 (class 2606 OID 16726)
-- Dependencies: 1591 1886 1593
-- Name: FK_GROUP; Type: FK CONSTRAINT; Schema: sitools; Owner: sitools
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "FK_GROUP" FOREIGN KEY (name) REFERENCES "GROUPS"(name) ON DELETE CASCADE;


--
-- TOC entry 1894 (class 2606 OID 16731)
-- Dependencies: 1593 1592 1888
-- Name: FK_USER; Type: FK CONSTRAINT; Schema: sitools; Owner: sitools
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "FK_USER" FOREIGN KEY (identifier) REFERENCES "USERS"(identifier) ON DELETE CASCADE;


--
-- TOC entry 1901 (class 0 OID 0)
-- Dependencies: 8
-- Name: sitools; Type: ACL; Schema: -; Owner: sitools
--

REVOKE ALL ON SCHEMA sitools FROM PUBLIC;
REVOKE ALL ON SCHEMA sitools FROM sitools;
GRANT ALL ON SCHEMA sitools TO sitools;


--
-- TOC entry 1902 (class 0 OID 0)
-- Dependencies: 1591
-- Name: GROUPS; Type: ACL; Schema: sitools; Owner: sitools
--

REVOKE ALL ON TABLE "GROUPS" FROM PUBLIC;
REVOKE ALL ON TABLE "GROUPS" FROM sitools;
GRANT ALL ON TABLE "GROUPS" TO sitools;
GRANT ALL ON TABLE "GROUPS" TO PUBLIC;


--
-- TOC entry 1903 (class 0 OID 0)
-- Dependencies: 1592
-- Name: USERS; Type: ACL; Schema: sitools; Owner: sitools
--

REVOKE ALL ON TABLE "USERS" FROM PUBLIC;
REVOKE ALL ON TABLE "USERS" FROM sitools;
GRANT ALL ON TABLE "USERS" TO sitools;
GRANT ALL ON TABLE "USERS" TO PUBLIC;


--
-- TOC entry 1904 (class 0 OID 0)
-- Dependencies: 1593
-- Name: USER_GROUP; Type: ACL; Schema: sitools; Owner: sitools
--

REVOKE ALL ON TABLE "USER_GROUP" FROM PUBLIC;
REVOKE ALL ON TABLE "USER_GROUP" FROM sitools;
GRANT ALL ON TABLE "USER_GROUP" TO sitools;
GRANT ALL ON TABLE "USER_GROUP" TO PUBLIC;


--
-- TOC entry 1905 (class 0 OID 0)
-- Dependencies: 1594
-- Name: USER_PROPERTIES; Type: ACL; Schema: sitools; Owner: sitools
--

REVOKE ALL ON TABLE "USER_PROPERTIES" FROM PUBLIC;
REVOKE ALL ON TABLE "USER_PROPERTIES" FROM sitools;
GRANT ALL ON TABLE "USER_PROPERTIES" TO sitools;
GRANT ALL ON TABLE "USER_PROPERTIES" TO PUBLIC;


-- Completed on 2011-04-13 18:14:28

--
-- PostgreSQL database dump complete
--

