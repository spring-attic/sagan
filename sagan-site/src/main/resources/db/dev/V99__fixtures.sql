-- Member Profiles

INSERT INTO member_profile
(avatar_url, bio, latitude, longitude, github_id,  github_username, gravatar_email, hidden, lanyrd_username,
 location, name, speakerdeck_username, twitter_username,  username, video_embeds, job_title)
 VALUES ('/images/icon-spring-boot.svg', 'Spring Boot team member and fixture user for the application.',
         37.781929, -122.4041, 123456, 'spring', 'spring@spring.io', FALSE, NULL, 'San Francisco, CA',
         'Emily Spring', NULL, 'springcentral', 'springcentral', NULL, 'Spring Team Member');

-- Blog Posts

INSERT INTO post (broadcast, category, created_at, draft, format, public_slug, publish_at, raw_content,
                  rendered_content, rendered_summary, title, author_id)
VALUES (FALSE,'RELEASES','2020-01-22 08:55:32.503',FALSE,'MARKDOWN','2020/01/22/spring-boot-2-3-0','2020-01-22 08:56:05.769',
'Dear Spring Community,

We are happy to announce the 2.3.0 release of Spring Boot.
Highlights from this release include:

* A great feature
* Another great feature
* Many bug fixes',
'<p>Dear Spring Community,</p>
<p>We are happy to announce the 2.3.0 release of Spring Boot.</p>
<p>Highlights from this release include:</p>
<ul>
<li>A great feature</li><li>Another great feature</li><li>Many bug fixes</li>
</ul>', '<p>Dear Spring Community,</p><p>We are happy to announce the 2.3.0 release of Spring Boot.</p>'
,'Spring Boot 2.3.0 released',1),

(FALSE,'NEWS_AND_EVENTS','2020-01-18 08:55:32.503',FALSE,'MARKDOWN','2020/01/18/this-week-in-spring-2020-01-18','2020-01-18 08:56:05.769',
'Hi, Spring fans!
There is a lot to cover this week, so let s get to it.

Links this week:

* [Spring Blog](https://spring.io/blog)
* [Spring Initializr](https://start.spring.io)
',
'<p>Hi, Spring fans!</p>
<p>There is a lot to cover this week, so let s get to it.</p>
<p>Links this week:</p><ul>
<li><a href="https://spring.io/blog">Spring Blog</a></li>
<li><a href="https://start.spring.io/">Spring Initializr</a></li></ul>
',
'<p>Hi Spring fans!</p><p>There is a lot to cover this week, so let s get to it.</p>',
'This Week in Spring - 2020-01-18',1);

-- Projects

INSERT INTO project
(id, name, parent_project_id, repo_url, status, support_policy, site_url, stack_overflow_tags,
 tag_line, bootconfig_source, bootconfig_html, overview_source, overview_html,
 featured, sort_order, image_path)
VALUES ('spring-framework', 'Spring Framework', NULL, 'https://github.com/spring-projects/spring-framework', 'ACTIVE', 'UPSTREAM',
        '/projects/spring-framework',
        'spring,spring-mvc,spring-jms,spring-test,spring-webflux',
        'Provides core support for dependency injection, transaction management, web apps, data access, messaging and more.',
        '', '', '## Sample description\nThis is a sample description.',
        '<h2>Sample description</h2><p>This is a sample description.</p>', FALSE, 2, '/images/projects/spring-framework.svg'),
       ('spring-boot', 'Spring Boot', NULL, 'https://github.com/spring-projects/spring-boot', 'ACTIVE', 'SPRING_BOOT',
        '/projects/spring-boot',
        'spring-boot',
        'Takes an opinionated view of building Spring applications and gets you up and running as quickly as possible.',
        '', '', '## Sample description\nThis is a sample description.',
        '<h2>Sample description</h2><p>This is a sample description.</p>', FALSE, 1, '/images/projects/spring-boot.svg'),
       ('spring-data', 'Spring Data', NULL, 'https://github.com/spring-projects/spring-data', 'ACTIVE', 'UPSTREAM',
        '/projects/spring-data',
        'spring-data,spring-data-jpa,spring-data-mongodb,spring-data-rest,spring-data-elasticsearch,spring-data-redis',
        'Spring Data’s mission is to provide a familiar and consistent, Spring-based programming model for data access while still retaining the special traits of the underlying data store.',
        '', '', '## Sample description\nThis is a sample description.',
        '<h2>Sample description</h2><p>This is a sample description.</p>', FALSE, 3, '/images/projects/spring-data.svg'),
       ('spring-data-jpa', 'Spring Data JPA', 'spring-data', 'https://github.com/spring-projects/spring-data-jpa', 'ACTIVE', 'UPSTREAM',
        '/projects/spring-data-jpa', 'spring-data',
        'Provides a familiar and consistent, Spring-based programming model for relational database access.',
        'Add `spring-boot-starter-data-jpa` as a dependency.',
        'Add <pre><code>spring-boot-starter-data-jpa</code></pre> as a dependency.',
        '## Sample description\nThis is a sample description.',
        '<h2>Sample description</h2><p>This is a sample description.</p>', FALSE, 255, '/images/projects/spring-data.svg'),
       ('spring-data-elasticsearch', 'Spring Data Elasticsearch', 'spring-data',
        'https://github.com/spring-projects/spring-data-elasticsearch', 'ACTIVE', 'UPSTREAM', '/projects/spring-data-elasticsearch',
        'spring-data-elasticsearch',
        'Provides a familiar and consistent, Spring-based programming model for Elasticsearch index access.',
        'Add `spring-boot-starter-data-elasticsearch` as a dependency.',
        'Add <pre><code>spring-boot-starter-data-elasticsearch</code></pre> as a dependency.',
        '## Sample description\nThis is a sample description.',
        '<h2>Sample description</h2><p>This is a sample description.</p>', FALSE, 255, '/images/projects/spring-data.svg'),
       ('platform', 'Spring IO Platform', NULL, 'https://github.com/spring-io/platform', 'END_OF_LIFE', 'DOWNSTREAM',
        'http://platform.spring.io/platform/', 'spring-io',
        'Spring IO is a cohesive, versioned platform for building modern applications.', '', '',
        'It is a modular, enterprise-grade distribution that delivers a curated set of dependencies while keeping developers in full control of deploying only the parts they need. Spring IO is 100% open source, lean, and modular.',
        '<p>It is a modular, enterprise-grade distribution that delivers a curated set of dependencies while keeping developers in full control of deploying only the parts they need. Spring IO is 100% open source, lean, and modular.</p>',
        FALSE, 255, ''),
       ('spring-scala', 'Spring Scala', NULL, 'http://hub.darcs.net/psnively/spring-scala', 'COMMUNITY', 'DOWNSTREAM',
        'http://hub.darcs.net/psnively/spring-scala', 'spring-scala',
        'Brings the power and expressiveness of Scala together with the productivity and deep ecosystem of Spring.', '',
        '',
        '## Sample description\nThis is a sample description.',
        '<h2>Sample description</h2><p>This is a sample description.</p>', FALSE, 255, ''),
       ('spring-restdocs', 'Spring REST Docs', NULL, 'https://github.com/spring-projects/spring-restdocs', 'ACTIVE', 'UPSTREAM',
        '/projects/spring-restdocs', 'spring-restdocs',
        'Document RESTful services by combining hand-written documentation with auto-generated snippets produced with Spring MVC Test or REST Assured.',
        '', '',
        'Spring REST Docs helps you to document RESTful services.',
        '<p>Spring REST Docs helps you to document RESTful services.</p>',
        TRUE, 255, '/images/projects/spring-restdocs.png'),
       ('spring-cloud', 'Spring Cloud', NULL, 'https://github.com/spring-cloud/spring-cloud-release', 'ACTIVE', 'DOWNSTREAM',
        '/projects/spring-cloud',
        'spring-cloud',
        'Spring Cloud provides tools for developers to quickly build some of the common patterns in distributed systems.',
        '', '', '## Sample description\nThis is a sample description.',
        '<h2>Sample description</h2><p>This is a sample description.</p>', FALSE, 255, '/images/projects/spring-cloud.svg');

-- Project releases

INSERT INTO project_release
(project_id, repository, api_doc_url, is_current, ref_doc_url, release_status, version)
VALUES ('spring-boot', 'RELEASE', 'https://docs.spring.io/spring-boot/docs/{version}/api/', false,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/', 'GENERAL_AVAILABILITY', '2.1.14.RELEASE'),
       ('spring-boot', 'SNAPSHOT', 'https://docs.spring.io/spring-boot/docs/{version}/api/', false,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/', 'SNAPSHOT', '2.1.15.BUILD-SNAPSHOT'),
       ('spring-boot', 'RELEASE', 'https://docs.spring.io/spring-boot/docs/{version}/api/', false,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/', 'GENERAL_AVAILABILITY', '2.2.7.RELEASE'),
       ('spring-boot', 'SNAPSHOT', 'https://docs.spring.io/spring-boot/docs/{version}/api/', false,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/', 'SNAPSHOT', '2.2.8.BUILD-SNAPSHOT'),
       ('spring-boot', 'RELEASE', 'https://docs.spring.io/spring-boot/docs/{version}/api/', true,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/','GENERAL_AVAILABILITY', '2.3.0.RELEASE'),
       ('spring-boot', 'SNAPSHOT', 'https://docs.spring.io/spring-boot/docs/{version}/api/', false,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/', 'SNAPSHOT', '2.3.1-SNAPSHOT'),
       ('spring-boot', 'SNAPSHOT', 'https://docs.spring.io/spring-boot/docs/{version}/api/', false,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/', 'SNAPSHOT', '2.4.0-SNAPSHOT'),
       ('spring-boot', 'MILESTONE', 'https://docs.spring.io/spring-boot/docs/{version}/api/', false,
        'https://docs.spring.io/spring-boot/docs/{version}/reference/html/','PRERELEASE', '2.4.0.M1'),
       ('platform', 'RELEASE', 'https://spring.io/projects/platform', true,
        'https://docs.spring.io/platform/docs/{version}/reference/htmlsingle/', 'SNAPSHOT', 'Cairo-SR7');


-- Project samples

INSERT INTO project_sample (title, description, url, sort_order, project_id)
VALUES ('Simple', 'Simple command line application',
        'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-simple', 0,
        'spring-boot'),
       ('Embedded Tomcat', 'It is a mountain kitty.',
        'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-tomcat', 1,
        'spring-boot'),
       ('batch', 'Define and run a batch job in a few lines of code',
        'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-batch', 2,
        'spring-boot'),
       ('Data JPA', 'Stores data using Spring Data JPA with Hibernate',
        'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-data-jpa', 3,
        'spring-boot'),
       ('Integration', 'Integration application built using Spring Integration and its Java DSL',
        'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-integration',
        4, 'spring-boot'),
       ('more...', 'Collection of Spring Boot sample applications.',
        'https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples', 5, 'spring-boot');

-- Spring Tools

INSERT INTO spring_tools_platform (id)
VALUES ('vscode'),
       ('eclipse'),
       ('theia');

INSERT INTO spring_tools_platform_downloads (spring_tools_platform_id, variant, label, download_url)
VALUES ('eclipse', 'windows', 'Windows 64-bit',
        'http://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-win32.win32.x86_64.zip'),
       ('eclipse', 'macos', 'macOS 64-bit',
        'http://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-macosx.cocoa.x86_64.dmg'),
       ('eclipse', 'linux', 'Linux 64-bit',
        'http://download.springsource.com/release/STS4/4.1.1.RELEASE/dist/e4.10/spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-linux.gtk.x86_64.tar.gz'),
       ('vscode', 'marketplace', 'VSCode Marketplace',
        'https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack'),
       ('theia', 'package', 'Package for Theia',
        'https://registry.npmjs.org/@pivotal-tools/theia-spring-boot/-/theia-spring-boot-1.8.0.tgz');

-- Project groups
INSERT INTO project_groups (name, label)
VALUES ('microservices', 'Microservices'),
       ('event-driven', 'Event Driven'),
       ('cloud', 'Cloud'),
       ('reactive', 'Reactive'),
       ('web', 'Web Apps'),
       ('serverless', 'Serverless'),
       ('streams', 'Streams'),
       ('batch', 'Batch');

INSERT INTO project_groups_rel
SELECT 'spring-boot', id FROM PROJECT_GROUPS WHERE name in ('microservices', 'event-driven', 'batch');
INSERT INTO project_groups_rel
SELECT 'spring-framework', id FROM PROJECT_GROUPS WHERE name in ('microservices', 'batch');
INSERT INTO project_groups_rel
SELECT 'spring-data', id FROM PROJECT_GROUPS WHERE name in ('microservices', 'batch');
INSERT INTO project_groups_rel
SELECT 'spring-data-jpa', id FROM PROJECT_GROUPS WHERE name in ('microservices', 'batch');
INSERT INTO project_groups_rel
SELECT 'spring-data-elasticsearch', id FROM PROJECT_GROUPS WHERE name in ('microservices', 'batch');
INSERT INTO project_groups_rel
SELECT 'platform', id FROM PROJECT_GROUPS WHERE name in ('microservices', 'batch');
INSERT INTO project_groups_rel
SELECT 'spring-restdocs', id FROM PROJECT_GROUPS WHERE name in ('microservices');

-- Project generations
INSERT INTO project_generation (project_id, name, initial_release_date, oss_support_enforced_end_date, oss_support_policy_end_date, commercial_support_enforced_end_date, commercial_support_policy_end_date)
VALUES ('spring-boot', '2.2.x', '2019-10-01', null, null, null, null),
       ('spring-boot', '2.3.x', '2020-05-15', null, null, null, null),
       ('spring-boot', '2.4.x', '2020-11-15', null, null, null, null),
       ('spring-boot', '2.5.x', '2021-05-21', null, null, null, null),
       ('spring-boot', '2.6.x', '2021-11-18', null, null, null, null),
       ('spring-cloud', 'Hoxton.x', '2019-12-02', null, null, null, null),
       ('spring-cloud', '2020.0.x', '2020-12-22', null, null, null, null),
       ('spring-cloud', '2021.0.x', '2021-11-30', null, null, null, null);