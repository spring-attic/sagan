CREATE TABLE memberprofile (
    id serial NOT NULL PRIMARY KEY,
    avatarurl character varying(255),
    bio text,
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
    videoembeds text,
    jobtitle character varying(255)
);

CREATE TABLE post (
    id serial NOT NULL PRIMARY KEY,
    broadcast boolean NOT NULL,
    category character varying(255) NOT NULL,
    createdat timestamp NOT NULL,
    draft boolean NOT NULL,
    publicslug character varying(255) UNIQUE,
    publishat timestamp,
    rawcontent text NOT NULL,
    renderedcontent text NOT NULL,
    renderedsummary text NOT NULL,
    title character varying(255) NOT NULL,
    author_id integer NOT NULL REFERENCES memberprofile(id)
);

CREATE INDEX idx_category ON post(category);
CREATE INDEX idx_draft ON post(draft);
CREATE INDEX idx_publishat ON post(publishat);
