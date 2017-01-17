--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
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


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: authorities; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE authorities (
    username character varying(50) NOT NULL,
    authority character varying(50) NOT NULL
);


ALTER TABLE authorities OWNER TO postgres;

--
-- Name: entities; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE entities (
    entity_id integer NOT NULL,
    type_of_entity integer,
    parent_id integer
);


ALTER TABLE entities OWNER TO postgres;

--
-- Name: entity_attributes; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE entity_attributes (
    type_of_entity_id integer NOT NULL,
    attribute_id integer NOT NULL
);


ALTER TABLE entity_attributes OWNER TO postgres;

--
-- Name: relationship; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE relationship (
    entity_id integer NOT NULL,
    entity_val integer NOT NULL,
    attribute_id integer NOT NULL
);


ALTER TABLE relationship OWNER TO postgres;

--
-- Name: serial; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE serial
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE serial OWNER TO postgres;

--
-- Name: types_attributes; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE types_attributes (
    attribute_id integer NOT NULL,
    name character varying(255),
    type_of_attribute integer
);


ALTER TABLE types_attributes OWNER TO postgres;

--
-- Name: types_of_entities; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE types_of_entities (
    type_of_entity_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE types_of_entities OWNER TO postgres;

--
-- Name: userconnection; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE userconnection (
    userid character varying(255) NOT NULL,
    providerid character varying(255) NOT NULL,
    provideruserid character varying(255) NOT NULL,
    rank integer NOT NULL,
    displayname character varying(255),
    profileurl character varying(512),
    imageurl character varying(512),
    accesstoken character varying(1024) NOT NULL,
    secret character varying(255),
    refreshtoken character varying(255),
    expiretime bigint
);


ALTER TABLE userconnection OWNER TO postgres;

--
-- Name: userprofile; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE userprofile (
    userid character varying(255) NOT NULL,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    city character varying(255),
    name character varying(255),
    username character varying(255),
    userentityid integer
);


ALTER TABLE userprofile OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE users (
    username character varying(50) NOT NULL,
    password character varying(50) NOT NULL,
    enabled boolean NOT NULL
);


ALTER TABLE users OWNER TO postgres;

--
-- Name: values; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE "values" (
    attribute_id integer NOT NULL,
    entity_id integer NOT NULL,
    date_value timestamp without time zone,
    int_value integer,
    text_value character varying(255)
);


ALTER TABLE "values" OWNER TO postgres;

--
-- Data for Name: authorities; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: entities; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO entities VALUES (136, 1, NULL);
INSERT INTO entities VALUES (135, 5, 136);
INSERT INTO entities VALUES (138, 2, 136);
INSERT INTO entities VALUES (134, 2, 136);
INSERT INTO entities VALUES (137, 5, 138);
INSERT INTO entities VALUES (133, 5, 134);
INSERT INTO entities VALUES (140, 3, NULL);
INSERT INTO entities VALUES (139, 5, 140);
INSERT INTO entities VALUES (142, 3, NULL);
INSERT INTO entities VALUES (141, 5, 142);
INSERT INTO entities VALUES (144, 1, NULL);
INSERT INTO entities VALUES (143, 5, 144);
INSERT INTO entities VALUES (146, 1, NULL);
INSERT INTO entities VALUES (145, 5, 146);


--
-- Data for Name: entity_attributes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO entity_attributes VALUES (1, 1);
INSERT INTO entity_attributes VALUES (1, 2);
INSERT INTO entity_attributes VALUES (1, 31);
INSERT INTO entity_attributes VALUES (1, 32);
INSERT INTO entity_attributes VALUES (1, 33);
INSERT INTO entity_attributes VALUES (2, 1);
INSERT INTO entity_attributes VALUES (2, 3);
INSERT INTO entity_attributes VALUES (2, 4);
INSERT INTO entity_attributes VALUES (2, 7);
INSERT INTO entity_attributes VALUES (3, 1);
INSERT INTO entity_attributes VALUES (3, 6);
INSERT INTO entity_attributes VALUES (3, 3);
INSERT INTO entity_attributes VALUES (3, 4);
INSERT INTO entity_attributes VALUES (3, 32);
INSERT INTO entity_attributes VALUES (4, 3);
INSERT INTO entity_attributes VALUES (4, 8);
INSERT INTO entity_attributes VALUES (5, 5);


--
-- Data for Name: relationship; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO relationship VALUES (133, 133, -1);
INSERT INTO relationship VALUES (133, 133, -3);
INSERT INTO relationship VALUES (134, 134, -1);
INSERT INTO relationship VALUES (134, 134, -3);
INSERT INTO relationship VALUES (135, 135, -1);
INSERT INTO relationship VALUES (135, 135, -3);
INSERT INTO relationship VALUES (136, 136, -1);
INSERT INTO relationship VALUES (136, 136, -3);
INSERT INTO relationship VALUES (137, 137, -1);
INSERT INTO relationship VALUES (137, 137, -3);
INSERT INTO relationship VALUES (138, 138, -1);
INSERT INTO relationship VALUES (138, 138, -3);
INSERT INTO relationship VALUES (139, 139, -1);
INSERT INTO relationship VALUES (139, 139, -3);
INSERT INTO relationship VALUES (140, 140, -1);
INSERT INTO relationship VALUES (136, 140, 32);
INSERT INTO relationship VALUES (140, 140, -3);
INSERT INTO relationship VALUES (141, 141, -1);
INSERT INTO relationship VALUES (141, 141, -3);
INSERT INTO relationship VALUES (142, 142, -1);
INSERT INTO relationship VALUES (136, 142, 32);
INSERT INTO relationship VALUES (142, 142, -3);
INSERT INTO relationship VALUES (143, 143, -1);
INSERT INTO relationship VALUES (143, 143, -3);
INSERT INTO relationship VALUES (144, 144, -1);
INSERT INTO relationship VALUES (144, 144, -3);
INSERT INTO relationship VALUES (145, 145, -1);
INSERT INTO relationship VALUES (145, 145, -3);
INSERT INTO relationship VALUES (146, 146, -1);
INSERT INTO relationship VALUES (146, 146, -3);


--
-- Name: serial; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('serial', 146, true);


--
-- Data for Name: types_attributes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO types_attributes VALUES (1, 'name', 1);
INSERT INTO types_attributes VALUES (2, 'surname', 1);
INSERT INTO types_attributes VALUES (3, 'date', 1);
INSERT INTO types_attributes VALUES (4, 'description', 1);
INSERT INTO types_attributes VALUES (5, 'imageref', 1);
INSERT INTO types_attributes VALUES (6, 'chalStatus', 1);
INSERT INTO types_attributes VALUES (7, 'chalDefStatus', 1);
INSERT INTO types_attributes VALUES (8, 'message', 1);
INSERT INTO types_attributes VALUES (31, 'friends', 4);
INSERT INTO types_attributes VALUES (32, 'acceptedChalIns', 4);
INSERT INTO types_attributes VALUES (33, 'autorComment', 4);


--
-- Data for Name: types_of_entities; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO types_of_entities VALUES (1, 'User');
INSERT INTO types_of_entities VALUES (2, 'ChallengeDefinition');
INSERT INTO types_of_entities VALUES (3, 'ChallengeInstance');
INSERT INTO types_of_entities VALUES (4, 'Comment');
INSERT INTO types_of_entities VALUES (5, 'Image');


--
-- Data for Name: userconnection; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: userprofile; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: values; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "values" VALUES (1, 134, NULL, NULL, 'Make something');
INSERT INTO "values" VALUES (3, 134, NULL, NULL, 'Tue Jan 17 14:40:25 MSK 2017');
INSERT INTO "values" VALUES (4, 134, NULL, NULL, 'Hi, I''m first. Selected me!');
INSERT INTO "values" VALUES (7, 134, NULL, NULL, 'CREATED');
INSERT INTO "values" VALUES (5, 133, NULL, NULL, 'image133');
INSERT INTO "values" VALUES (5, 135, NULL, NULL, 'image135');
INSERT INTO "values" VALUES (1, 136, NULL, NULL, 'Evgeniy 1');
INSERT INTO "values" VALUES (2, 136, NULL, NULL, NULL);
INSERT INTO "values" VALUES (1, 138, NULL, NULL, 'Hi, make your''s task 4 Ivan.');
INSERT INTO "values" VALUES (3, 138, NULL, NULL, 'Tue Jan 17 14:40:27 MSK 2017');
INSERT INTO "values" VALUES (4, 138, NULL, NULL, 'After (may be)');
INSERT INTO "values" VALUES (7, 138, NULL, NULL, 'CREATED');
INSERT INTO "values" VALUES (5, 137, NULL, NULL, 'image137');
INSERT INTO "values" VALUES (5, 139, NULL, NULL, 'image139');
INSERT INTO "values" VALUES (1, 140, NULL, NULL, 'I can made it');
INSERT INTO "values" VALUES (3, 140, NULL, NULL, 'Tue Jan 17 14:40:30 MSK 2017');
INSERT INTO "values" VALUES (4, 140, NULL, NULL, 'After (may be)');
INSERT INTO "values" VALUES (6, 140, NULL, NULL, 'AWAITING');
INSERT INTO "values" VALUES (5, 141, NULL, NULL, 'image141');
INSERT INTO "values" VALUES (1, 142, NULL, NULL, 'Ou ');
INSERT INTO "values" VALUES (3, 142, NULL, NULL, 'Tue Jan 17 14:40:31 MSK 2017');
INSERT INTO "values" VALUES (4, 142, NULL, NULL, 'After (may be)');
INSERT INTO "values" VALUES (6, 142, NULL, NULL, 'AWAITING');
INSERT INTO "values" VALUES (5, 143, NULL, NULL, 'image143');
INSERT INTO "values" VALUES (1, 144, NULL, NULL, 'Jonnie Fast-Foot');
INSERT INTO "values" VALUES (2, 144, NULL, NULL, NULL);
INSERT INTO "values" VALUES (5, 145, NULL, NULL, 'image145');
INSERT INTO "values" VALUES (1, 146, NULL, NULL, 'Annet Fast-Food');
INSERT INTO "values" VALUES (2, 146, NULL, NULL, NULL);


--
-- Name: entities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY entities
    ADD CONSTRAINT entities_pkey PRIMARY KEY (entity_id);


--
-- Name: relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY relationship
    ADD CONSTRAINT relationship_pkey PRIMARY KEY (entity_id, entity_val, attribute_id);


--
-- Name: types_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY types_attributes
    ADD CONSTRAINT types_attributes_pkey PRIMARY KEY (attribute_id);


--
-- Name: types_of_entities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY types_of_entities
    ADD CONSTRAINT types_of_entities_pkey PRIMARY KEY (type_of_entity_id);


--
-- Name: uk_rs4q91b7jl33pkh10a1ybgpm3; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY entity_attributes
    ADD CONSTRAINT uk_rs4q91b7jl33pkh10a1ybgpm3 UNIQUE (type_of_entity_id, attribute_id);


--
-- Name: userconnection_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY userconnection
    ADD CONSTRAINT userconnection_pkey PRIMARY KEY (userid, providerid, provideruserid);


--
-- Name: userprofile_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY userprofile
    ADD CONSTRAINT userprofile_pkey PRIMARY KEY (userid);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);


--
-- Name: values_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY "values"
    ADD CONSTRAINT values_pkey PRIMARY KEY (attribute_id, entity_id);


--
-- Name: ix_auth_username; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE UNIQUE INDEX ix_auth_username ON authorities USING btree (username, authority);


--
-- Name: userconnectionrank; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE UNIQUE INDEX userconnectionrank ON userconnection USING btree (userid, providerid, rank);


--
-- Name: userprofilepk; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE UNIQUE INDEX userprofilepk ON userprofile USING btree (userid);


--
-- Name: fk_1c1yur5nqaq2qgamrk3qpaviq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entity_attributes
    ADD CONSTRAINT fk_1c1yur5nqaq2qgamrk3qpaviq FOREIGN KEY (attribute_id) REFERENCES types_attributes(attribute_id);


--
-- Name: fk_1mujrn3bp408pujeeg4skmwaw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY relationship
    ADD CONSTRAINT fk_1mujrn3bp408pujeeg4skmwaw FOREIGN KEY (entity_id) REFERENCES entities(entity_id);


--
-- Name: fk_2f7wt9plqgav1h2bu3vprtodb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entities
    ADD CONSTRAINT fk_2f7wt9plqgav1h2bu3vprtodb FOREIGN KEY (parent_id) REFERENCES entities(entity_id);


--
-- Name: fk_7ml7ux1mvkqa295fvh26epds1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY relationship
    ADD CONSTRAINT fk_7ml7ux1mvkqa295fvh26epds1 FOREIGN KEY (entity_val) REFERENCES entities(entity_id);


--
-- Name: fk_authorities_users; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY authorities
    ADD CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users(username);


--
-- Name: fk_kurr0r2uudu6xr7e607q8pedd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entity_attributes
    ADD CONSTRAINT fk_kurr0r2uudu6xr7e607q8pedd FOREIGN KEY (type_of_entity_id) REFERENCES types_of_entities(type_of_entity_id);


--
-- Name: fk_re5hfjwgt2xf596qgl4tvpdj8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "values"
    ADD CONSTRAINT fk_re5hfjwgt2xf596qgl4tvpdj8 FOREIGN KEY (entity_id) REFERENCES entities(entity_id) ON DELETE CASCADE;


--
-- Name: fk_userprofile_entities; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY userprofile
    ADD CONSTRAINT fk_userprofile_entities FOREIGN KEY (userentityid) REFERENCES entities(entity_id);


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

