-- Project Repositories

insert into project_repository values('spring-libs-milestone','Spring Milestones','https://repo.spring.io/libs-milestone',false);
insert into project_repository values('spring-libs-release','Spring Releases','https://repo.spring.io/libs-release',false);
insert into project_repository values('spring-libs-snapshot','Spring Snapshots','https://repo.spring.io/libs-snapshot',true);
insert into project_repository values('spring-milestones','Spring Milestones','https://repo.spring.io/libs-milestone',false);
insert into project_repository values('spring-snapshots','Spring Snapshots','https://repo.spring.io/libs-snapshot',true);

-- Projects

insert into project values('spring-boot','Spring Boot','https://github.com/spring-projects/spring-boot','active','/projects/spring-boot','spring-boot', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit', 'Lorem ipsum dolor sit amet, <a>consectetur</a> adipiscing elit.', 'hello world', '<ul><li>hello world</li></ul>', NULL, 0, '', FALSE);
insert into project values('spring-framework','Spring Framework','http://github.com/spring-projects/spring-framework','active','/projects/spring-framework','spring,spring-mvc,spring-aop,spring-jdbc,spring-transactions,spring-annotations,spring-jms,spring-el,spring-test,spring-java-config,spring-remoting,spring-orm,spring-jmx,spring-cache,spring-webflux', '', '', '', '', NULL, 1, '', FALSE);
insert into project values('platform','Spring IO Platform','http://github.com/spring-io/platform','active','http://platform.spring.io/platform/','spring-io', '', '', '', '', NULL, 2, '', FALSE);
insert into project values('spring-data','Spring Data','http://github.com/spring-projects/spring-data','active','/projects/spring-data','spring-data,spring-data-jpa,spring-data-neo4j,spring-data-mongodb,spring-data-rest,spring-data-solr,spring-data-gemfire,spring-data-elasticsearch,spring-data-couchbase,spring-data-cassandra,spring-data-redis', '', '', '', '', NULL, 3, '', FALSE);
insert into project values('spring-data-cassandra','Spring Data for Apache Cassandra','http://github.com/spring-projects/spring-data-cassandra','incubator','/projects/spring-data-cassandra','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-commons','Spring Data Commons','http://github.com/spring-projects/spring-data-commons','active','/projects/spring-data-commons','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-couchbase','Spring Data Couchbase','http://github.com/spring-projects/spring-data-couchbase','incubator','/projects/spring-data-couchbase','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-elasticsearch','Spring Data Elasticsearch','http://github.com/spring-projects/spring-data-elasticsearch','incubator','/projects/spring-data-elasticsearch','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-envers','Spring Data Envers','http://github.com/spring-projects/spring-data-envers','active','/projects/spring-data-envers/','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-gemfire','Spring Data GemFire','http://github.com/spring-projects/spring-data-gemfire','active','/projects/spring-data-gemfire','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-graph','Spring Data Graph','http://github.com/spring-projects/spring-data-graph','attic','',NULL, '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-jdbc-ext','Spring Data JDBC Extensions','http://github.com/spring-projects/spring-data-jdbc-ext','active','/projects/spring-data-jdbc-ext','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-jpa','Spring Data JPA','http://github.com/spring-projects/spring-data-jpa','active','/projects/spring-data-jpa','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-ldap','Spring Data LDAP','http://github.com/spring-projects/spring-data-ldap','active','/projects/spring-data-ldap','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-mongodb','Spring Data MongoDB','http://github.com/spring-projects/spring-data-mongodb','active','/projects/spring-data-mongodb','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-neo4j','Spring Data Neo4J','http://github.com/spring-projects/spring-data-neo4j','active','/projects/spring-data-neo4j','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-redis','Spring Data Redis','http://github.com/spring-projects/spring-data-redis','active','/projects/spring-data-redis','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-rest','Spring Data REST','http://github.com/spring-projects/spring-data-rest','active','/projects/spring-data-rest','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-data-solr','Spring Data for Apache Solr','http://github.com/spring-projects/spring-data-solr','active','/projects/spring-data-solr','', '', '', '', '', 'spring-data', 3, '', FALSE);
insert into project values('spring-security','Spring Security','http://github.com/spring-projects/spring-security','active','/projects/spring-security','spring-security,spring-security-oauth2,spring-security-kerberos', '', '', '', '', NULL, 4, '', FALSE);
insert into project values('spring-security-kerberos','Spring Security Kerberos','http://github.com/spring-projects/spring-security-kerberos','active','/projects/spring-security-kerberos/','', '', '', '', '', 'spring-security', 4, '', FALSE);
insert into project values('spring-security-oauth','Spring Security OAuth','http://github.com/spring-projects/spring-security-oauth','active','/projects/spring-security-oauth','spring-security-oauth2', '', '', '', '', 'spring-security', 4, '', FALSE);
insert into project values('spring-security-saml','Spring Security SAML','http://github.com/spring-projects/spring-security-saml','incubator','http://docs.spring.io/spring-security/site/extensions/saml/','', '', '', '', '', 'spring-security', 4, '', FALSE);
insert into project values('spring-cloud','Spring Cloud','http://github.com/spring-projects/spring-cloud','active','/projects/spring-cloud','spring-cloud', '', '', '', '', NULL, 5, '', FALSE);
insert into project values('spring-cloud-aws','Spring Cloud for Amazon Web Services','http://github.com/spring-cloud/spring-cloud-aws','community','http://cloud.spring.io/spring-cloud-aws','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-bus','Spring Cloud Bus','http://github.com/spring-cloud/spring-cloud-bus','incubator','http://cloud.spring.io/spring-cloud-bus','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-cli','Spring Cloud CLI','http://github.com/spring-cloud/spring-cloud-cli','active','http://cloud.spring.io/spring-cloud-cli','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-cloudfoundry','Spring Cloud for Cloud Foundry','http://github.com/spring-cloud/spring-cloud-cloudfoundry','incubator','http://cloud.spring.io/spring-cloud-cloudfoundry','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-cloudfoundry-service-broker','Spring Cloud Cloud Foundry Service Broker','https://github.com/spring-cloud/spring-cloud-cloudfoundry-service-broker','incubator','http://cloud.spring.io/spring-cloud-cloudfoundry-service-broker','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-cluster','Spring Cloud Cluster','http://github.com/spring-cloud/spring-cloud-cluster','active','http://cloud.spring.io/spring-cloud-cluster','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-commons','Spring Cloud Commons','http://github.com/spring-cloud/spring-cloud-commons','active','http://cloud.spring.io/spring-cloud-commons','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-config','Spring Cloud Config','http://github.com/spring-cloud/spring-cloud-config','active','http://cloud.spring.io/spring-cloud-config','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-connectors','Spring Cloud Connectors','http://github.com/spring-cloud/spring-cloud-connectors','active','http://spring-cloud.github.io/spring-cloud-connectors','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-consul','Spring Cloud Consul','https://github.com/spring-cloud/spring-cloud-consul','active','http://cloud.spring.io/spring-cloud-consul','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-contract','Spring Cloud Contract','https://github.com/spring-cloud/spring-cloud-contract','active','http://cloud.spring.io/spring-cloud-contract/','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-data','spring-cloud-data','https://github.com/spring-cloud/spring-cloud-data','incubator','http://cloud.spring.io/spring-cloud-data','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-function','Spring Cloud Function','http://github.com/spring-cloud/spring-cloud-function','active','http://cloud.spring.io/spring-cloud-function','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-gateway','Spring Cloud Gateway','https://github.com/spring-cloud/spring-cloud-gateway','active','http://cloud.spring.io/spring-cloud-gateway/','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-gcp','Spring Cloud GCP','http://github.com/spring-cloud/spring-cloud-gcp','active','http://cloud.spring.io/spring-cloud-gcp','spring-cloud-gcp', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-netflix','Spring Cloud Netflix','http://github.com/spring-cloud/spring-cloud-netflix','active','http://cloud.spring.io/spring-cloud-netflix','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-pipelines','Spring Cloud Pipelines','https://github.com/spring-cloud/spring-cloud-pipelines','active','http://cloud.spring.io/spring-cloud-pipelines','spring-cloud,spring-cloud-pipelines', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-security','Spring Cloud Security','http://github.com/spring-cloud/spring-cloud-security','incubator','http://cloud.spring.io/spring-cloud-security','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-sleuth','Spring Cloud Sleuth','http://github.com/spring-cloud/spring-cloud-sleuth','active','http://cloud.spring.io/spring-cloud-sleuth','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-spinnaker','Spring Cloud Spinnaker','https://github.com/spring-cloud/spring-cloud-spinnaker','active','https://cloud.spring.io/spring-cloud-spinnaker/','spinnaker', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-stream','Spring Cloud Stream','http://github.com/spring-cloud/spring-cloud-stream','active','http://cloud.spring.io/spring-cloud-stream','spring-cloud-stream', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-stream-app-starters','Spring Cloud Stream App Starters','http://github.com/spring-cloud/spring-cloud-stream-app-starters','active','http://cloud.spring.io/spring-cloud-stream-app-starters','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-task','Spring Cloud Task','http://github.com/spring-cloud/spring-cloud-task','active','http://cloud.spring.io/spring-cloud-task','spring-cloud-task', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-task-app-starters','Spring Cloud Task App Starters','http://github.com/spring-cloud/spring-cloud-task-app-starters','active',' http://cloud.spring.io/spring-cloud-task-app-starters','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-vault','Spring Cloud Vault','http://github.com/spring-cloud/spring-cloud-vault','active','http://cloud.spring.io/spring-cloud-vault/','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-vault-config','Spring Cloud Vault','http://github.com/spring-cloud/spring-cloud-vault-config','active','http://cloud.spring.io/spring-cloud-vault-config','', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-zookeeper','Spring Cloud Zookeeper','https://github.com/spring-cloud/spring-cloud-zookeeper','active','http://cloud.spring.io/spring-cloud-zookeeper','spring-cloud', '', '', '', '', 'spring-cloud', 5, '', FALSE);
insert into project values('spring-cloud-dataflow','Spring Cloud Data Flow','https://github.com/spring-cloud/spring-cloud-dataflow','active','http://cloud.spring.io/spring-cloud-dataflow','spring-cloud-dataflow', '', '', '', '', NULL, 6, '', FALSE);
insert into project values('spring-cloud-dataflow-server-cloudfoundry','Spring Cloud Data Flow for Cloud Foundry','https://github.com/spring-cloud/spring-cloud-dataflow-server-cloudfoundry','active','http://cloud.spring.io/spring-cloud-dataflow-server-cloudfoundry/','', '', '', '', '', 'spring-cloud-dataflow', 6, '', FALSE);
insert into project values('spring-cloud-dataflow-server-kubernetes','Spring Cloud Data Flow for Kubernetes','https://github.com/spring-cloud/spring-cloud-dataflow-server-kubernetes','active','http://cloud.spring.io/spring-cloud-dataflow-server-kubernetes/','', '', '', '', '', 'spring-cloud-dataflow', 6, '', FALSE);
insert into project values('spring-cloud-dataflow-server-mesos','Spring Cloud Data Flow for Apache Mesos','https://github.com/spring-cloud/spring-cloud-dataflow-server-mesos','active','http://cloud.spring.io/spring-cloud-dataflow-server-mesos','', '', '', '', '', 'spring-cloud-dataflow', 6, '', FALSE);
insert into project values('spring-cloud-dataflow-server-yarn','Spring Cloud Data Flow for Apache YARN','https://github.com/spring-cloud/spring-cloud-dataflow-server-yarn','active','http://cloud.spring.io/spring-cloud-dataflow-server-yarn/','', '', '', '', '', 'spring-cloud-dataflow', 6, '', FALSE);
insert into project values('spring-batch','Spring batch','http://github.com/spring-projects/spring-batch','active','/projects/spring-batch','spring-batch', '', '', '', '', NULL, 7, '', FALSE);
insert into project values('spring-restdocs','Spring REST Docs','http://github.com/spring-projects/spring-restdocs','active','/projects/spring-restdocs','spring-restdocs', '', '', '', '', NULL, 8, '', FALSE);
insert into project values('spring-hateoas','Spring HATEOAS','http://github.com/spring-projects/spring-hateoas','active','/projects/spring-hateoas','spring-hateoas', '', '', '', '', NULL, 9, '', FALSE);
insert into project values('spring-integration','Spring Integration','http://github.com/spring-projects/spring-integration','active','/projects/spring-integration','spring-integration', '', '', '', '', NULL, 10, '', FALSE);
insert into project values('spring-integration-dsl-groovy','Spring Integration Groovy DSL','http://github.com/spring-projects/spring-integration-dsl-groovy','incubator','',NULL, '', '', '', '', NULL, 10, '', FALSE);
insert into project values('spring-integration-dsl-scala','Spring Integration Scala DSL','http://github.com/spring-projects/spring-integration-dsl-scala','incubator','',NULL, '', '', '', '', NULL, 10, '', FALSE);
insert into project values('spring-session','Spring Session','http://github.com/spring-projects/spring-session','active','/projects/spring-session/','spring-session', '', '', '', '', NULL, 11, '', FALSE);
insert into project values('spring-session-data-mongodb','Spring Session MongoDB','http://github.com/spring-projects/spring-session-data-mongodb','active','/projects/spring-session-data-mongodb','', '', '', '', '', NULL, 11, '', FALSE);
insert into project values('spring-amqp','Spring AMQP','http://github.com/spring-projects/spring-amqp','active','/projects/spring-amqp','spring-amqp', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-android','Spring for Android','http://github.com/spring-projects/spring-android','active','/projects/spring-android','spring-android', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-credhub','Spring CredHub','https://github.com/spring-projects/spring-credhub','active','https:///projects/spring-credhub','spring-credhub', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-flex','Spring Flex','http://github.com/spring-projects/spring-flex','attic','/projects/spring-flex','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-flo','Spring Flo','https://github.com/spring-projects/spring-flo','incubator','/projects/spring-flo/','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-hadoop','Spring for Apache Hadoop','http://github.com/spring-projects/spring-hadoop','active','/projects/spring-hadoop','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-kafka','Spring for Apache Kafka','http://github.com/spring-projects/spring-kafka','incubator','/projects/spring-kafka','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-ldap','Spring LDAP','http://github.com/spring-projects/spring-ldap','active','/projects/spring-ldap','spring-ldap', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-mobile','Spring Mobile','http://github.com/spring-projects/spring-mobile','active','/projects/spring-mobile','spring-mobile', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-plugin','Spring Plugin','http://github.com/spring-projects/spring-plugin','incubator','/projects/spring-plugin','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-roo','Spring Roo','http://github.com/spring-projects/spring-roo','community','/projects/spring-roo','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-scala','Spring Scala','http://hub.darcs.net/psnively/spring-scala','community','http://hub.darcs.net/psnively/spring-scala','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-shell','Spring Shell','http://github.com/spring-projects/spring-shell','active','/projects/spring-shell','spring-shell', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-social','Spring Social','http://github.com/spring-projects/spring-social','attic','/projects/spring-social','spring-social,spring-social-facebook,spring-social-linkedin,spring-social-google', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-social-facebook','Spring Social Facebook','http://github.com/spring-projects/spring-social-facebook','active','/projects/spring-social-facebook','', '', '', '', '', 'spring-social', 12, '', FALSE);
insert into project values('spring-social-github','Spring Social GitHub','http://github.com/spring-projects/spring-social-github','incubator','/projects/spring-social-github',NULL, '', '', '', '', 'spring-social', 12, '', FALSE);
insert into project values('spring-social-linkedin','Spring Social LinkedIn','http://github.com/spring-projects/spring-social-linkedin','incubator','/projects/spring-social-linkedin',NULL, '', '', '', '', 'spring-social', 12, '', FALSE);
insert into project values('spring-social-tripit','Spring Social TripIt','http://github.com/spring-projects/spring-social-tripit','incubator','/projects/spring-social-tripit',NULL, '', '', '', '', 'spring-social', 12, '', FALSE);
insert into project values('spring-social-twitter','Spring Social Twitter','http://github.com/spring-projects/spring-social-twitter','active','/projects/spring-social-twitter',NULL, '', '', '', '', 'spring-social', 12, '', FALSE);
insert into project values('spring-statemachine','Spring Statemachine','http://github.com/spring-projects/spring-statemachine','active','/projects/spring-statemachine','spring-statemachine', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-test-htmlunit','Spring Test HtmlUnit','http://github.com/spring-projects/spring-test-htmlunit','active','/projects/spring-test-htmlunit','', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-test-mvc','Spring Test MVC','http://github.com/spring-projects/spring-test-mvc','attic','',NULL, '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-vault','Spring Vault','http://github.com/spring-projects/spring-vault','active','/projects/spring-vault','spring-vault', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-webflow','Spring Web Flow','http://github.com/spring-projects/spring-webflow','active','/projects/spring-webflow','spring-webflow,spring-webflow-2', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-ws','Spring Web Services','http://github.com/spring-projects/spring-ws','active','/projects/spring-ws','spring-ws', '', '', '', '', NULL, 12, '', FALSE);
insert into project values('spring-xd','Spring XD','http://github.com/spring-projects/spring-xd','attic','/projects/spring-xd','spring-xd', '', '', '', '', NULL, 12, '', FALSE);

-- Project releases

INSERT INTO project_release_list values('spring-boot',NULL,'http://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/api/','spring-boot','org.springframework.boot',false,'http://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/reference/htmlsingle/',2,'1.4.7.RELEASE');
INSERT INTO project_release_list values('spring-boot',NULL,'http://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/api/','spring-boot','org.springframework.boot',true,'http://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/reference/htmlsingle/',2,'1.5.7.RELEASE');
INSERT INTO project_release_list values('spring-boot','spring-snapshots','http://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/api/','spring-boot','org.springframework.boot',false,'http://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/reference/htmlsingle/',0,'1.5.8.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-boot','spring-snapshots','http://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/api/','spring-boot','org.springframework.boot',false,'http://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/reference/htmlsingle/',0,'2.0.0.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-boot','spring-milestones','http://docs.spring.io/spring-boot/docs/2.0.0.M4/api/','spring-boot','org.springframework.boot',false,'http://docs.spring.io/spring-boot/docs/2.0.0.M4/reference/htmlsingle/',1,'2.0.0.M4');
INSERT INTO project_release_list values('platform',NULL,'','platform','io.spring.platform',false,'https://docs.spring.io/platform/docs/Athens-SR6/reference/htmlsingle/',2,'Athens-SR6');
INSERT INTO project_release_list values('platform','spring-snapshots','','platform ','io.spring.platform',false,'https://docs.spring.io/platform/docs/Brussels-BUILD-SNAPSHOT/reference/htmlsingle/',0,'Brussels-BUILD-SNAPSHOT');
INSERT INTO project_release_list values('platform',NULL,'','platform','io.spring.platform',false,'https://docs.spring.io/platform/docs/Brussels-SR4/reference/htmlsingle/',2,'Brussels-SR4');
INSERT INTO project_release_list values('spring-framework',NULL,'http://docs.spring.io/spring/docs/3.2.18.RELEASE/javadoc-api/','spring-context','org.springframework',false,'http://docs.spring.io/spring/docs/3.2.18.RELEASE/spring-framework-reference/htmlsingle/',2,'3.2.18.RELEASE');
INSERT INTO project_release_list values('spring-framework',NULL,'http://docs.spring.io/spring/docs/4.2.9.RELEASE/javadoc-api/','spring-context','org.springframework',false,'http://docs.spring.io/spring/docs/4.2.9.RELEASE/spring-framework-reference/htmlsingle/',2,'4.2.9.RELEASE');
INSERT INTO project_release_list values('spring-framework',NULL,'http://docs.spring.io/spring/docs/current/javadoc-api/','spring-context','org.springframework',true,'http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/',2,'4.3.11.RELEASE');
INSERT INTO project_release_list values('spring-framework','spring-snapshots','http://docs.spring.io/spring/docs/4.3.12.BUILD-SNAPSHOT/javadoc-api/','spring-context','org.springframework',false,'http://docs.spring.io/spring/docs/4.3.12.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/',0,'4.3.12.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-framework','spring-snapshots','http://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/javadoc-api/','spring-context','org.springframework',false,'http://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/',0,'5.0.0.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-framework','spring-milestones','http://docs.spring.io/spring/docs/5.0.0.RC4/javadoc-api/','spring-context','org.springframework',false,'http://docs.spring.io/spring/docs/5.0.0.RC4/spring-framework-reference/',1,'5.0.0.RC4');

-- Project samples

INSERT INTO project_sample_list (title, description, url, display_order, project_id) VALUES
  ('Simple', 'Simple command line application', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-simple', 0, 'spring-boot'),
  ('Embedded Tomcat', 'It is a mountain kitty.', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-tomcat', 1, 'spring-boot'),
  ('batch', 'Define and run a batch job in a few lines of code', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-batch', 2, 'spring-boot'),
  ('Data JPA', 'Stores data using Spring Data JPA with Hibernate', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-data-jpa', 3, 'spring-boot'),
  ('Integration', 'Integration application built using Spring Integration and its Java DSL', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-integration', 4, 'spring-boot'),
  ('more…', 'Collection of Spring Boot sample applications.', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples', 5, 'spring-boot');

-- Spring Tools

INSERT INTO spring_tools_platform VALUES ('vscode');
INSERT INTO spring_tools_platform VALUES ('eclipse');
INSERT INTO spring_tools_platform VALUES ('theia');

INSERT INTO spring_tools_platform_downloads (spring_tools_platform_id, variant, label, download_url) VALUES
('eclipse', 'windows', 'Windows 64-bit', 'http://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-win32.win32.x86_64.zip'),
('eclipse', 'macos', 'macOS 64-bit', 'http://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-macosx.cocoa.x86_64.dmg'),
('eclipse', 'linux', 'Linux 64-bit', 'http://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-linux.gtk.x86_64.tar.gz'),
('vscode', 'marketplace', 'VSCode Marketplace', 'https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack'),
('theia', 'package', 'Package for Theia', 'https://registry.npmjs.org/@pivotal-tools/theia-spring-boot/-/theia-spring-boot-1.8.0.tgz');

-- Project groups
INSERT INTO project_groups (name, label) values('microservices', 'Microservices');
INSERT INTO project_groups (name, label) values('event-driven', 'Event Driven');
INSERT INTO project_groups (name, label) values('cloud', 'Cloud');
INSERT INTO project_groups (name, label) values('reactive', 'Reactive');
INSERT INTO project_groups (name, label) values('web', 'Web Apps');
INSERT INTO project_groups (name, label) values('serverless', 'Serverless');
INSERT INTO project_groups (name, label) values('streams', 'Streams');
INSERT INTO project_groups (name, label) values('batch', 'Batch');

INSERT INTO project_groups_rel SELECT 'spring-test-mvc', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-social-facebook', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-social-github', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-social-linkedin', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-social-tripit', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-social-twitter', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-couchbase', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-gateway', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-integration-dsl-groovy', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-webflow', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-ldap', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-integration-dsl-scala', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-security-saml', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-roo', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-xd', id FROM PROJECT_GROUPS WHERE name in ('event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-data', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-social', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-stream-app-starters', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-test-htmlunit', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-dataflow-server-kubernetes', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-cloudfoundry', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-spinnaker', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-dataflow-server-mesos', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-dataflow-server-yarn', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-dataflow-server-cloudfoundry', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-cluster', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-vault', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-statemachine', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-skipper', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven');
INSERT INTO project_groups_rel SELECT 'spring-cloud-pipelines', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-integration', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-zookeeper', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-connectors', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-rest', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-circuitbreaker', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-ws', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-cli', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-data-r2dbc', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-hateoas', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
--INSERT INTO project_groups_rel SELECT 'spring-session-data-geode', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-config', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-security', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-kubernetes', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-vault', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-mongodb', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-consul', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-jpa', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-gcp', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-batch', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-session', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-alibaba', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-redis', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-dataflow', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-data-neo4j', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-stream', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-framework', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-function', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-commons', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-data-jdbc', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'platform', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-security-oauth', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-task', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-data-elasticsearch', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-open-service-broker', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-app-broker', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-boot', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-amqp', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-restdocs', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-schema-registry', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven');
INSERT INTO project_groups_rel SELECT 'spring-cloud-cloudfoundry-service-broker', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-security', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-envers', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-hadoop', id FROM PROJECT_GROUPS WHERE name in ('batch');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-azure', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-cassandra', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-sleuth', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-cloud-aws', id FROM PROJECT_GROUPS WHERE name in ('microservices');
--INSERT INTO project_groups_rel SELECT 'spring-data-geode', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-data-solr', id FROM PROJECT_GROUPS WHERE name in ('microservices','batch');
INSERT INTO project_groups_rel SELECT 'spring-data-gemfire', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-stream-binder-rabbit', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-stream-binder-kafka', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-bus', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven');
INSERT INTO project_groups_rel SELECT 'spring-data-jdbc-ext', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-data-ldap', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-credhub', id FROM PROJECT_GROUPS WHERE name in ('microservices');
INSERT INTO project_groups_rel SELECT 'spring-kafka', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','cloud','serverless','streams','batch');
INSERT INTO project_groups_rel SELECT 'spring-session-data-mongodb', id FROM PROJECT_GROUPS WHERE name in ('microservices','reactive','cloud','web');
INSERT INTO project_groups_rel SELECT 'spring-cloud-netflix', id FROM PROJECT_GROUPS WHERE name in ('microservices','reactive','event-driven','cloud','web','serverless','streams','batch');
INSERT INTO project_groups_rel SELECT 'spring-cloud-contract', id FROM PROJECT_GROUPS WHERE name in ('microservices','reactive','event-driven','cloud','web','serverless','streams');
INSERT INTO project_groups_rel SELECT 'spring-cloud-task-app-starters', id FROM PROJECT_GROUPS WHERE name in ('microservices','event-driven','cloud','Streams','batch');
--INSERT INTO project_groups_rel SELECT 'spring-cloud-openfeign', id FROM PROJECT_GROUPS WHERE name in ('microservices','cloud','web');