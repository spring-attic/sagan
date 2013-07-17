--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: btree_gin; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS btree_gin WITH SCHEMA public;


--
-- Name: EXTENSION btree_gin; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION btree_gin IS 'support for indexing common datatypes in GIN';


--
-- Name: btree_gist; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS btree_gist WITH SCHEMA public;


--
-- Name: EXTENSION btree_gist; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION btree_gist IS 'support for indexing common datatypes in GiST';


--
-- Name: citext; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;


--
-- Name: EXTENSION citext; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION citext IS 'data type for case-insensitive character strings';


--
-- Name: cube; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS cube WITH SCHEMA public;


--
-- Name: EXTENSION cube; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION cube IS 'data type for multidimensional cubes';


--
-- Name: dblink; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS dblink WITH SCHEMA public;


--
-- Name: EXTENSION dblink; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION dblink IS 'connect to other PostgreSQL databases from within a database';


--
-- Name: dict_int; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS dict_int WITH SCHEMA public;


--
-- Name: EXTENSION dict_int; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION dict_int IS 'text search dictionary template for integers';


--
-- Name: dict_xsyn; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS dict_xsyn WITH SCHEMA public;


--
-- Name: EXTENSION dict_xsyn; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION dict_xsyn IS 'text search dictionary template for extended synonym processing';


--
-- Name: earthdistance; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS earthdistance WITH SCHEMA public;


--
-- Name: EXTENSION earthdistance; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION earthdistance IS 'calculate great-circle distances on the surface of the Earth';


--
-- Name: fuzzystrmatch; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA public;


--
-- Name: EXTENSION fuzzystrmatch; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION fuzzystrmatch IS 'determine similarities and distance between strings';


--
-- Name: hstore; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS hstore WITH SCHEMA public;


--
-- Name: EXTENSION hstore; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION hstore IS 'data type for storing sets of (key, value) pairs';


--
-- Name: intarray; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS intarray WITH SCHEMA public;


--
-- Name: EXTENSION intarray; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION intarray IS 'functions, operators, and index support for 1-D arrays of integers';


--
-- Name: ltree; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS ltree WITH SCHEMA public;


--
-- Name: EXTENSION ltree; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION ltree IS 'data type for hierarchical tree-like structures';


--
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: pgrowlocks; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pgrowlocks WITH SCHEMA public;


--
-- Name: EXTENSION pgrowlocks; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgrowlocks IS 'show row-level locking information';


--
-- Name: pgstattuple; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pgstattuple WITH SCHEMA public;


--
-- Name: EXTENSION pgstattuple; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgstattuple IS 'show tuple-level statistics';


--
-- Name: tablefunc; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS tablefunc WITH SCHEMA public;


--
-- Name: EXTENSION tablefunc; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION tablefunc IS 'functions that manipulate whole tables, including crosstab';


--
-- Name: unaccent; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS unaccent WITH SCHEMA public;


--
-- Name: EXTENSION unaccent; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION unaccent IS 'text search dictionary that removes accents';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- Name: xml2; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS xml2 WITH SCHEMA public;


--
-- Name: EXTENSION xml2; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION xml2 IS 'XPath querying and XSLT';


SET search_path = public, pg_catalog;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: pivotal
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO pivotal;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: memberprofile; Type: TABLE; Schema: public; Owner: pivotal; Tablespace: 
--

CREATE TABLE memberprofile (
    id bigint NOT NULL,
    avatarurl character varying(255),
    bio character varying(255),
    latitude real,
    longitude real,
    githubusername character varying(255),
    lanyrdusername character varying(255),
    location character varying(255),
    memberid character varying(255) NOT NULL,
    name character varying(255),
    speakerdeckusername character varying(255),
    twitterusername character varying(255),
    videoembeds text
);


ALTER TABLE public.memberprofile OWNER TO pivotal;

--
-- Name: post; Type: TABLE; Schema: public; Owner: pivotal; Tablespace: 
--

CREATE TABLE post (
    id bigint NOT NULL,
    broadcast boolean NOT NULL,
    category character varying(255) NOT NULL,
    createdat timestamp without time zone NOT NULL,
    draft boolean NOT NULL,
    publishat timestamp without time zone,
    rawcontent text NOT NULL,
    renderedcontent text NOT NULL,
    renderedsummary text NOT NULL,
    title character varying(255) NOT NULL,
    author_id bigint NOT NULL
);


ALTER TABLE public.post OWNER TO pivotal;

--
-- Name: searchentry; Type: TABLE; Schema: public; Owner: pivotal; Tablespace: 
--

CREATE TABLE searchentry (
    id character varying(255) NOT NULL,
    path character varying(255),
    publishat timestamp without time zone,
    rawcontent character varying(255),
    summary character varying(255),
    title character varying(255)
);


ALTER TABLE public.searchentry OWNER TO pivotal;

--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: pivotal
--

SELECT pg_catalog.setval('hibernate_sequence', 17, true);


--
-- Data for Name: memberprofile; Type: TABLE DATA; Schema: public; Owner: pivotal
--

COPY memberprofile (id, avatarurl, bio, latitude, longitude, githubusername, lanyrdusername, location, memberid, name, speakerdeckusername, twitterusername, videoembeds) FROM stdin;
10	https://secure.gravatar.com/avatar/4265c262601883aabd7bd437511f5397	change	\N	\N	\N			nickstreet	Nick Street			
12	https://secure.gravatar.com/avatar/ae671230e3a1c2a0eefa7604990084f1	\N	\N	\N	dsyer	\N	\N	dsyer	Dave Syer	\N	\N	\N
1	\N	hey	56	0	\N			kseebaldt	Kurtis Seebaldt			
11	https://secure.gravatar.com/avatar/29490cd51d5a93b61cc946844f471589		0	0	\N			cbeams	Chris Beams			<strong>HTML</strong>
\.


--
-- Data for Name: post; Type: TABLE DATA; Schema: public; Owner: pivotal
--

COPY post (id, broadcast, category, createdat, draft, publishat, rawcontent, renderedcontent, renderedsummary, title, author_id) FROM stdin;
\.


--
-- Data for Name: searchentry; Type: TABLE DATA; Schema: public; Owner: pivotal
--

COPY searchentry (id, path, publishat, rawcontent, summary, title) FROM stdin;
\.


--
-- Name: memberprofile_pkey; Type: CONSTRAINT; Schema: public; Owner: pivotal; Tablespace: 
--

ALTER TABLE ONLY memberprofile
    ADD CONSTRAINT memberprofile_pkey PRIMARY KEY (id);


--
-- Name: post_pkey; Type: CONSTRAINT; Schema: public; Owner: pivotal; Tablespace: 
--

ALTER TABLE ONLY post
    ADD CONSTRAINT post_pkey PRIMARY KEY (id);


--
-- Name: searchentry_pkey; Type: CONSTRAINT; Schema: public; Owner: pivotal; Tablespace: 
--

ALTER TABLE ONLY searchentry
    ADD CONSTRAINT searchentry_pkey PRIMARY KEY (id);


--
-- Name: fk_85e505960d1a47b9a1093672e31; Type: FK CONSTRAINT; Schema: public; Owner: pivotal
--

ALTER TABLE ONLY post
    ADD CONSTRAINT fk_85e505960d1a47b9a1093672e31 FOREIGN KEY (author_id) REFERENCES memberprofile(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

