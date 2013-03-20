--
-- PostgreSQL database dump
--

-- Dumped from database version 8.4.7
-- Dumped by pg_dump version 9.0.1
-- Started on 2011-09-22 10:01:48

SET statement_timeout = 0;
--SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

DROP SCHEMA IF EXISTS test CASCADE;
CREATE SCHEMA test;
ALTER SCHEMA test OWNER TO @db_user_pgsql@;

SET search_path = test, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;



--
-- TOC entry 1604 (class 1259 OID 17766)
-- Dependencies: 12
-- Name: table_tests; Type: TABLE; Schema: test; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE table_tests (
    "int" integer,
    "float" real,
    double double precision,
    "varchar" character varying(200),
    varchar_id character varying(10) NOT NULL,
    text text,
    "timestamp" timestamp without time zone,
    timestamp_with_time_zone timestamp with time zone,
    date date,
    "time" time with time zone,
    "smallint" smallint,
    "char" character(1),
    bool boolean,
    "bigint" bigint,
    "numeric" numeric,
    char10 character(10),
    serial integer NOT NULL,
    bigserial bigint NOT NULL,
    time_without_time_zone time without time zone
);


ALTER TABLE test.table_tests OWNER TO @db_user_pgsql@;

--
-- TOC entry 1608 (class 1259 OID 17961)
-- Dependencies: 12 1604
-- Name: table_tests_bigserial_seq; Type: SEQUENCE; Schema: test; Owner: @db_user_pgsql@
--

CREATE SEQUENCE table_tests_bigserial_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test.table_tests_bigserial_seq OWNER TO @db_user_pgsql@;

--
-- TOC entry 1904 (class 0 OID 0)
-- Dependencies: 1608
-- Name: table_tests_bigserial_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: @db_user_pgsql@
--

ALTER SEQUENCE table_tests_bigserial_seq OWNED BY table_tests.bigserial;


--
-- TOC entry 1905 (class 0 OID 0)
-- Dependencies: 1608
-- Name: table_tests_bigserial_seq; Type: SEQUENCE SET; Schema: test; Owner: @db_user_pgsql@
--

SELECT pg_catalog.setval('table_tests_bigserial_seq', 5, true);


--
-- TOC entry 1607 (class 1259 OID 17951)
-- Dependencies: 1604 12
-- Name: table_tests_serial_seq; Type: SEQUENCE; Schema: test; Owner: @db_user_pgsql@
--

CREATE SEQUENCE table_tests_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test.table_tests_serial_seq OWNER TO @db_user_pgsql@;

--
-- TOC entry 1906 (class 0 OID 0)
-- Dependencies: 1607
-- Name: table_tests_serial_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: @db_user_pgsql@
--

ALTER SEQUENCE table_tests_serial_seq OWNED BY table_tests.serial;


--
-- TOC entry 1907 (class 0 OID 0)
-- Dependencies: 1607
-- Name: table_tests_serial_seq; Type: SEQUENCE SET; Schema: test; Owner: @db_user_pgsql@
--

SELECT pg_catalog.setval('table_tests_serial_seq', 5, true);

--
-- TOC entry 1891 (class 2604 OID 17953)
-- Dependencies: 1607 1604
-- Name: serial; Type: DEFAULT; Schema: test; Owner: @db_user_pgsql@
--

ALTER TABLE table_tests ALTER COLUMN serial SET DEFAULT nextval('table_tests_serial_seq'::regclass);


--
-- TOC entry 1892 (class 2604 OID 17963)
-- Dependencies: 1608 1604
-- Name: bigserial; Type: DEFAULT; Schema: test; Owner: @db_user_pgsql@
--

ALTER TABLE table_tests ALTER COLUMN bigserial SET DEFAULT nextval('table_tests_bigserial_seq'::regclass);

--
-- TOC entry 1897 (class 0 OID 17766)
-- Dependencies: 1604
-- Data for Name: table_tests; Type: TABLE DATA; Schema: test; Owner: @db_user_pgsql@
--

INSERT INTO table_tests VALUES (1, 1.316118, 5.9160387339626901, '1', '1', 'ceci est un enregistrement 1', '2011-04-21 14:01:13.048296', '2011-08-21 14:01:13.048297+02', '2011-04-27', '14:25:13.04697+02', 2, 'y', false, 22, 3.43866701866351, 'ceci est 5', 2, 2, '14:28:13.04697');
INSERT INTO table_tests VALUES (0, 1.1553268, 8.9609593900505402, '0', '0', 'ceci est un enregistrement 0', '2011-04-21 14:01:13.047791', '2011-04-21 14:01:13.047794+02', '2011-04-21', '14:01:13.04697+02', 1, 'y', true, 97, 0.675227390775783, 'ceci est 8', 1, 1, '14:01:13.04697');

--
-- TOC entry 1895 (class 2606 OID 17773)
-- Dependencies: 1604 1604
-- Name: varchar_id; Type: CONSTRAINT; Schema: test; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY table_tests
    ADD CONSTRAINT varchar_id PRIMARY KEY (varchar_id);


--
-- TOC entry 1908 (class 0 OID 0)
-- Dependencies: 1603
-- Name: test_date; Type: ACL; Schema: test; Owner: @db_user_pgsql@
--

REVOKE ALL ON TABLE table_tests FROM PUBLIC;
REVOKE ALL ON TABLE table_tests FROM @db_user_pgsql@;
GRANT ALL ON TABLE table_tests TO @db_user_pgsql@;
GRANT ALL ON TABLE table_tests TO PUBLIC;

--
-- TOC entry 1611 (class 1259 OID 17988)
-- Dependencies: 12
-- Name: table_tests_3m; Type: TABLE; Schema: test; Owner: @db_user_pgsql@; Tablespace: 
--

CREATE TABLE table_tests_3m (
    "int" integer,
    "float" real,
    double double precision,
    "varchar" character varying(200),
    varchar_id character varying(10) NOT NULL,
    text text,
    "timestamp" timestamp without time zone,
    timestamp_with_time_zone timestamp with time zone,
    date date,
    "time" time with time zone,
    "smallint" smallint,
    "char" character(1),
    bool boolean,
    "bigint" bigint,
    "numeric" numeric,
    char10 character(10),
    serial integer NOT NULL,
    bigserial bigint NOT NULL,
    time_without_time_zone time without time zone
);


ALTER TABLE test.table_tests_3m OWNER TO @db_user_pgsql@;

--
-- TOC entry 1610 (class 1259 OID 17986)
-- Dependencies: 12 1611
-- Name: table_tests_3m_bigserial_seq; Type: SEQUENCE; Schema: test; Owner: @db_user_pgsql@
--

CREATE SEQUENCE table_tests_3m_bigserial_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test.table_tests_3m_bigserial_seq OWNER TO @db_user_pgsql@;

--
-- TOC entry 1976 (class 0 OID 0)
-- Dependencies: 1610
-- Name: table_tests_3m_bigserial_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: @db_user_pgsql@
--

ALTER SEQUENCE table_tests_3m_bigserial_seq OWNED BY table_tests_3m.bigserial;


--
-- TOC entry 1609 (class 1259 OID 17984)
-- Dependencies: 1611 12
-- Name: table_tests_3m_serial_seq; Type: SEQUENCE; Schema: test; Owner: @db_user_pgsql@
--

CREATE SEQUENCE table_tests_3m_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test.table_tests_3m_serial_seq OWNER TO @db_user_pgsql@;

--
-- TOC entry 1977 (class 0 OID 0)
-- Dependencies: 1609
-- Name: table_tests_3m_serial_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: @db_user_pgsql@
--

ALTER SEQUENCE table_tests_3m_serial_seq OWNED BY table_tests_3m.serial;

--
-- TOC entry 1924 (class 2604 OID 17991)
-- Dependencies: 1611 1609 1611
-- Name: serial; Type: DEFAULT; Schema: test; Owner: @db_user_pgsql@
--

ALTER TABLE table_tests_3m ALTER COLUMN serial SET DEFAULT nextval('table_tests_3m_serial_seq'::regclass);


--
-- TOC entry 1925 (class 2604 OID 17992)
-- Dependencies: 1610 1611 1611
-- Name: bigserial; Type: DEFAULT; Schema: test; Owner: @db_user_pgsql@
--

ALTER TABLE table_tests_3m ALTER COLUMN bigserial SET DEFAULT nextval('table_tests_3m_bigserial_seq'::regclass);

--
-- TOC entry 1951 (class 2606 OID 17997)
-- Dependencies: 1611 1611
-- Name: varchar_id_3m; Type: CONSTRAINT; Schema: test; Owner: @db_user_pgsql@; Tablespace: 
--

ALTER TABLE ONLY table_tests_3m
    ADD CONSTRAINT varchar_id_3m PRIMARY KEY (varchar_id);

-- Completed on 2011-09-22 10:01:49

--
-- PostgreSQL database dump complete
--

