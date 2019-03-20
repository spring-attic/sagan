-- Project Repositories

insert into project_repository values('spring-libs-milestone','Spring Milestones','https://repo.spring.io/libs-milestone',false);
insert into project_repository values('spring-libs-release','Spring Releases','https://repo.spring.io/libs-release',false);
insert into project_repository values('spring-libs-snapshot','Spring Snapshots','https://repo.spring.io/libs-snapshot',true);
insert into project_repository values('spring-milestones','Spring Milestones','https://repo.spring.io/libs-milestone',false);
insert into project_repository values('spring-snapshots','Spring Snapshots','https://repo.spring.io/libs-snapshot',true);

-- Projects

insert into project values('spring-boot','Spring Boot','https://github.com/spring-projects/spring-boot','active','https://projects.spring.io/spring-boot','spring-boot', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit', 'Lorem ipsum dolor sit amet, <a>consectetur</a> adipiscing elit.', 'hello world', '<ul><li>hello world</li></ul>', NULL, 0);
insert into project values('spring-framework','Spring Framework','https://github.com/spring-projects/spring-framework','active','https://projects.spring.io/spring-framework','spring,spring-mvc,spring-aop,spring-jdbc,spring-transactions,spring-annotations,spring-jms,spring-el,spring-test,spring-java-config,spring-remoting,spring-orm,spring-jmx,spring-cache,spring-webflux', '', '', '', '', NULL, 1);
insert into project values('platform','Spring IO Platform','https://github.com/spring-io/platform','active','https://platform.spring.io/platform/','spring-io', '', '', '', '', NULL, 2);
insert into project values('spring-data','Spring Data','https://github.com/spring-projects/spring-data','active','https://projects.spring.io/spring-data','spring-data,spring-data-jpa,spring-data-neo4j,spring-data-mongodb,spring-data-rest,spring-data-solr,spring-data-gemfire,spring-data-elasticsearch,spring-data-couchbase,spring-data-cassandra,spring-data-redis', '', '', '', '', NULL, 3);
insert into project values('spring-data-cassandra','Spring Data for Apache Cassandra','https://github.com/spring-projects/spring-data-cassandra','incubator','https://projects.spring.io/spring-data-cassandra','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-commons','Spring Data Commons','https://github.com/spring-projects/spring-data-commons','active','https://projects.spring.io/spring-data-commons','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-couchbase','Spring Data Couchbase','https://github.com/spring-projects/spring-data-couchbase','incubator','https://projects.spring.io/spring-data-couchbase','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-elasticsearch','Spring Data Elasticsearch','https://github.com/spring-projects/spring-data-elasticsearch','incubator','https://projects.spring.io/spring-data-elasticsearch','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-envers','Spring Data Envers','https://github.com/spring-projects/spring-data-envers','active','https://projects.spring.io/spring-data-envers/','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-gemfire','Spring Data GemFire','https://github.com/spring-projects/spring-data-gemfire','active','https://projects.spring.io/spring-data-gemfire','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-graph','Spring Data Graph','https://github.com/spring-projects/spring-data-graph','attic','',NULL, '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-jdbc-ext','Spring Data JDBC Extensions','https://github.com/spring-projects/spring-data-jdbc-ext','active','https://projects.spring.io/spring-data-jdbc-ext','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-jpa','Spring Data JPA','https://github.com/spring-projects/spring-data-jpa','active','https://projects.spring.io/spring-data-jpa','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-ldap','Spring Data LDAP','https://github.com/spring-projects/spring-data-ldap','active','https://projects.spring.io/spring-data-ldap','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-mongodb','Spring Data MongoDB','https://github.com/spring-projects/spring-data-mongodb','active','https://projects.spring.io/spring-data-mongodb','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-neo4j','Spring Data Neo4J','https://github.com/spring-projects/spring-data-neo4j','active','https://projects.spring.io/spring-data-neo4j','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-redis','Spring Data Redis','https://github.com/spring-projects/spring-data-redis','active','https://projects.spring.io/spring-data-redis','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-rest','Spring Data REST','https://github.com/spring-projects/spring-data-rest','active','https://projects.spring.io/spring-data-rest','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-data-solr','Spring Data for Apache Solr','https://github.com/spring-projects/spring-data-solr','active','https://projects.spring.io/spring-data-solr','', '', '', '', '', 'spring-data', 3);
insert into project values('spring-security','Spring Security','https://github.com/spring-projects/spring-security','active','https://projects.spring.io/spring-security','spring-security,spring-security-oauth2,spring-security-kerberos', '', '', '', '', NULL, 4);
insert into project values('spring-security-kerberos','Spring Security Kerberos','https://github.com/spring-projects/spring-security-kerberos','active','https://projects.spring.io/spring-security-kerberos/','', '', '', '', '', 'spring-security', 4);
insert into project values('spring-security-oauth','Spring Security OAuth','https://github.com/spring-projects/spring-security-oauth','active','https://projects.spring.io/spring-security-oauth','spring-security-oauth2', '', '', '', '', 'spring-security', 4);
insert into project values('spring-security-saml','Spring Security SAML','https://github.com/spring-projects/spring-security-saml','incubator','https://docs.spring.io/spring-security/site/extensions/saml/','', '', '', '', '', 'spring-security', 4);
insert into project values('spring-cloud','Spring Cloud','https://github.com/spring-projects/spring-cloud','active','https://projects.spring.io/spring-cloud','spring-cloud', '', '', '', '', NULL, 5);
insert into project values('spring-cloud-aws','Spring Cloud for Amazon Web Services','https://github.com/spring-cloud/spring-cloud-aws','community','https://cloud.spring.io/spring-cloud-aws','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-bus','Spring Cloud Bus','https://github.com/spring-cloud/spring-cloud-bus','incubator','https://cloud.spring.io/spring-cloud-bus','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-cli','Spring Cloud CLI','https://github.com/spring-cloud/spring-cloud-cli','active','https://cloud.spring.io/spring-cloud-cli','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-cloudfoundry','Spring Cloud for Cloud Foundry','https://github.com/spring-cloud/spring-cloud-cloudfoundry','incubator','https://cloud.spring.io/spring-cloud-cloudfoundry','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-cloudfoundry-service-broker','Spring Cloud Cloud Foundry Service Broker','https://github.com/spring-cloud/spring-cloud-cloudfoundry-service-broker','incubator','https://cloud.spring.io/spring-cloud-cloudfoundry-service-broker','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-cluster','Spring Cloud Cluster','https://github.com/spring-cloud/spring-cloud-cluster','active','https://cloud.spring.io/spring-cloud-cluster','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-commons','Spring Cloud Commons','https://github.com/spring-cloud/spring-cloud-commons','active','https://cloud.spring.io/spring-cloud-commons','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-config','Spring Cloud Config','https://github.com/spring-cloud/spring-cloud-config','active','https://cloud.spring.io/spring-cloud-config','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-connectors','Spring Cloud Connectors','https://github.com/spring-cloud/spring-cloud-connectors','active','https://spring-cloud.github.io/spring-cloud-connectors','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-consul','Spring Cloud Consul','https://github.com/spring-cloud/spring-cloud-consul','active','https://cloud.spring.io/spring-cloud-consul','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-contract','Spring Cloud Contract','https://github.com/spring-cloud/spring-cloud-contract','active','https://cloud.spring.io/spring-cloud-contract/','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-data','spring-cloud-data','https://github.com/spring-cloud/spring-cloud-data','incubator','https://cloud.spring.io/spring-cloud-data','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-function','Spring Cloud Function','https://github.com/spring-cloud/spring-cloud-function','active','https://cloud.spring.io/spring-cloud-function','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-gateway','Spring Cloud Gateway','https://github.com/spring-cloud/spring-cloud-gateway','active','https://cloud.spring.io/spring-cloud-gateway/','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-gcp','Spring Cloud GCP','https://github.com/spring-cloud/spring-cloud-gcp','active','https://cloud.spring.io/spring-cloud-gcp','spring-cloud-gcp', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-netflix','Spring Cloud Netflix','https://github.com/spring-cloud/spring-cloud-netflix','active','https://cloud.spring.io/spring-cloud-netflix','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-pipelines','Spring Cloud Pipelines','https://github.com/spring-cloud/spring-cloud-pipelines','active','https://cloud.spring.io/spring-cloud-pipelines','spring-cloud,spring-cloud-pipelines', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-security','Spring Cloud Security','https://github.com/spring-cloud/spring-cloud-security','incubator','https://cloud.spring.io/spring-cloud-security','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-sleuth','Spring Cloud Sleuth','https://github.com/spring-cloud/spring-cloud-sleuth','active','https://cloud.spring.io/spring-cloud-sleuth','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-spinnaker','Spring Cloud Spinnaker','https://github.com/spring-cloud/spring-cloud-spinnaker','active','https://cloud.spring.io/spring-cloud-spinnaker/','spinnaker', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-stream','Spring Cloud Stream','https://github.com/spring-cloud/spring-cloud-stream','active','https://cloud.spring.io/spring-cloud-stream','spring-cloud-stream', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-stream-app-starters','Spring Cloud Stream App Starters','https://github.com/spring-cloud/spring-cloud-stream-app-starters','active','https://cloud.spring.io/spring-cloud-stream-app-starters','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-task','Spring Cloud Task','https://github.com/spring-cloud/spring-cloud-task','active','https://cloud.spring.io/spring-cloud-task','spring-cloud-task', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-task-app-starters','Spring Cloud Task App Starters','https://github.com/spring-cloud/spring-cloud-task-app-starters','active',' https://cloud.spring.io/spring-cloud-task-app-starters','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-vault','Spring Cloud Vault','https://github.com/spring-cloud/spring-cloud-vault','active','https://cloud.spring.io/spring-cloud-vault/','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-vault-config','Spring Cloud Vault','https://github.com/spring-cloud/spring-cloud-vault-config','active','https://cloud.spring.io/spring-cloud-vault-config','', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-zookeeper','Spring Cloud Zookeeper','https://github.com/spring-cloud/spring-cloud-zookeeper','active','https://cloud.spring.io/spring-cloud-zookeeper','spring-cloud', '', '', '', '', 'spring-cloud', 5);
insert into project values('spring-cloud-dataflow','Spring Cloud Data Flow','https://github.com/spring-cloud/spring-cloud-dataflow','active','https://cloud.spring.io/spring-cloud-dataflow','spring-cloud-dataflow', '', '', '', '', NULL, 6);
insert into project values('spring-cloud-dataflow-server-cloudfoundry','Spring Cloud Data Flow for Cloud Foundry','https://github.com/spring-cloud/spring-cloud-dataflow-server-cloudfoundry','active','https://cloud.spring.io/spring-cloud-dataflow-server-cloudfoundry/','', '', '', '', '', 'spring-cloud-dataflow', 6);
insert into project values('spring-cloud-dataflow-server-kubernetes','Spring Cloud Data Flow for Kubernetes','https://github.com/spring-cloud/spring-cloud-dataflow-server-kubernetes','active','https://cloud.spring.io/spring-cloud-dataflow-server-kubernetes/','', '', '', '', '', 'spring-cloud-dataflow', 6);
insert into project values('spring-cloud-dataflow-server-mesos','Spring Cloud Data Flow for Apache Mesos','https://github.com/spring-cloud/spring-cloud-dataflow-server-mesos','active','https://cloud.spring.io/spring-cloud-dataflow-server-mesos','', '', '', '', '', 'spring-cloud-dataflow', 6);
insert into project values('spring-cloud-dataflow-server-yarn','Spring Cloud Data Flow for Apache YARN','https://github.com/spring-cloud/spring-cloud-dataflow-server-yarn','active','https://cloud.spring.io/spring-cloud-dataflow-server-yarn/','', '', '', '', '', 'spring-cloud-dataflow', 6);
insert into project values('spring-batch','Spring Batch','https://github.com/spring-projects/spring-batch','active','https://projects.spring.io/spring-batch','spring-batch', '', '', '', '', NULL, 7);
insert into project values('spring-restdocs','Spring REST Docs','https://github.com/spring-projects/spring-restdocs','active','https://projects.spring.io/spring-restdocs','spring-restdocs', '', '', '', '', NULL, 8);
insert into project values('spring-hateoas','Spring HATEOAS','https://github.com/spring-projects/spring-hateoas','active','https://projects.spring.io/spring-hateoas','spring-hateoas', '', '', '', '', NULL, 9);
insert into project values('spring-integration','Spring Integration','https://github.com/spring-projects/spring-integration','active','https://projects.spring.io/spring-integration','spring-integration', '', '', '', '', NULL, 10);
insert into project values('spring-integration-dsl-groovy','Spring Integration Groovy DSL','https://github.com/spring-projects/spring-integration-dsl-groovy','incubator','',NULL, '', '', '', '', NULL, 10);
insert into project values('spring-integration-dsl-scala','Spring Integration Scala DSL','https://github.com/spring-projects/spring-integration-dsl-scala','incubator','',NULL, '', '', '', '', NULL, 10);
insert into project values('spring-session','Spring Session','https://github.com/spring-projects/spring-session','active','https://projects.spring.io/spring-session/','spring-session', '', '', '', '', NULL, 11);
insert into project values('spring-session-data-mongodb','Spring Session MongoDB','https://github.com/spring-projects/spring-session-data-mongodb','active','https://projects.spring.io/spring-session-data-mongodb','', '', '', '', '', NULL, 11);
insert into project values('spring-amqp','Spring AMQP','https://github.com/spring-projects/spring-amqp','active','https://projects.spring.io/spring-amqp','spring-amqp', '', '', '', '', NULL, 12);
insert into project values('spring-android','Spring for Android','https://github.com/spring-projects/spring-android','active','https://projects.spring.io/spring-android','spring-android', '', '', '', '', NULL, 12);
insert into project values('spring-credhub','Spring CredHub','https://github.com/spring-projects/spring-credhub','active','https://projects.spring.io/spring-credhub','spring-credhub', '', '', '', '', NULL, 12);
insert into project values('spring-flex','Spring Flex','https://github.com/spring-projects/spring-flex','attic','https://projects.spring.io/spring-flex','', '', '', '', '', NULL, 12);
insert into project values('spring-flo','Spring Flo','https://github.com/spring-projects/spring-flo','incubator','https://projects.spring.io/spring-flo/','', '', '', '', '', NULL, 12);
insert into project values('spring-hadoop','Spring for Apache Hadoop','https://github.com/spring-projects/spring-hadoop','active','https://projects.spring.io/spring-hadoop','', '', '', '', '', NULL, 12);
insert into project values('spring-kafka','Spring for Apache Kafka','https://github.com/spring-projects/spring-kafka','incubator','https://projects.spring.io/spring-kafka','', '', '', '', '', NULL, 12);
insert into project values('spring-ldap','Spring LDAP','https://github.com/spring-projects/spring-ldap','active','https://projects.spring.io/spring-ldap','spring-ldap', '', '', '', '', NULL, 12);
insert into project values('spring-mobile','Spring Mobile','https://github.com/spring-projects/spring-mobile','active','https://projects.spring.io/spring-mobile','spring-mobile', '', '', '', '', NULL, 12);
insert into project values('spring-plugin','Spring Plugin','https://github.com/spring-projects/spring-plugin','incubator','https://projects.spring.io/spring-plugin','', '', '', '', '', NULL, 12);
insert into project values('spring-roo','Spring Roo','https://github.com/spring-projects/spring-roo','active','https://projects.spring.io/spring-roo','', '', '', '', '', NULL, 12);
insert into project values('spring-scala','Spring Scala','https://hub.darcs.net/psnively/spring-scala','attic','https://hub.darcs.net/psnively/spring-scala','', '', '', '', '', NULL, 12);
insert into project values('spring-shell','Spring Shell','https://github.com/spring-projects/spring-shell','active','https://projects.spring.io/spring-shell','spring-shell', '', '', '', '', NULL, 12);
insert into project values('spring-social','Spring Social','https://github.com/spring-projects/spring-social','active','https://projects.spring.io/spring-social','spring-social,spring-social-facebook,spring-social-linkedin,spring-social-google', '', '', '', '', NULL, 12);
insert into project values('spring-social-facebook','Spring Social Facebook','https://github.com/spring-projects/spring-social-facebook','active','https://projects.spring.io/spring-social-facebook','', '', '', '', '', 'spring-social', 12);
insert into project values('spring-social-github','Spring Social GitHub','https://github.com/spring-projects/spring-social-github','incubator','https://projects.spring.io/spring-social-github',NULL, '', '', '', '', 'spring-social', 12);
insert into project values('spring-social-linkedin','Spring Social LinkedIn','https://github.com/spring-projects/spring-social-linkedin','incubator','https://projects.spring.io/spring-social-linkedin',NULL, '', '', '', '', 'spring-social', 12);
insert into project values('spring-social-tripit','Spring Social TripIt','https://github.com/spring-projects/spring-social-tripit','incubator','https://projects.spring.io/spring-social-tripit',NULL, '', '', '', '', 'spring-social', 12);
insert into project values('spring-social-twitter','Spring Social Twitter','https://github.com/spring-projects/spring-social-twitter','active','https://projects.spring.io/spring-social-twitter',NULL, '', '', '', '', 'spring-social', 12);
insert into project values('spring-statemachine','Spring Statemachine','https://github.com/spring-projects/spring-statemachine','active','https://projects.spring.io/spring-statemachine','spring-statemachine', '', '', '', '', NULL, 12);
insert into project values('spring-test-htmlunit','Spring Test HtmlUnit','https://github.com/spring-projects/spring-test-htmlunit','active','https://projects.spring.io/spring-test-htmlunit','', '', '', '', '', NULL, 12);
insert into project values('spring-test-mvc','Spring Test MVC','https://github.com/spring-projects/spring-test-mvc','attic','',NULL, '', '', '', '', NULL, 12);
insert into project values('spring-vault','Spring Vault','https://github.com/spring-projects/spring-vault','active','https://projects.spring.io/spring-vault','spring-vault', '', '', '', '', NULL, 12);
insert into project values('spring-webflow','Spring Web Flow','https://github.com/spring-projects/spring-webflow','active','https://projects.spring.io/spring-webflow','spring-webflow,spring-webflow-2', '', '', '', '', NULL, 12);
insert into project values('spring-ws','Spring Web Services','https://github.com/spring-projects/spring-ws','active','https://projects.spring.io/spring-ws','spring-ws', '', '', '', '', NULL, 12);
insert into project values('spring-xd','Spring XD','https://github.com/spring-projects/spring-xd','attic','https://projects.spring.io/spring-xd','spring-xd', '', '', '', '', NULL, 12);

-- Project releases

INSERT INTO project_release_list values('spring-boot',NULL,'https://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/api/','spring-boot','org.springframework.boot',false,'https://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/reference/htmlsingle/',2,'1.4.7.RELEASE');
INSERT INTO project_release_list values('spring-boot',NULL,'https://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/api/','spring-boot','org.springframework.boot',true,'https://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/reference/htmlsingle/',2,'1.5.7.RELEASE');
INSERT INTO project_release_list values('spring-boot','spring-snapshots','https://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/api/','spring-boot','org.springframework.boot',false,'https://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/reference/htmlsingle/',0,'1.5.8.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-boot','spring-snapshots','https://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/api/','spring-boot','org.springframework.boot',false,'https://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/reference/htmlsingle/',0,'2.0.0.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-boot','spring-milestones','https://docs.spring.io/spring-boot/docs/2.0.0.M4/api/','spring-boot','org.springframework.boot',false,'https://docs.spring.io/spring-boot/docs/2.0.0.M4/reference/htmlsingle/',1,'2.0.0.M4');
INSERT INTO project_release_list values('platform',NULL,'','platform','io.spring.platform',false,'https://docs.spring.io/platform/docs/Athens-SR6/reference/htmlsingle/',2,'Athens-SR6');
INSERT INTO project_release_list values('platform','spring-snapshots','','platform ','io.spring.platform',false,'https://docs.spring.io/platform/docs/Brussels-BUILD-SNAPSHOT/reference/htmlsingle/',0,'Brussels-BUILD-SNAPSHOT');
INSERT INTO project_release_list values('platform',NULL,'','platform','io.spring.platform',false,'https://docs.spring.io/platform/docs/Brussels-SR4/reference/htmlsingle/',2,'Brussels-SR4');
INSERT INTO project_release_list values('spring-framework',NULL,'https://docs.spring.io/spring/docs/3.2.18.RELEASE/javadoc-api/','spring-context','org.springframework',false,'https://docs.spring.io/spring/docs/3.2.18.RELEASE/spring-framework-reference/htmlsingle/',2,'3.2.18.RELEASE');
INSERT INTO project_release_list values('spring-framework',NULL,'https://docs.spring.io/spring/docs/4.2.9.RELEASE/javadoc-api/','spring-context','org.springframework',false,'https://docs.spring.io/spring/docs/4.2.9.RELEASE/spring-framework-reference/htmlsingle/',2,'4.2.9.RELEASE');
INSERT INTO project_release_list values('spring-framework',NULL,'https://docs.spring.io/spring/docs/current/javadoc-api/','spring-context','org.springframework',true,'https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/',2,'4.3.11.RELEASE');
INSERT INTO project_release_list values('spring-framework','spring-snapshots','https://docs.spring.io/spring/docs/4.3.12.BUILD-SNAPSHOT/javadoc-api/','spring-context','org.springframework',false,'https://docs.spring.io/spring/docs/4.3.12.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/',0,'4.3.12.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-framework','spring-snapshots','https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/javadoc-api/','spring-context','org.springframework',false,'https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/',0,'5.0.0.BUILD-SNAPSHOT');
INSERT INTO project_release_list values('spring-framework','spring-milestones','https://docs.spring.io/spring/docs/5.0.0.RC4/javadoc-api/','spring-context','org.springframework',false,'https://docs.spring.io/spring/docs/5.0.0.RC4/spring-framework-reference/',1,'5.0.0.RC4');

-- Project samples

INSERT INTO project_sample_list (title, description, url, display_order, project_id) VALUES
  ('Simple', 'Simple command line application', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-simple', 0, 'spring-boot'),
  ('Embedded Tomcat', 'It is a mountain kitty.', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-tomcat', 1, 'spring-boot'),
  ('Batch', 'Define and run a Batch job in a few lines of code', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-batch', 2, 'spring-boot'),
  ('Data JPA', 'Stores data using Spring Data JPA with Hibernate', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-data-jpa', 3, 'spring-boot'),
  ('Integration', 'Integration application built using Spring Integration and its Java DSL', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-integration', 4, 'spring-boot'),
  ('more…', 'Collection of Spring Boot sample applications.', 'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples', 5, 'spring-boot');

-- Spring Tools

INSERT INTO spring_tools_platform VALUES ('vscode');
INSERT INTO spring_tools_platform VALUES ('eclipse');
INSERT INTO spring_tools_platform VALUES ('atom');

INSERT INTO spring_tools_platform_downloads (spring_tools_platform_id, variant, label, download_url) VALUES
('eclipse', 'windows', 'Windows 64-bit', 'https://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-win32.win32.x86_64.zip'),
('eclipse', 'macos', 'macOS 64-bit', 'https://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-macosx.cocoa.x86_64.dmg'),
('eclipse', 'linux', 'Linux 64-bit', 'https://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-linux.gtk.x86_64.tar.gz'),
('vscode', 'marketplace', 'VSCode Marketplace', 'https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack'),
('atom', 'package', 'Package for Atom', 'https://atom.io/packages/spring-boot');
