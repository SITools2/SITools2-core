--
-- PostgreSQL database dump
--

-- Dumped from database version 8.4.7
-- Dumped by pg_dump version 9.0.1
-- Started on 2011-08-19 14:20:35

SET statement_timeout = 0;
-- SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 6 (class 2615 OID 16570)
-- Name: fuse; Type: SCHEMA; Schema: -; Owner: @db_user_pgsql@
--
DROP SCHEMA IF EXISTS fuse CASCADE;
CREATE SCHEMA fuse;


ALTER SCHEMA fuse OWNER TO @db_user_pgsql@;

SET search_path = fuse, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1601 (class 1259 OID 16619)
-- Dependencies: 1907 1908 1909 1910 1911 6
-- Name: fuse_prg_id; Type: TABLE; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE fuse_prg_id (
    prop_id character varying(4) DEFAULT ''::character varying NOT NULL,
    cycle integer,
    title character varying(200) DEFAULT NULL::character varying,
    fname character varying(20) DEFAULT NULL::character varying,
    lname character varying(30) DEFAULT NULL::character varying,
    institution character varying(100) DEFAULT NULL::character varying,
    abstract text
);


ALTER TABLE fuse.fuse_prg_id OWNER TO @db_user_pgsql@;

--
-- TOC entry 1602 (class 1259 OID 16630)
-- Dependencies: 1912 1913 1914 1915 1916 1917 1918 1919 1920 1921 1922 6
-- Name: headers; Type: TABLE; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE headers (
    dataset character varying(8) DEFAULT ''::character varying NOT NULL,
    targname character varying(30) DEFAULT NULL::character varying,
    ra_targ double precision,
    dec_targ double precision,
    dateobs timestamp without time zone,
    exptime double precision,
    aperture character varying(4) DEFAULT NULL::character varying,
    mode character varying(4) DEFAULT NULL::character varying,
    expos_nbr smallint,
    vmag double precision,
    sp_type character varying(4) DEFAULT NULL::character varying,
    ebv double precision,
    objclass smallint,
    src_type character varying(4) DEFAULT NULL::character varying,
    datearchiv timestamp without time zone,
    datepublic timestamp without time zone,
    ref smallint,
    z double precision,
    starttime timestamp without time zone,
    endtime timestamp without time zone,
    elat double precision,
    elong double precision,
    glat double precision,
    glong double precision,
    aper_pa double precision,
    high_proper_motion character(1) DEFAULT NULL::bpchar,
    moving_target character(1) DEFAULT NULL::bpchar,
    pr_inv_l character varying(15) DEFAULT NULL::character varying,
    pr_inv_f character varying(15) DEFAULT NULL::character varying,
    loadedatiap character(1) DEFAULT 'N'::bpchar,
    healpixid bigint,
    x_pos double precision NOT NULL,
    y_pos double precision NOT NULL,
    z_pos double precision NOT NULL
);


ALTER TABLE fuse.headers OWNER TO @db_user_pgsql@;

--
-- TOC entry 1603 (class 1259 OID 16644)
-- Dependencies: 1923 1924 1925 1926 1927 1928 1929 1930 1931 1932 6
-- Name: iapdatasets; Type: TABLE; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE iapdatasets (
    dataset character varying(8) DEFAULT ''::character varying NOT NULL,
    nevents integer DEFAULT 0 NOT NULL,
    timeobs time without time zone DEFAULT '00:00:00'::time without time zone NOT NULL,
    cf_vers character varying(5) DEFAULT NULL::character varying,
    slit character varying(5) DEFAULT NULL::character varying,
    obsstart double precision DEFAULT (0)::double precision NOT NULL,
    obsend double precision DEFAULT (0)::double precision NOT NULL,
    plantime double precision DEFAULT (0)::double precision NOT NULL,
    expnight double precision DEFAULT (0)::double precision NOT NULL,
    presence_all character(1) DEFAULT NULL::bpchar,
    dvd_id integer
);


ALTER TABLE fuse.iapdatasets OWNER TO @db_user_pgsql@;

--
-- TOC entry 1604 (class 1259 OID 16657)
-- Dependencies: 1933 1934 6
-- Name: object_class; Type: TABLE; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE object_class (
    obj_nbr integer DEFAULT 0 NOT NULL,
    object_name character varying(40) DEFAULT NULL::character varying
);

ALTER TABLE fuse.object_class OWNER TO @db_user_pgsql@;

--
-- TOC entry 1936 (class 2606 OID 18403)
-- Dependencies: 1601 1601
-- Name: fuse_prg_id_pkey; Type: CONSTRAINT; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY fuse_prg_id
    ADD CONSTRAINT fuse_prg_id_pkey PRIMARY KEY (prop_id);


--
-- TOC entry 1938 (class 2606 OID 16710)
-- Dependencies: 1602 1602
-- Name: headers_pkey; Type: CONSTRAINT; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY headers
    ADD CONSTRAINT headers_pkey PRIMARY KEY (dataset);


--
-- TOC entry 1940 (class 2606 OID 16712)
-- Dependencies: 1603 1603
-- Name: iapdatasets_pkey; Type: CONSTRAINT; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY iapdatasets
    ADD CONSTRAINT iapdatasets_pkey PRIMARY KEY (dataset);


--
-- TOC entry 1942 (class 2606 OID 18401)
-- Dependencies: 1604 1604
-- Name: object_class_pkey; Type: CONSTRAINT; Schema: fuse; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY object_class
    ADD CONSTRAINT object_class_pkey PRIMARY KEY (obj_nbr);


--
-- TOC entry 1945 (class 0 OID 0)
-- Dependencies: 6
-- Name: fuse; Type: ACL; Schema: -; Owner: @db_user_pgsql@
--

REVOKE ALL ON SCHEMA fuse FROM PUBLIC;
REVOKE ALL ON SCHEMA fuse FROM @db_user_pgsql@;
GRANT ALL ON SCHEMA fuse TO @db_user_pgsql@;


--
-- TOC entry 1946 (class 0 OID 0)
-- Dependencies: 1601
-- Name: fuse_prg_id; Type: ACL; Schema: fuse; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE fuse_prg_id FROM PUBLIC;
REVOKE ALL ON TABLE fuse_prg_id FROM @db_user_pgsql@;
GRANT ALL ON TABLE fuse_prg_id TO @db_user_pgsql@;


--
-- TOC entry 1947 (class 0 OID 0)
-- Dependencies: 1602
-- Name: headers; Type: ACL; Schema: fuse; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE headers FROM PUBLIC;
REVOKE ALL ON TABLE headers FROM @db_user_pgsql@;
GRANT ALL ON TABLE headers TO @db_user_pgsql@;


--
-- TOC entry 1948 (class 0 OID 0)
-- Dependencies: 1603
-- Name: iapdatasets; Type: ACL; Schema: fuse; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE iapdatasets FROM PUBLIC;
REVOKE ALL ON TABLE iapdatasets FROM @db_user_pgsql@;
GRANT ALL ON TABLE iapdatasets TO @db_user_pgsql@;


--
-- TOC entry 1949 (class 0 OID 0)
-- Dependencies: 1604
-- Name: object_class; Type: ACL; Schema: fuse; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE object_class FROM PUBLIC;
REVOKE ALL ON TABLE object_class FROM @db_user_pgsql@;
GRANT ALL ON TABLE object_class TO @db_user_pgsql@;


CREATE OR REPLACE VIEW fuse.view_headers_bis AS 
 SELECT headers.dataset, headers.targname, headers.ra_targ, headers.dec_targ, headers.dateobs, headers.exptime, headers.aperture, headers.mode, headers.expos_nbr, headers.vmag, headers.sp_type, headers.ebv, headers.objclass, headers.src_type, headers.datearchiv, headers.datepublic, headers.ref, headers.z, headers.starttime, headers.endtime, headers.elat, headers.elong, headers.glat, headers.glong, headers.aper_pa, headers.high_proper_motion, headers.moving_target, headers.pr_inv_l, headers.pr_inv_f, headers.loadedatiap, headers.healpixid, headers.x_pos, headers.y_pos, headers.z_pos
   FROM fuse.headers
  WHERE headers.dataset::text ~~ 'A%'::text;
  
ALTER TABLE fuse.view_headers_bis OWNER TO @db_user_pgsql@;;
REVOKE ALL ON TABLE fuse.view_headers_bis FROM PUBLIC;
GRANT ALL ON TABLE fuse.view_headers_bis TO @db_user_pgsql@;

CREATE OR REPLACE VIEW fuse.view_headers AS 
 SELECT headers.dataset, headers.targname, headers.ra_targ, headers.dec_targ, headers.dateobs, headers.exptime, headers.aperture, headers.mode, headers.expos_nbr, headers.vmag, headers.sp_type, headers.ebv, headers.objclass, headers.src_type, headers.datearchiv, headers.datepublic, headers.ref, headers.z, headers.starttime, headers.endtime, headers.elat, headers.elong, headers.glat, headers.glong, headers.aper_pa, headers.high_proper_motion, headers.moving_target, headers.pr_inv_l, headers.pr_inv_f, headers.loadedatiap, headers.healpixid, headers.x_pos, headers.y_pos, headers.z_pos
   FROM fuse.headers
  WHERE headers.dataset::text ~~ 'A%'::text;
  
ALTER TABLE fuse.view_headers_bis OWNER TO @db_user_pgsql@;;
REVOKE ALL ON TABLE fuse.view_headers_bis FROM PUBLIC;
GRANT ALL ON TABLE fuse.view_headers_bis TO @db_user_pgsql@;

-- Completed on 2011-08-19 14:20:35

--
-- PostgreSQL database dump complete
--




