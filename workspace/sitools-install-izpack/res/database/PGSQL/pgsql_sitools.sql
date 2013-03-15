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
-- Name: @db_schema@; Type: SCHEMA; Schema: -; Owner: @db_user@
--

CREATE SCHEMA @db_schema@;


ALTER SCHEMA @db_schema@ OWNER TO @db_user@;

SET search_path = @db_schema@, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1591 (class 1259 OID 16665)
-- Dependencies: 8
-- Name: GROUPS; Type: TABLE; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

CREATE TABLE "GROUPS" (
    name character varying(32) NOT NULL,
    description character varying(128)
);


ALTER TABLE @db_schema@."GROUPS" OWNER TO @db_user@;

--
-- TOC entry 1592 (class 1259 OID 16677)
-- Dependencies: 8
-- Name: USERS; Type: TABLE; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

CREATE TABLE "USERS" (
    identifier character varying(64) NOT NULL,
    firstname character varying(32),
    lastname character varying(32),
    email character varying(128),
    secret character varying(40)
);


ALTER TABLE @db_schema@."USERS" OWNER TO @db_user@;

--
-- TOC entry 1593 (class 1259 OID 16680)
-- Dependencies: 8
-- Name: USER_GROUP; Type: TABLE; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

CREATE TABLE "USER_GROUP" (
    identifier character varying(64) NOT NULL,
    name character varying(32) NOT NULL
);


ALTER TABLE @db_schema@."USER_GROUP" OWNER TO @db_user@;

--
-- TOC entry 1594 (class 1259 OID 16683)
-- Dependencies: 8
-- Name: USER_PROPERTIES; Type: TABLE; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

CREATE TABLE "USER_PROPERTIES" (
    key character varying(64) NOT NULL,
    value character varying(256),
    identifier character varying(64),
    scope character varying(8)
);


ALTER TABLE @db_schema@."USER_PROPERTIES" OWNER TO @db_user@;

--
-- TOC entry 1895 (class 0 OID 16665)
-- Dependencies: 1591
-- Data for Name: GROUPS; Type: TABLE DATA; Schema: @db_schema@; Owner: @db_user@
--

INSERT INTO "GROUPS" (name, description) VALUES ('register', 'Group of registered persons');
INSERT INTO "GROUPS" (name, description) VALUES ('administrator', 'Group of persons managing the archive system');

--
-- TOC entry 1896 (class 0 OID 16677)
-- Dependencies: 1592
-- Data for Name: USERS; Type: TABLE DATA; Schema: @db_schema@; Owner: @db_user@
--

INSERT INTO "USERS" (identifier, firstname, lastname, email, secret) VALUES ('admin', 'firstname', 'lastname', 'admin-sitools@cnes.fr', 'admin');

--
-- TOC entry 1897 (class 0 OID 16680)
-- Dependencies: 1593
-- Data for Name: USER_GROUP; Type: TABLE DATA; Schema: @db_schema@; Owner: @db_user@
--

INSERT INTO "USER_GROUP" (identifier, name) VALUES ('admin', 'administrator');


--
-- TOC entry 1898 (class 0 OID 16683)
-- Dependencies: 1594
-- Data for Name: USER_PROPERTIES; Type: TABLE DATA; Schema: @db_schema@; Owner: @db_user@
--



--
-- TOC entry 1887 (class 2606 OID 16714)
-- Dependencies: 1591 1591
-- Name: PK_GROUP; Type: CONSTRAINT; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

ALTER TABLE ONLY "GROUPS"
    ADD CONSTRAINT "PK_GROUP" PRIMARY KEY (name);


--
-- TOC entry 1889 (class 2606 OID 16718)
-- Dependencies: 1592 1592
-- Name: PK_USER; Type: CONSTRAINT; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

ALTER TABLE ONLY "USERS"
    ADD CONSTRAINT "PK_USER" PRIMARY KEY (identifier);


--
-- TOC entry 1891 (class 2606 OID 16720)
-- Dependencies: 1593 1593 1593
-- Name: USER_GROUP_pkey; Type: CONSTRAINT; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "USER_GROUP_pkey" PRIMARY KEY (identifier, name);


--
-- TOC entry 1892 (class 1259 OID 16725)
-- Dependencies: 1594 1594
-- Name: IDX_PROPERTIES; Type: INDEX; Schema: @db_schema@; Owner: @db_user@; Tablespace: 
--

CREATE INDEX "IDX_PROPERTIES" ON "USER_PROPERTIES" USING btree (identifier, key);


--
-- TOC entry 1893 (class 2606 OID 16726)
-- Dependencies: 1591 1886 1593
-- Name: FK_GROUP; Type: FK CONSTRAINT; Schema: @db_schema@; Owner: @db_user@
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "FK_GROUP" FOREIGN KEY (name) REFERENCES "GROUPS"(name) ON DELETE CASCADE;


--
-- TOC entry 1894 (class 2606 OID 16731)
-- Dependencies: 1593 1592 1888
-- Name: FK_USER; Type: FK CONSTRAINT; Schema: @db_schema@; Owner: @db_user@
--

ALTER TABLE ONLY "USER_GROUP"
    ADD CONSTRAINT "FK_USER" FOREIGN KEY (identifier) REFERENCES "USERS"(identifier) ON DELETE CASCADE;


--
-- TOC entry 1901 (class 0 OID 0)
-- Dependencies: 8
-- Name: @db_schema@; Type: ACL; Schema: -; Owner: @db_user@
--

REVOKE ALL ON SCHEMA @db_schema@ FROM PUBLIC;
REVOKE ALL ON SCHEMA @db_schema@ FROM @db_user@;
GRANT ALL ON SCHEMA @db_schema@ TO @db_user@;


--
-- TOC entry 1902 (class 0 OID 0)
-- Dependencies: 1591
-- Name: GROUPS; Type: ACL; Schema: @db_schema@; Owner: @db_user@
--

REVOKE ALL ON TABLE "GROUPS" FROM PUBLIC;
REVOKE ALL ON TABLE "GROUPS" FROM @db_user@;
GRANT ALL ON TABLE "GROUPS" TO @db_user@;
GRANT ALL ON TABLE "GROUPS" TO PUBLIC;


--
-- TOC entry 1903 (class 0 OID 0)
-- Dependencies: 1592
-- Name: USERS; Type: ACL; Schema: @db_schema@; Owner: @db_user@
--

REVOKE ALL ON TABLE "USERS" FROM PUBLIC;
REVOKE ALL ON TABLE "USERS" FROM @db_user@;
GRANT ALL ON TABLE "USERS" TO @db_user@;
GRANT ALL ON TABLE "USERS" TO PUBLIC;


--
-- TOC entry 1904 (class 0 OID 0)
-- Dependencies: 1593
-- Name: USER_GROUP; Type: ACL; Schema: @db_schema@; Owner: @db_user@
--

REVOKE ALL ON TABLE "USER_GROUP" FROM PUBLIC;
REVOKE ALL ON TABLE "USER_GROUP" FROM @db_user@;
GRANT ALL ON TABLE "USER_GROUP" TO @db_user@;
GRANT ALL ON TABLE "USER_GROUP" TO PUBLIC;


--
-- TOC entry 1905 (class 0 OID 0)
-- Dependencies: 1594
-- Name: USER_PROPERTIES; Type: ACL; Schema: @db_schema@; Owner: @db_user@
--

REVOKE ALL ON TABLE "USER_PROPERTIES" FROM PUBLIC;
REVOKE ALL ON TABLE "USER_PROPERTIES" FROM @db_user@;
GRANT ALL ON TABLE "USER_PROPERTIES" TO @db_user@;
GRANT ALL ON TABLE "USER_PROPERTIES" TO PUBLIC;


-- Completed on 2011-04-13 18:14:28

--
-- PostgreSQL database dump complete
--

