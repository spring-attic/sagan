alter table project add stackoverflowtags character varying(255);

update project set stackoverflowtags = 'spring,spring-core,spring-framework,dependency-injection' where id = 'spring-framework';
update project set stackoverflowtags = 'spring-security' where id = 'spring-security';
update project set stackoverflowtags = 'spring-data,spring-data-mongodb,spring-data-neo4j' where id = 'spring-data';
update project set stackoverflowtags = 'spring-social,spring-social-facebook' where id = 'spring-social';
update project set stackoverflowtags = 'spring-batch,jsr-352' where id = 'spring-batch';
