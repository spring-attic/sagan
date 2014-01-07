CREATE TABLE project (
    id character varying(255) NOT NULL PRIMARY KEY,
    name character varying(255),
    repoUrl character varying(255),
    category character varying(255),
    siteUrl character varying(255),
    isAggregator boolean
);

CREATE TABLE project_releaselist (
    project_id character varying(255) NOT NULL,
    repository_id character varying(255),
    apiDocUrl character varying(255),
    artifactId character varying(255),
    groupId character varying(255),
    isCurrent boolean,
    refDocUrl character varying(255),
    releaseStatus int,
    versionName character varying(255),
    primary key (project_id,versionName)
);

create TABLE projectrepository (
    id character varying(255) NOT NULL PRIMARY KEY,
    name character varying(255),
    url character varying(255),
    snapshotsEnabled boolean
);
insert into projectrepository values('spring-snapshots','Spring Snapshots','http://repo.spring.io/snapshot','TRUE');
insert into projectrepository values('spring-milestones','Spring Milestones','http://repo.spring.io/milestone','FALSE');
insert into project values('spring-boot','Spring Boot','http://github.com/spring-projects/spring-boot','incubator','http://projects.spring.io/spring-boot','FALSE');
insert into project values('spring-data-elasticsearch','Spring Data Elasticsearch','http://github.com/spring-projects/spring-data-elasticsearch','incubator','http://projects.spring.io/spring-data-elasticsearch','FALSE');
insert into project values('spring-data-envers','Spring Data Envers','http://github.com/spring-projects/spring-data-envers','incubator','http://projects.spring.io/spring-data-envers','FALSE');
insert into project values('spring-data-couchbase','Spring Data Couchbase','http://github.com/spring-projects/spring-data-couchbase','incubator','http://projects.spring.io/spring-data-couchbase','FALSE');
insert into project values('spring-integration-dsl-groovy','Spring Integration Groovy DSL','http://github.com/spring-projects/spring-integration-dsl-groovy','incubator','','FALSE');
insert into project values('spring-integration-dsl-scala','Spring Integration Scala DSL','http://github.com/spring-projects/spring-integration-dsl-scala','incubator','','FALSE');
insert into project values('spring-plugin','Spring Plugin','http://github.com/spring-projects/spring-plugin','incubator','http://projects.spring.io/spring-plugin','FALSE');
insert into project values('spring-scala','Spring Scala','http://github.com/spring-projects/spring-scala','incubator','','FALSE');
insert into project values('spring-security-kerberos','Spring Security Kerberos','http://github.com/spring-projects/spring-security-kerberos','incubator','http://docs.spring.io/spring-security/site/extensions/krb/','FALSE');
insert into project values('spring-security-saml','Spring Security SAML','http://github.com/spring-projects/spring-security-saml','incubator','http://docs.spring.io/spring-security/site/extensions/saml/','FALSE');
insert into project values('spring-social-github','Spring Social GitHub','http://github.com/spring-projects/spring-social-github','incubator','http://projects.spring.io/spring-social-github','FALSE');
insert into project values('spring-social-linkedin','Spring Social LinkedIn','http://github.com/spring-projects/spring-social-linkedin','incubator','http://projects.spring.io/spring-social-linkedin','FALSE');
insert into project values('spring-social-tripit','Spring Social TripIt','http://github.com/spring-projects/spring-social-tripit','incubator','http://projects.spring.io/spring-social-tripit','FALSE');
insert into project values('spring-xd','Spring XD','http://github.com/spring-projects/spring-xd','incubator','http://projects.spring.io/spring-xd','FALSE');
insert into project values('spring-amqp','Spring AMQP','http://github.com/spring-projects/spring-amqp','active','http://projects.spring.io/spring-amqp','FALSE');
insert into project values('spring-android','Spring for Android','http://github.com/spring-projects/spring-android','active','http://projects.spring.io/spring-android','FALSE');
insert into project values('spring-batch','Spring Batch','http://github.com/spring-projects/spring-batch','active','http://projects.spring.io/spring-batch','FALSE');
insert into project values('spring-data','Spring Data','http://github.com/spring-projects/spring-data','active','http://projects.spring.io/spring-data','TRUE');
insert into project values('spring-data-jpa','Spring Data JPA','http://github.com/spring-projects/spring-data-jpa','active','http://projects.spring.io/spring-data-jpa','FALSE');
insert into project values('spring-data-commons','Spring Data Commons','http://github.com/spring-projects/spring-data-commons','active','http://projects.spring.io/spring-data-commons','FALSE');
insert into project values('spring-data-jdbc-ext','Spring Data JDBC Extensions','http://github.com/spring-projects/spring-data-jdbc-ext','active','http://projects.spring.io/spring-data-jdbc-ext','FALSE');
insert into project values('spring-data-mongodb','Spring Data MongoDB','http://github.com/spring-projects/spring-data-mongodb','active','http://projects.spring.io/spring-data-mongodb','FALSE');
insert into project values('spring-data-neo4j','Spring Data Neo4J','http://github.com/spring-projects/spring-data-neo4j','active','http://projects.spring.io/spring-data-neo4j','FALSE');
insert into project values('spring-data-redis','Spring Data Redis','http://github.com/spring-projects/spring-data-redis','active','http://projects.spring.io/spring-data-redis','FALSE');
insert into project values('spring-data-rest','Spring Data REST','http://github.com/spring-projects/spring-data-rest','active','http://projects.spring.io/spring-data-rest','FALSE');
insert into project values('spring-data-solr','Spring Data Solr','http://github.com/spring-projects/spring-data-solr','active','http://projects.spring.io/spring-data-solr','FALSE');
insert into project values('spring-flex','Spring Flex','http://github.com/spring-projects/spring-flex','active','http://projects.spring.io/spring-flex','FALSE');
insert into project values('spring-framework','Spring Framework','http://github.com/spring-projects/spring-framework','active','http://projects.spring.io/spring-framework','FALSE');
insert into project values('spring-data-gemfire','Spring Data GemFire','http://github.com/spring-projects/spring-data-gemfire','active','http://projects.spring.io/spring-data-gemfire','FALSE');
insert into project values('spring-hadoop','Spring for Apache Hadoop','http://github.com/spring-projects/spring-hadoop','active','http://projects.spring.io/spring-hadoop','FALSE');
insert into project values('spring-hateoas','Spring HATEOAS','http://github.com/spring-projects/spring-hateoas','active','http://projects.spring.io/spring-hateoas','FALSE');
insert into project values('spring-integration','Spring Integration','http://github.com/spring-projects/spring-integration','active','http://projects.spring.io/spring-integration','FALSE');
insert into project values('spring-ldap','Spring LDAP','http://github.com/spring-projects/spring-ldap','active','http://projects.spring.io/spring-ldap','FALSE');
insert into project values('spring-mobile','Spring Mobile','http://github.com/spring-projects/spring-mobile','active','http://projects.spring.io/spring-mobile','FALSE');
insert into project values('spring-roo','Spring Roo','http://github.com/spring-projects/spring-roo','active','http://projects.spring.io/spring-roo','FALSE');
insert into project values('spring-security','Spring Security','http://github.com/spring-projects/spring-security','active','http://projects.spring.io/spring-security','FALSE');
insert into project values('spring-security-oauth','Spring Security OAuth','http://github.com/spring-projects/spring-security-oauth','active','http://projects.spring.io/spring-security-oauth','FALSE');
insert into project values('spring-shell','Spring Shell','http://github.com/spring-projects/spring-shell','active','http://projects.spring.io/spring-shell','FALSE');
insert into project values('spring-social','Spring Social','http://github.com/spring-projects/spring-social','active','http://projects.spring.io/spring-social','FALSE');
insert into project values('spring-social-facebook','Spring Social Facebook','http://github.com/spring-projects/spring-social-facebook','active','http://projects.spring.io/spring-social-facebook','FALSE');
insert into project values('spring-social-twitter','Spring Social Twitter','http://github.com/spring-projects/spring-social-twitter','active','http://projects.spring.io/spring-social-twitter','FALSE');
insert into project values('spring-webflow','Spring Web Flow','http://github.com/spring-projects/spring-webflow','active','http://projects.spring.io/spring-webflow','FALSE');
insert into project values('spring-ws','Spring Web Services','http://github.com/spring-projects/spring-ws','active','http://projects.spring.io/spring-ws','FALSE');
insert into project values('spring-data-graph','Spring Data Graph','http://github.com/spring-projects/spring-data-graph','attic','','FALSE');
insert into project values('spring-test-mvc','Spring Test MVC','http://github.com/spring-projects/spring-test-mvc','attic','','FALSE');
insert into project_releaselist values('spring-boot','spring-snapshots','http://docs.spring.io/spring-boot/docs/0.5.0.BUILD-SNAPSHOT/api/','spring-boot','org.springframework.boot','FALSE','http://projects.spring.io/spring-boot/docs/README.html','0','0.5.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-boot','spring-milestones','http://docs.spring.io/spring-boot/docs/0.5.0.M6/api/','spring-boot','org.springframework.boot','FALSE','http://projects.spring.io/spring-boot/docs/README.html','1','0.5.0.M6');
insert into project_releaselist values('spring-data-elasticsearch','spring-snapshots','','spring-data-elasticsearch','org.springframework.data','FALSE','http://github.com/spring-projects/spring-data-elasticsearch#readme','0','1.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-envers',null,'','spring-data-envers','org.springframework.data','TRUE','','2','0.1.0.RELEASE');
insert into project_releaselist values('spring-data-couchbase','spring-snapshots','http://docs.spring.io/spring-data/couchbase/docs/1.0.0.BUILD-SNAPSHOT/api/','spring-data-couchbase','org.springframework.data','FALSE','http://docs.spring.io/spring-data-couchbase/docs/1.0.0.BUILD-SNAPSHOT/reference/html','0','1.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-couchbase','spring-milestones','http://docs.spring.io/spring-data/couchbase/docs/1.0.0.M1/api/','spring-data-couchbase','org.springframework.data','FALSE','http://docs.spring.io/spring-data-couchbase/docs/1.0.0.M1/reference/html','1','1.0.0.M1');
insert into project_releaselist values('spring-integration-dsl-groovy','spring-milestones','','spring-integration-dsl-groovy','org.springframework.integration','FALSE','','1','1.0.0.M1');
insert into project_releaselist values('spring-integration-dsl-scala','spring-milestones','','spring-integration-dsl-scala','org.springframework.integration','FALSE','','1','1.0.0.M2');
insert into project_releaselist values('spring-plugin',null,'','spring-plugin','org.springframework.plugin','TRUE','http://github.com/spring-projects/spring-plugin/blob/master/README.markdown','2','0.8.0.RELEASE');
insert into project_releaselist values('spring-scala','spring-milestones','','spring-scala','org.springframework.scala','FALSE','','1','1.0.0.RC1');
insert into project_releaselist values('spring-security-kerberos','spring-snapshots','http://docs.spring.io/spring-security-kerberos/docs/1.0.0.CI-SNAPSHOT/api/','spring-security-kerberos','org.springframework.security','FALSE','http://docs.spring.io/spring-security-kerberos/docs/1.0.0.CI-SNAPSHOT/reference/html','0','1.0.0.CI-SNAPSHOT');
insert into project_releaselist values('spring-security-saml','spring-milestones','http://docs.spring.io/spring-security-saml/docs/1.0.x/api/','spring-security-saml','org.springframework.security','FALSE','http://docs.spring.io/spring-security-saml/docs/1.0.x/reference/html/','1','1.0.0.RC2');
insert into project_releaselist values('spring-social-github','spring-snapshots','http://docs.spring.io/spring-social-github/docs/1.0.0.BUILD-SNAPSHOT/api/','spring-social-github','org.springframework.social','FALSE','http://docs.spring.io/spring-social-github/docs/1.0.0.BUILD-SNAPSHOT/reference/html','0','1.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-social-github','spring-milestones','http://docs.spring.io/spring-social-github/docs/1.0.0.M3/api/','spring-social-github','org.springframework.social','FALSE','http://docs.spring.io/spring-social-github/docs/1.0.0.M3/reference/html','1','1.0.0.M3');
insert into project_releaselist values('spring-social-linkedin','spring-snapshots','http://docs.spring.io/spring-social-linkedin/docs/1.0.0.BUILD-SNAPSHOT/api/','spring-social-linkedin','org.springframework.social','FALSE','http://docs.spring.io/spring-social-linkedin/docs/1.0.0.BUILD-SNAPSHOT/reference/html','0','1.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-social-linkedin','spring-milestones','http://docs.spring.io/spring-social-linkedin/docs/1.0.0.RC3/api/','spring-social-linkedin','org.springframework.social','FALSE','http://docs.spring.io/spring-social-linkedin/docs/1.0.0.RC3/reference/html','1','1.0.0.RC3');
insert into project_releaselist values('spring-social-tripit','spring-snapshots','http://docs.spring.io/spring-social-tripit/docs/1.0.0.BUILD-SNAPSHOT/api/','spring-social-tripit','org.springframework.social','FALSE','http://docs.spring.io/spring-social-tripit/docs/1.0.0.BUILD-SNAPSHOT/reference/html','0','1.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-social-tripit','spring-milestones','http://docs.spring.io/spring-social-tripit/docs/1.0.0.M3/api/','spring-social-tripit','org.springframework.social','FALSE','http://docs.spring.io/spring-social-tripit/docs/1.0.0.M3/reference/html','1','1.0.0.M3');
insert into project_releaselist values('spring-xd','spring-snapshots','http://docs.spring.io/spring-xd/docs/1.0.0.BUILD-SNAPSHOT/api/','spring-xd','org.springframework.xd','FALSE','http://docs.spring.io/spring-xd/docs/1.0.0.BUILD-SNAPSHOT/reference/html','0','1.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-xd','spring-milestones','http://docs.spring.io/spring-xd/docs/1.0.0.M4/api/','spring-xd','org.springframework.xd','FALSE','http://docs.spring.io/spring-xd/docs/1.0.0.M4/reference/html','1','1.0.0.M4');
insert into project_releaselist values('spring-amqp','spring-snapshots','http://docs.spring.io/spring-amqp/docs/1.2.1.BUILD-SNAPSHOT/api/','spring-amqp','org.springframework.amqp','FALSE','http://docs.spring.io/spring-amqp/docs/1.2.1.BUILD-SNAPSHOT/reference/html','0','1.2.1.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-amqp',null,'http://docs.spring.io/spring-amqp/docs/1.2.0.RELEASE/api/','spring-amqp','org.springframework.amqp','TRUE','http://docs.spring.io/spring-amqp/docs/1.2.0.RELEASE/reference/html','2','1.2.0.RELEASE');
insert into project_releaselist values('spring-amqp',null,'http://docs.spring.io/spring-amqp/docs/1.1.4.RELEASE/api/','spring-amqp','org.springframework.amqp','FALSE','http://docs.spring.io/spring-amqp/docs/1.1.4.RELEASE/reference/html','2','1.1.4.RELEASE');
insert into project_releaselist values('spring-android','spring-snapshots','http://docs.spring.io/spring-android/docs/1.0.2.BUILD-SNAPSHOT/api/','spring-android','org.springframework.android','FALSE','http://docs.spring.io/spring-android/docs/1.0.2.BUILD-SNAPSHOT/reference/html','0','1.0.2.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-android',null,'http://docs.spring.io/spring-android/docs/1.0.1.RELEASE/api/','spring-android','org.springframework.android','TRUE','http://docs.spring.io/spring-android/docs/1.0.1.RELEASE/reference/html','2','1.0.1.RELEASE');
insert into project_releaselist values('spring-batch','spring-milestones','http://docs.spring.io/spring-batch/trunk/apidocs/index.html','spring-batch','org.springframework.batch','FALSE','http://docs.spring.io/spring-batch/trunk/reference/html/index.html','1','3.0.0.M2');
insert into project_releaselist values('spring-batch',null,'http://docs.spring.io/spring-batch/2.2.x/apidocs/index.html','spring-batch','org.springframework.batch','TRUE','http://docs.spring.io/spring-batch/2.2.x/reference/html/index.html','2','2.2.4.RELEASE');
insert into project_releaselist values('spring-batch',null,'http://docs.spring.io/spring-batch/2.1.x/apidocs/index.html','spring-batch','org.springframework.batch','FALSE','http://docs.spring.io/spring-batch/2.1.x/reference/html/index.html','2','2.1.9.RELEASE');
insert into project_releaselist values('spring-data-jpa','spring-snapshots','','spring-data-jpa','org.springframework.data','FALSE','','0','1.5.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-jpa','spring-milestones','http://docs.spring.io/spring-data/jpa/docs/1.5.0.M1/api/','spring-data-jpa','org.springframework.data','FALSE','http://docs.spring.io/spring-data/jpa/docs/1.5.0.M1/reference/html/','1','1.5.0.M1');
insert into project_releaselist values('spring-data-jpa','spring-snapshots','','spring-data-jpa','org.springframework.data','FALSE','','0','1.4.4.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-jpa',null,'http://docs.spring.io/spring-data/jpa/docs/1.4.3.RELEASE/api/','spring-data-jpa','org.springframework.data','TRUE','http://docs.spring.io/spring-data/jpa/docs/1.4.3.RELEASE/reference/html/','2','1.4.3.RELEASE');
insert into project_releaselist values('spring-data-jpa',null,'http://docs.spring.io/spring-data/jpa/docs/1.3.5.RELEASE/api/','spring-data-jpa','org.springframework.data','FALSE','http://docs.spring.io/spring-data/jpa/docs/1.3.5.RELEASE/reference/html/','2','1.3.5.RELEASE');
insert into project_releaselist values('spring-data-commons','spring-snapshots','','spring-data-commons','org.springframework.data','FALSE','','0','1.7.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-commons','spring-milestones','http://docs.spring.io/spring-data/data-commons/docs/1.7.0.M1/api/','spring-data-commons','org.springframework.data','FALSE','http://docs.spring.io/spring-data/data-commons/docs/1.7.0.M1/reference/html','1','1.7.0.M1');
insert into project_releaselist values('spring-data-commons','spring-snapshots','','spring-data-commons','org.springframework.data','FALSE','','0','1.6.4.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-commons',null,'http://docs.spring.io/spring-data/data-commons/docs/1.6.3.RELEASE/api/','spring-data-commons','org.springframework.data','TRUE','http://docs.spring.io/spring-data/data-commons/docs/1.6.3.RELEASE/reference/html','2','1.6.3.RELEASE');
insert into project_releaselist values('spring-data-commons',null,'http://docs.spring.io/spring-data/data-commons/docs/1.5.3.RELEASE/api/','spring-data-commons','org.springframework.data','FALSE','http://docs.spring.io/spring-data/data-commons/docs/1.5.3.RELEASE/reference/html','2','1.5.3.RELEASE');
insert into project_releaselist values('spring-data-jdbc-ext',null,'http://docs.spring.io/spring-data/jdbc/docs/1.0.0.RELEASE/api/','spring-data-oracle','org.springframework.data','TRUE','http://docs.spring.io/spring-data/jdbc/docs/1.0.0.RELEASE/reference/html','2','1.0.0.RELEASE');
insert into project_releaselist values('spring-data-mongodb','spring-snapshots','','spring-data-mongodb','org.springframework.data','FALSE','','0','1.4.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-mongodb','spring-milestones','http://docs.spring.io/spring-data/data-mongo/docs/1.4.0.M1/api/','spring-data-mongodb','org.springframework.data','FALSE','http://docs.spring.io/spring-data/data-mongo/docs/1.4.0.M1/reference/html','1','1.4.0.M1');
insert into project_releaselist values('spring-data-mongodb','spring-snapshots','','spring-data-mongodb','org.springframework.data','FALSE','','0','1.3.4.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-mongodb',null,'http://docs.spring.io/spring-data/data-mongo/docs/1.3.3.RELEASE/api/','spring-data-mongodb','org.springframework.data','TRUE','http://docs.spring.io/spring-data/data-mongo/docs/1.3.3.RELEASE/reference/html','2','1.3.3.RELEASE');
insert into project_releaselist values('spring-data-mongodb',null,'http://docs.spring.io/spring-data/data-mongo/docs/1.2.4.RELEASE/api/','spring-data-mongodb','org.springframework.data','FALSE','http://docs.spring.io/spring-data/data-mongo/docs/1.2.4.RELEASE/reference/html','2','1.2.4.RELEASE');
insert into project_releaselist values('spring-data-neo4j','spring-milestones','http://docs.spring.io/spring-data/data-neo4j/docs/3.0.0.M1/api/','spring-data-neo4j','org.springframework.data','FALSE','http://docs.spring.io/spring-data/data-neo4j/docs/3.0.0.M1/reference/html','1','3.0.0.M1');
insert into project_releaselist values('spring-data-neo4j','spring-snapshots','','spring-data-neo4j','org.springframework.data','FALSE','','0','2.4.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-neo4j','spring-snapshots','','spring-data-neo4j','org.springframework.data','FALSE','','0','2.3.4.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-neo4j',null,'http://docs.spring.io/spring-data/data-neo4j/docs/2.3.3.RELEASE/api/','spring-data-neo4j','org.springframework.data','TRUE','http://docs.spring.io/spring-data/data-neo4j/docs/2.3.3.RELEASE/reference/html','2','2.3.3.RELEASE');
insert into project_releaselist values('spring-data-neo4j',null,'http://docs.spring.io/spring-data/data-neo4j/docs/2.2.3.RELEASE/api/','spring-data-neo4j','org.springframework.data','FALSE','http://docs.spring.io/spring-data/data-neo4j/docs/2.2.3.RELEASE/reference/html','2','2.2.3.RELEASE');
insert into project_releaselist values('spring-data-redis','spring-snapshots','','spring-data-redis','org.springframework.data','FALSE','','0','1.1.1.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-redis',null,'http://docs.spring.io/spring-data/data-redis/docs/1.1.0.RELEASE/api/','spring-data-redis','org.springframework.data','TRUE','http://docs.spring.io/spring-data/data-redis/docs/1.1.0.RELEASE/reference/html','2','1.1.0.RELEASE');
insert into project_releaselist values('spring-data-redis','spring-snapshots','','spring-data-redis','org.springframework.data','FALSE','','0','1.0.7.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-redis',null,'http://docs.spring.io/spring-data/data-redis/docs/1.0.6.RELEASE/api/','spring-data-redis','org.springframework.data','FALSE','http://docs.spring.io/spring-data/data-redis/docs/1.0.6.RELEASE/reference/html','2','1.0.6.RELEASE');
insert into project_releaselist values('spring-data-rest','spring-snapshots','','spring-data-rest-webmvc','org.springframework.data','FALSE','','0','2.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-rest','spring-milestones','http://docs.spring.io/spring-data/rest/docs/2.0.0.M1/api/','spring-data-rest-webmvc','org.springframework.data','FALSE','http://docs.spring.io/spring-data/rest/docs/2.0.0.M1/reference/html','1','2.0.0.M1');
insert into project_releaselist values('spring-data-solr','spring-snapshots','','spring-data-solr','org.springframework.data','FALSE','','0','1.1.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-solr','spring-milestones','http://docs.spring.io/spring-data/solr/docs/1.1.0.M1/api/','spring-data-solr','org.springframework.data','FALSE','http://docs.spring.io/spring-data/solr/docs/1.1.0.M1/reference/html/','1','1.1.0.M1');
insert into project_releaselist values('spring-data-solr','spring-snapshots','','spring-data-solr','org.springframework.data','FALSE','','0','1.0.1.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-solr',null,'http://docs.spring.io/spring-data/solr/docs/1.0.0.RELEASE/api/','spring-data-solr','org.springframework.data','TRUE','http://docs.spring.io/spring-data/solr/docs/1.0.0.RELEASE/reference/html/','2','1.0.0.RELEASE');
insert into project_releaselist values('spring-flex',null,'http://docs.spring.io/spring-flex/docs/1.5.2.RELEASE/javadoc-api/','spring-flex','org.springframework.flex','TRUE','http://docs.spring.io/spring-flex/docs/1.5.2.RELEASE/reference/html','2','1.5.2.RELEASE');
insert into project_releaselist values('spring-framework','spring-snapshots','http://docs.spring.io/spring/docs/4.0.1.BUILD-SNAPSHOT/javadoc-api/','spring-framework','org.springframework','FALSE','http://docs.spring.io/spring/docs/4.0.1.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/','0','4.0.1.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-framework',null,'http://docs.spring.io/spring/docs/4.0.0.RELEASE/javadoc-api/','spring-framework','org.springframework','TRUE','http://docs.spring.io/spring/docs/4.0.0.RELEASE/spring-framework-reference/htmlsingle/','2','4.0.0.RELEASE');
insert into project_releaselist values('spring-framework','spring-snapshots','http://docs.spring.io/spring/docs/3.2.7.BUILD-SNAPSHOT/javadoc-api/','spring-framework','org.springframework','FALSE','http://docs.spring.io/spring/docs/3.2.7.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/','0','3.2.7.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-framework',null,'http://docs.spring.io/spring/docs/3.2.6.RELEASE/javadoc-api/','spring-framework','org.springframework','FALSE','http://docs.spring.io/spring/docs/3.2.6.RELEASE/spring-framework-reference/htmlsingle/','2','3.2.6.RELEASE');
insert into project_releaselist values('spring-framework',null,'http://docs.spring.io/spring/docs/3.1.4.RELEASE/javadoc-api/','spring-framework','org.springframework','FALSE','http://docs.spring.io/spring/docs/3.1.4.RELEASE/spring-framework-reference/htmlsingle/','2','3.1.4.RELEASE');
insert into project_releaselist values('spring-data-gemfire','spring-snapshots','http://docs.spring.io/spring-data-gemfire/docs/1.4.0.BUILD-SNAPSHOT/api/','spring-data-gemfire','org.springframework.data','FALSE','http://docs.spring.io/spring-data-gemfire/docs/1.4.0.BUILD-SNAPSHOT/reference/html','0','1.4.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-data-gemfire',null,'http://docs.spring.io/spring-data-gemfire/docs/1.3.3.RELEASE/api/','spring-data-gemfire','org.springframework.data','TRUE','http://docs.spring.io/spring-data-gemfire/docs/1.3.3.RELEASE/reference/html','2','1.3.3.RELEASE');
insert into project_releaselist values('spring-data-gemfire',null,'http://docs.spring.io/spring-data-gemfire/docs/1.2.2.RELEASE/api/','spring-data-gemfire','org.springframework.data','FALSE','http://docs.spring.io/spring-data-gemfire/docs/1.2.2.RELEASE/reference/html','2','1.2.2.RELEASE');
insert into project_releaselist values('spring-hadoop','spring-snapshots','http://docs.spring.io/spring-hadoop/docs/2.0.0.BUILD-SNAPSHOT/api/','spring-data-hadoop','org.springframework.data','FALSE','http://docs.spring.io/spring-hadoop/docs/2.0.0.BUILD-SNAPSHOT/reference/html','0','2.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-hadoop','spring-milestones','http://docs.spring.io/spring-hadoop/docs/2.0.0.M4/api/','spring-data-hadoop','org.springframework.data','FALSE','http://docs.spring.io/spring-hadoop/docs/2.0.0.M4/reference/html','1','2.0.0.M4');
insert into project_releaselist values('spring-hadoop',null,'http://docs.spring.io/spring-hadoop/docs/1.0.2.RELEASE/api/','spring-data-hadoop','org.springframework.data','TRUE','http://docs.spring.io/spring-hadoop/docs/1.0.2.RELEASE/reference/html','2','1.0.2.RELEASE');
insert into project_releaselist values('spring-hadoop',null,'http://docs.spring.io/spring-hadoop/docs/1.0.1.RELEASE/api/','spring-data-hadoop','org.springframework.data','FALSE','http://docs.spring.io/spring-hadoop/docs/1.0.1.RELEASE/reference/html','2','1.0.1.RELEASE');
insert into project_releaselist values('spring-hateoas',null,'','spring-hateoas','org.springframework.hateoas','TRUE','http://github.com/spring-projects/spring-hateoas/blob/master/readme.md','2','0.7.0.RELEASE');
insert into project_releaselist values('spring-integration','spring-snapshots','http://docs.spring.io/spring-integration/docs/4.0.0.BUILD-SNAPSHOT/api/','spring-integration','org.springframework.integration','FALSE','http://docs.spring.io/spring-integration/docs/4.0.0.BUILD-SNAPSHOT/reference/html','0','4.0.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-integration','spring-milestones','http://docs.spring.io/spring-integration/docs/4.0.0.M2/api/','spring-integration','org.springframework.integration','FALSE','http://docs.spring.io/spring-integration/docs/4.0.0.M2/reference/html','1','4.0.0.M2');
insert into project_releaselist values('spring-integration','spring-snapshots','http://docs.spring.io/spring-integration/docs/3.0.1.BUILD-SNAPSHOT/api/','spring-integration','org.springframework.integration','FALSE','http://docs.spring.io/spring-integration/docs/3.0.1.BUILD-SNAPSHOT/reference/html','0','3.0.1.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-integration',null,'http://docs.spring.io/spring-integration/docs/3.0.0.RELEASE/api/','spring-integration','org.springframework.integration','TRUE','http://docs.spring.io/spring-integration/docs/3.0.0.RELEASE/reference/html','2','3.0.0.RELEASE');
insert into project_releaselist values('spring-integration','spring-snapshots','http://docs.spring.io/spring-integration/docs/2.2.7.BUILD-SNAPSHOT/api/','spring-integration','org.springframework.integration','FALSE','http://docs.spring.io/spring-integration/docs/2.2.7.BUILD-SNAPSHOT/reference/html','0','2.2.7.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-integration',null,'http://docs.spring.io/spring-integration/docs/2.2.6.RELEASE/api/','spring-integration','org.springframework.integration','FALSE','http://docs.spring.io/spring-integration/docs/2.2.6.RELEASE/reference/html','2','2.2.6.RELEASE');
insert into project_releaselist values('spring-integration',null,'http://docs.spring.io/spring-integration/docs/2.1.6.RELEASE/api/','spring-integration','org.springframework.integration','FALSE','http://docs.spring.io/spring-integration/docs/2.1.6.RELEASE/reference/html','2','2.1.6.RELEASE');
insert into project_releaselist values('spring-ldap','spring-snapshots','http://docs.spring.io/spring-ldap/docs/2.0.0.CI-SNAPSHOT/apidocs/','spring-ldap','org.springframework.ldap','FALSE','http://docs.spring.io/spring-ldap/docs/2.0.0.CI-SNAPSHOT/reference/','0','2.0.0.CI-SNAPSHOT');
insert into project_releaselist values('spring-ldap','spring-milestones','http://docs.spring.io/spring-ldap/docs/2.0.0.M1/apidocs/','spring-ldap','org.springframework.ldap','FALSE','http://docs.spring.io/spring-ldap/docs/2.0.0.M1/reference/','1','2.0.0.M1');
insert into project_releaselist values('spring-ldap',null,'http://docs.spring.io/spring-ldap/docs/1.3.2.RELEASE/apidocs/','spring-ldap','org.springframework.ldap','TRUE','http://docs.spring.io/spring-ldap/docs/1.3.2.RELEASE/reference/html','2','1.3.2.RELEASE');
insert into project_releaselist values('spring-mobile','spring-snapshots','http://docs.spring.io/spring-mobile/docs/1.2.0.BUILD-SNAPSHOT/api/','spring-mobile-device','org.springframework.mobile','FALSE','http://docs.spring.io/spring-mobile/docs/1.2.0.BUILD-SNAPSHOT/reference/html','0','1.2.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-mobile',null,'http://docs.spring.io/spring-mobile/docs/1.1.0.RELEASE/api/','spring-mobile-device','org.springframework.mobile','TRUE','http://docs.spring.io/spring-mobile/docs/1.1.0.RELEASE/reference/html','2','1.1.0.RELEASE');
insert into project_releaselist values('spring-roo',null,'','spring-roo','org.springframework.roo','TRUE','http://docs.spring.io/spring-roo/reference/html/','2','1.2.4.RELEASE');
insert into project_releaselist values('spring-security','spring-snapshots','http://docs.spring.io/spring-security/site/docs/3.2.1.CI-SNAPSHOT/apidocs/','spring-security','org.springframework.security','FALSE','http://docs.spring.io/spring-security/site/docs/3.2.1.CI-SNAPSHOT/reference/htmlsingle/','0','3.2.1.CI-SNAPSHOT');
insert into project_releaselist values('spring-security',null,'http://docs.spring.io/spring-security/site/docs/3.2.0.RELEASE/apidocs/','spring-security','org.springframework.security','TRUE','http://docs.spring.io/spring-security/site/docs/3.2.0.RELEASE/reference/htmlsingle/','2','3.2.0.RELEASE');
insert into project_releaselist values('spring-security',null,'http://docs.spring.io/spring-security/site/docs/3.1.4.RELEASE/apidocs/','spring-security','org.springframework.security','FALSE','http://docs.spring.io/spring-security/site/docs/3.1.4.RELEASE/reference/springsecurity.html','2','3.1.4.RELEASE');
insert into project_releaselist values('spring-security-oauth',null,'http://docs.spring.io/spring-security/oauth/apidocs/index.html','spring-security-oauth','org.springframework.security.oauth','TRUE','http://projects.spring.io/spring-security-oauth/docs/Home.html','2','1.0.5.RELEASE');
insert into project_releaselist values('spring-shell','spring-milestones','http://docs.spring.io/spring-shell/docs/1.1.0.M1/api/','spring-shell','org.springframework.shell','FALSE','http://docs.spring.io/spring-shell/docs/1.1.0.M1/reference/html','1','1.1.0.M1');
insert into project_releaselist values('spring-shell',null,'http://docs.spring.io/spring-shell/docs/1.0.0.RELEASE/api/','spring-shell','org.springframework.shell','TRUE','http://docs.spring.io/spring-shell/docs/1.0.0.RELEASE/reference/html','2','1.0.0.RELEASE');
insert into project_releaselist values('spring-social','spring-snapshots','http://docs.spring.io/spring-social/docs/1.1.0.BUILD-SNAPSHOT/api/','spring-social','org.springframework.social','FALSE','http://docs.spring.io/spring-social/docs/1.1.0.BUILD-SNAPSHOT/reference/html','0','1.1.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-social','spring-milestones','http://docs.spring.io/spring-social/docs/1.1.0.M4/api/','spring-social','org.springframework.social','FALSE','http://docs.spring.io/spring-social/docs/1.1.0.M4/reference/html','1','1.1.0.M4');
insert into project_releaselist values('spring-social',null,'http://docs.spring.io/spring-social/docs/1.0.3.RELEASE/api/','spring-social','org.springframework.social','TRUE','http://docs.spring.io/spring-social/docs/1.0.3.RELEASE/reference/html','2','1.0.3.RELEASE');
insert into project_releaselist values('spring-social-facebook','spring-snapshots','http://docs.spring.io/spring-social-facebook/docs/1.1.0.BUILD-SNAPSHOT/api/','spring-social-facebook','org.springframework.social','FALSE','http://docs.spring.io/spring-social-facebook/docs/1.1.0.BUILD-SNAPSHOT/reference/html','0','1.1.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-social-facebook','spring-milestones','http://docs.spring.io/spring-social-facebook/docs/1.1.0.M4/api/','spring-social-facebook','org.springframework.social','FALSE','http://docs.spring.io/spring-social-facebook/docs/1.1.0.M4/reference/html','1','1.1.0.M4');
insert into project_releaselist values('spring-social-facebook',null,'http://docs.spring.io/spring-social-facebook/docs/1.0.3.RELEASE/api/','spring-social-facebook','org.springframework.social','TRUE','http://docs.spring.io/spring-social-facebook/docs/1.0.3.RELEASE/reference/html','2','1.0.3.RELEASE');
insert into project_releaselist values('spring-social-twitter','spring-snapshots','http://docs.spring.io/spring-social-twitter/docs/1.1.0.BUILD-SNAPSHOT/api/','spring-social-twitter','org.springframework.social','FALSE','http://docs.spring.io/spring-social-twitter/docs/1.1.0.BUILD-SNAPSHOT/reference/html','0','1.1.0.BUILD-SNAPSHOT');
insert into project_releaselist values('spring-social-twitter','spring-milestones','http://docs.spring.io/spring-social-twitter/docs/1.1.0.M4/api/','spring-social-twitter','org.springframework.social','FALSE','http://docs.spring.io/spring-social-twitter/docs/1.1.0.M4/reference/html','1','1.1.0.M4');
insert into project_releaselist values('spring-social-twitter',null,'http://docs.spring.io/spring-social-twitter/docs/1.0.5.RELEASE/api/','spring-social-twitter','org.springframework.social','TRUE','http://docs.spring.io/spring-social-twitter/docs/1.0.5.RELEASE/reference/html','2','1.0.5.RELEASE');
insert into project_releaselist values('spring-webflow','spring-milestones','http://docs.spring.io/spring-webflow/docs/2.4.0.M1/api/','spring-webflow','org.springframework.webflow','FALSE','http://docs.spring.io/spring-webflow/docs/2.4.0.M1/reference/html','1','2.4.0.M1');
insert into project_releaselist values('spring-webflow',null,'http://docs.spring.io/spring-webflow/docs/2.3.2.RELEASE/javadoc-api/','spring-webflow','org.springframework.webflow','TRUE','http://docs.spring.io/spring-webflow/docs/2.3.2.RELEASE/reference/html','2','2.3.2.RELEASE');
insert into project_releaselist values('spring-ws',null,'http://docs.spring.io/spring-ws/sites/2.0/apidocs/index.html','spring-ws','org.springframework.ws','TRUE','http://docs.spring.io/spring-ws/sites/2.0/reference/html/index.html','2','2.1.4.RELEASE');
insert into project_releaselist values('spring-data-graph',null,'http://docs.spring.io/spring-data/data-graph/docs/1.1.0.RELEASE/api/','spring-data-graph','org.springframework.data','TRUE','http://docs.spring.io/spring-data/data-graph/docs/1.1.0.RELEASE/reference/html','2','1.1.0.RELEASE');
insert into project_releaselist values('spring-test-mvc','spring-milestones','','spring-test-mvc','org.springframework.test','FALSE','','1','1.0.0.M2');
