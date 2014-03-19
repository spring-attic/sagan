CREATE TABLE post_publicslugaliases (
    post_id int NOT NULL,
    publicSlugAliases character varying(255) NOT NULL UNIQUE,
    primary key (post_id,publicSlugAliases)
);


INSERT INTO post_publicslugaliases
    (post_id, publicSlugAliases)
SELECT
    (select id from post where publicslug = '2014/02/19/spring-security-3-2-1-and-3-1-5-released'),
    '2014/02/19/spring-security-3-2-1-and-3-0-5-released'
WHERE
    EXISTS (
        (select id from post where publicslug = '2014/02/19/spring-security-3-2-1-and-3-1-5-released')
);

INSERT INTO post_publicslugaliases
    (post_id, publicSlugAliases)
SELECT
    (select id from post where publicslug = '2014/03/11/cve-2014-0097-fixed-in-spring-security-3-2-2-and-3-1-6'),
    '2014/03/11/cve-2014-009-fixed-in-spring-security-3-2-2-and-3-1-6'
WHERE
    EXISTS (
        (select id from post where publicslug = '2014/03/11/cve-2014-0097-fixed-in-spring-security-3-2-2-and-3-1-6')
);