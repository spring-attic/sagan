package sagan.blog.support;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PostSummaryTests {

    private PostSummary postSummary = new PostSummary();

    @Test
    public void extractBasicSummary() {
        String content =
                "<p>this is a blog post</p>\n"
                        + "<p>With the release of Spring 1.1 the Spring community was given it?s first taste of JMS support. This support included exception translation, message conversion, and a template class much like <a href=\"http://static.springframework.org/spring/docs/2.0-m3/api/org/springframework/jdbc/core/JdbcTemplate.html\">JdbcTemplate</a>. This support also took care of domain unification between the JMS 1.0.2 and 1.1 specs. The centerpieces of this support are the <a href=\"http://static.springframework.org/spring/docs/1.2.x/api/org/springframework/jms/core/JmsTemplate.html\">JmsTemplate</a> class and it?s JMS 1.0.2 counterpart <a href=\"http://static.springframework.org/spring/docs/1.2.x/api/org/springframework/jms/core/JmsTemplate102.html\">JmsTemplate102</a>.</p>\n"
                        + "<p>This support was a great improvement over using the raw JMS APIs to do enterprise messaging. However it did have a shortcoming; the JmsTemplate only supported synchronous reception of messages using the <a href=\"http://static.springframework.org/spring/docs/1.2.x/api/org/springframework/jms/core/JmsTemplate.html#receive()\">JmsTemplate.receive()</a> methods. This behavior worked well for many people but the vast majority of users of ended up rolling their own implementations of an asynchronous consumer. In short, they wanted what EJB 2 called <a href=\"http://java.sun.com/j2ee/tutorial/1_3-fcs/doc/EJBConcepts5.html\">Message Driven Beans</a>.</p>";

        String expected =
                "<p>this is a blog post</p>\n"
                        + "<p>With the release of Spring 1.1 the Spring community was given it?s first taste of JMS support. This support included exception translation, message conversion, and a template class much like <a href=\"http://static.springframework.org/spring/docs/2.0-m3/api/org/springframework/jdbc/core/JdbcTemplate.html\">JdbcTemplate</a>. This support also took care of domain unification between the JMS 1.0.2 and 1.1 specs. The centerpieces of this support are the <a href=\"http://static.springframework.org/spring/docs/1.2.x/api/org/springframework/jms/core/JmsTemplate.html\">JmsTemplate</a> class and it?s JMS 1.0.2 counterpart <a href=\"http://static.springframework.org/spring/docs/1.2.x/api/org/springframework/jms/core/JmsTemplate102.html\">JmsTemplate102</a>.</p>\n";

        assertThat(postSummary.forContent(content, 50), is(expected));
    }

    @Test
    public void extractNestedTagSummary() {
        String content =
                "<p>We?re pleased to announce that, after a long period of internal incubation, we?re releasing a foundational framework for asynchronous applications on the JVM which we?re calling <em>Reactor</em>. It provides abstractions for Java, Groovy and other JVM languages to make building event and data-driven applications easier. It?s also really fast. On modest hardware, it's possible to process over 15,000,000 events per second with the fastest non-blocking <code>Dispatcher</code>. Other dispatchers are available to provide the developer with a range of choices from thread-pool style, long-running task execution to non-blocking, high-volume task dispatching. The GitHub repo is here <a href=\"https://github.com/reactor/reactor\"></a><a href=\"https://github.com/reactor/reactor\">https://github.com/reactor/reactor</a>.</p>\n"
                        + "\n"
                        + "<p>Reactor, as the name suggests, is heavily influenced by <a href=\"http://en.wikipedia.org/wiki/Reactor_pattern\" target=\"_blank\">the well-known Reactor design pattern</a>. But it is also influenced by other event-driven design practices, as well as several awesome JVM-based solutions that have been developed over the years. Reactor's goal is to condense these ideas and patterns into a simple and reusable foundation for making event-driven programming much easier.</p>\n"
                        + "\n"
                        + "<p>Reactor?s abstractions give the developer a set of tools to not just develop but <em>compose</em> applications in a way that more efficiently uses system resources--which is particularly important when running in the cloud--and reduce or eliminate the spaghetti of nested callbacks (aptly named <a href=\"http://callbackhell.com/\" target=\"_blank\">?callback hell?</a>) that has so far burdened most asynchronous applications.</p>\n"
                        + "\n"
                        + "<h3>\n"
                        + "<a name=\"what-is-reactor-good-for\" class=\"anchor\" href=\"#what-is-reactor-good-for\"><span class=\"octicon octicon-link\"></span></a>What is Reactor good for?</h3>";

        String expected =
                "<p>We?re pleased to announce that, after a long period of internal incubation, we?re releasing a foundational framework for asynchronous applications on the JVM which we?re calling <em>Reactor</em>. It provides abstractions for Java, Groovy and other JVM languages to make building event and data-driven applications easier. It?s also really fast. On modest hardware, it's possible to process over 15,000,000 events per second with the fastest non-blocking <code>Dispatcher</code>. Other dispatchers are available to provide the developer with a range of choices from thread-pool style, long-running task execution to non-blocking, high-volume task dispatching. The GitHub repo is here <a href=\"https://github.com/reactor/reactor\"></a><a href=\"https://github.com/reactor/reactor\">https://github.com/reactor/reactor</a>.</p>\n";

        assertThat(postSummary.forContent(content, 500), is(expected));
    }

}
