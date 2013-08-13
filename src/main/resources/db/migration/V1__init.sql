--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

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
    githubid bigint,
    githubusername character varying(255),
    gravataremail character varying(255),
    hidden boolean,
    lanyrdusername character varying(255),
    location character varying(255),
    name character varying(255),
    speakerdeckusername character varying(255),
    twitterusername character varying(255),
    username character varying(255) NOT NULL,
    videoembeds text
);


--
-- Name: post; Type: TABLE; Schema: public; Owner: pivotal; Tablespace: 
--

CREATE TABLE post (
    id bigint NOT NULL,
    broadcast boolean NOT NULL,
    category character varying(255) NOT NULL,
    createdat timestamp without time zone NOT NULL,
    draft boolean NOT NULL,
    publicslug character varying(255),
    publishat timestamp without time zone,
    rawcontent text NOT NULL,
    renderedcontent text NOT NULL,
    renderedsummary text NOT NULL,
    title character varying(255) NOT NULL,
    author_id bigint NOT NULL
);

-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: pivotal
--

SELECT pg_catalog.setval('hibernate_sequence', 4514, true);


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
-- Name: fk_6f0c59b9beeb43f6b00be2b1647; Type: FK CONSTRAINT; Schema: public; Owner: pivotal
--

ALTER TABLE ONLY post
    ADD CONSTRAINT fk_6f0c59b9beeb43f6b00be2b1647 FOREIGN KEY (author_id) REFERENCES memberprofile(id);


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

