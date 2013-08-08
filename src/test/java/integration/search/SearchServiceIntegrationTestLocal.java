package integration.search;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.boot.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchResult;
import org.springframework.site.search.SearchResults;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.site.web.search.SearchEntryBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import utils.FreePortFinder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfiguration.class,
		SearchServiceIntegrationTestLocal.IntegrationTestElasticSearchConfiguration.class }, initializers = {
		ConfigFileApplicationContextInitializer.class,
		LoggingApplicationContextInitializer.class })
@DirtiesContext
public class SearchServiceIntegrationTestLocal {

	public static class IntegrationTestElasticSearchConfiguration {

		@Autowired
		private SearchService searchService;

		@Autowired
		private Client client;

		private String elasticSearchPort;

		@Bean
		public Client elasticSearchClient() throws Exception {
			NodeBuilder nodeBuilder = nodeBuilder().local(false);
			nodeBuilder.getSettings().put("network.host", "127.0.0.1");
			nodeBuilder.getSettings().put("http.port", getElasticSearchPort());
			nodeBuilder.getSettings().put("index.number_of_shards", "1");
			nodeBuilder.getSettings().put("index.number_of_replicas", "0");
			Client client = nodeBuilder.node().client();
			return client;
		}

		private String getElasticSearchPort() {
			if (this.elasticSearchPort == null) {
				this.elasticSearchPort = FreePortFinder.find() + "";
			}
			return this.elasticSearchPort;
		}

		@PostConstruct
		public void configureSearchService() {
			this.searchService.setUseRefresh(true);
		}

		@PreDestroy
		public void closeClient() throws Exception {
			this.client.close();
		}

		@Bean
		@Primary
		public JestClient jestClient() {
			JestClientFactory factory = new JestClientFactory();
			factory.setClientConfig(clientConfig());
			return factory.getObject();
		}

		private ClientConfig clientConfig() {
			LinkedHashSet<String> servers = new LinkedHashSet<String>();
			servers.add("http://localhost:" + getElasticSearchPort());
			ClientConfig clientConfig = new ClientConfig.Builder(servers).multiThreaded(
					true).build();
			return clientConfig;
		}
	}

	private final Pageable pageable = new PageRequest(0, 10);

	@Autowired
	private SearchService searchService;

	@Autowired
	private JestClient jestClient;

	private SearchEntry entry;

	@Before
	public void setUp() throws Exception {
		SearchIndexSetup searchIndexSetup = new SearchIndexSetup(this.jestClient);
		searchIndexSetup.deleteIndex();
		searchIndexSetup.createIndex();
	}

	private void indexSingleEntry() throws ParseException {
		this.entry = createSingleEntry("/some/path");
		this.searchService.saveToIndex(this.entry);
	}

	private SearchEntry createSingleEntry(String path) throws ParseException {
		return SearchEntryBuilder.entry().path(path).title("This week in Spring")
				.rawContent("raw content").summary("Html summary")
				.publishAt("2013-01-01 10:00").build();
	}

	private void assertThatSearchReturnsEntry(String query) {
		Page<SearchResult> searchEntries = this.searchService
				.search(query, this.pageable, Collections.<String>emptyList()).getPage();
		List<SearchResult> entries = searchEntries.getContent();
		assertThat(entries, not(empty()));
		assertThat(entries.get(0).getTitle(), is(equalTo(this.entry.getTitle())));
	}

	@Test
	public void testSearchAll() throws ParseException {
		indexSingleEntry();
		assertThatSearchReturnsEntry("");
	}

	@Test
	public void testSearchContent() throws ParseException {
		indexSingleEntry();
		assertThatSearchReturnsEntry("raw");
	}

	@Test
	public void testSearchTitle() throws ParseException {
		indexSingleEntry();
		assertThatSearchReturnsEntry("Spring");
	}

	@Test
	public void testSearchWithMultipleWords() throws ParseException {
		indexSingleEntry();
		assertThatSearchReturnsEntry("raw content");
	}

	@Test
	public void searchOnlyIncludesEntriesMatchingSearchTerm() throws ParseException {
		indexSingleEntry();

		SearchEntry secondEntry = SearchEntryBuilder.entry().path("/another/path")
				.title("Test").rawContent("Test body").build();

		this.searchService.saveToIndex(secondEntry);
		Page<SearchResult> searchEntries = this.searchService.search("content",
				this.pageable, Collections.<String>emptyList()).getPage();
		List<SearchResult> entries = searchEntries.getContent();
		assertThat(entries.size(), equalTo(1));
	}

	@Test
	public void searchPagesProperly() throws ParseException {
		SearchEntryBuilder builder = SearchEntryBuilder.entry().rawContent("raw content")
				.summary("Html summary").publishAt("2013-01-01 10:00");

		SearchEntry entry1 = builder.path("item1").title("Item 1").build();
		this.searchService.saveToIndex(entry1);

		SearchEntry entry2 = builder.path("item2").title("Item 2").build();
		this.searchService.saveToIndex(entry2);

		Pageable page1 = new PageRequest(0, 1);
		Page<SearchResult> searchEntries1 = this.searchService.search("content", page1, Collections.<String>emptyList()).getPage();
		assertThat(searchEntries1.getContent().get(0).getId(), equalTo(entry1.getId()));

		Pageable page2 = new PageRequest(1, 1);
		Page<SearchResult> searchEntries2 = this.searchService.search("content", page2, Collections.<String>emptyList()).getPage();
		assertThat(searchEntries2.getContent().get(0).getId(), equalTo(entry2.getId()));
	}

	@Test
	public void searchQueryReportsPageTotals() throws Exception {
		SearchEntryBuilder builder = SearchEntryBuilder.entry().rawContent("raw content")
				.summary("Html summary").publishAt("2013-01-01 10:00");

		for (int i = 0; i < 25; i++) {
			SearchEntry entry = builder.path("item" + i).title("Item " + i).build();
			this.searchService.saveToIndex(entry);
		}

		int page = 1;
		Pageable pageable = new PageRequest(page, 10);
		Page<SearchResult> searchEntries = this.searchService.search("", pageable, Collections.<String>emptyList()).getPage();
		assertThat(searchEntries.getContent().size(), equalTo(10));
		assertThat(searchEntries.getTotalPages(), equalTo(3));
		assertThat(searchEntries.getNumber(), equalTo(page));
	}

	@Test
	public void searchThatReturnsNoResultsIsEmpty() throws ParseException {
		indexSingleEntry();
		Pageable page = new PageRequest(0, 10);
		Page<SearchResult> searchEntries = this.searchService.search(
				"somethingthatwillneverappearsupercalifragilousIcantspelltherest", page, Collections.<String>emptyList()).getPage();
		assertThat(searchEntries.getContent().size(), equalTo(0));
		assertThat(searchEntries.getTotalPages(), equalTo(0));
	}

	@Test
	public void searchByCamelCaseTerms() throws ParseException {
		this.entry = SearchEntryBuilder.entry().path("http://example.com")
				.title("My Entry").rawContent("SomeCamelCaseThing is here")
				.summary("Html summary").publishAt("2013-01-01 10:00").build();
		this.searchService.saveToIndex(this.entry);

		assertThatSearchReturnsEntry("Camel");
	}

	@Test
	public void searchisCaseInsensitive() throws ParseException {
		this.entry = SearchEntryBuilder.entry().path("http://example.com")
				.title("My Entry").rawContent("SomeCamelCaseThing is here")
				.summary("Html summary").publishAt("2013-01-01 10:00").build();
		this.searchService.saveToIndex(this.entry);

		assertThatSearchReturnsEntry("camel");
	}

	@Test
	public void searchMatchesPartialWords() throws ParseException {
		this.entry = SearchEntryBuilder.entry().path("http://example.com")
				.title("My Entry").rawContent("BlogExporter is here")
				.summary("Html summary").publishAt("2013-01-01 10:00").build();
		this.searchService.saveToIndex(this.entry);

		assertThatSearchReturnsEntry("export");
	}

	@Test
	public void boostsTitleMatchesOverContent() throws ParseException {
		SearchEntry entryContent = SearchEntryBuilder.entry()
				.path("http://example.com/content").title("a title")
				.rawContent("application is in the content").summary("Html summary")
				.publishAt("2013-01-01 10:00").build();

		this.searchService.saveToIndex(entryContent);

		SearchEntry entryTitle = SearchEntryBuilder.entry()
				.path("http://example.com/title").title("application is in the title")
				.rawContent("some content").summary("Html summary")
				.publishAt("2013-01-01 10:00").build();

		this.searchService.saveToIndex(entryTitle);

		List<SearchResult> results = this.searchService.search("application",
				this.pageable, Collections.<String>emptyList()).getPage().getContent();
		assertThat(results.get(0).getId(), is(entryTitle.getId()));
		assertThat(results.get(1).getId(), is(entryContent.getId()));
	}

	@Test
	public void boostsCurrentVersionEntries() throws ParseException {
		SearchEntry notCurrent = SearchEntryBuilder.entry()
				.path("http://example.com/content")
				.title("a title")
				.rawContent("application is in the content")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.build();

		searchService.saveToIndex(notCurrent);

		SearchEntry current = SearchEntryBuilder.entry()
				.path("http://example.com/another_one")
				.title("a title")
				.rawContent("application is in the content")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.build();

		searchService.saveToIndex(current);

		List<SearchResult> results = searchService.search("application", pageable, Collections.<String>emptyList()).getPage().getContent();
		assertThat(results.get(0).getId(), is(current.getId()));
		assertThat(results.get(1).getId(), is(notCurrent.getId()));
	}

	@Test
	public void facets() throws ParseException {
		SearchEntry gsg = SearchEntryBuilder.entry()
				.path("http://example.com/gsg/some")
				.title("a title")
				.rawContent("some guide")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Guides")
				.facetPath("Guides/Getting Started")
				.notCurrent()
				.build();

		searchService.saveToIndex(gsg);

		SearchEntry gsg2 = SearchEntryBuilder.entry()
				.path("http://example.com/gsg/another")
				.title("a title")
				.rawContent("another guide")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Guides")
				.facetPath("Guides/Getting Started")
				.notCurrent()
				.build();

		searchService.saveToIndex(gsg2);

		SearchEntry tutorial = SearchEntryBuilder.entry()
				.path("http://example.com/tutorial")
				.title("a title")
				.rawContent("a tutorial")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Guides")
				.facetPath("Guides/Tutorials")
				.build();

		searchService.saveToIndex(tutorial);

		SearchEntry blog = SearchEntryBuilder.entry()
				.path("http://example.com/blog")
				.title("a title")
				.rawContent("a blog post")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Blog")
				.facetPath("Blog/Engineering")
				.build();

		searchService.saveToIndex(blog);

		SearchResults searchResults = searchService.search("title", pageable, Collections.<String>emptyList());

		List<SearchResult> content = searchResults.getPage().getContent();
		assertThat(content.size(), equalTo(4));
		assertThat(searchResults.getFacets().size(), equalTo(2));
		assertThat(searchResults.getFacets().get(0).getName(), equalTo("Blog"));
		assertThat(searchResults.getFacets().get(1).getName(), equalTo("Guides"));
	}

	@Test
	public void filterByFacets() throws ParseException {
		SearchEntry gsg = SearchEntryBuilder.entry()
				.path("http://example.com/gsg/some")
				.title("a title")
				.rawContent("some guide")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Guides")
				.facetPath("Guides/Getting Started")
				.notCurrent()
				.build();

		searchService.saveToIndex(gsg);

		SearchEntry gsg2 = SearchEntryBuilder.entry()
				.path("http://example.com/gsg/another")
				.title("a title")
				.rawContent("another guide")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Guides")
				.facetPath("Guides/Getting Started")
				.notCurrent()
				.build();

		searchService.saveToIndex(gsg2);

		SearchEntry tutorial = SearchEntryBuilder.entry()
				.path("http://example.com/tutorial")
				.title("a title")
				.rawContent("a tutorial")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Guides")
				.facetPath("Guides/Tutorials")
				.build();

		searchService.saveToIndex(tutorial);

		SearchEntry blog = SearchEntryBuilder.entry()
				.path("http://example.com/blog")
				.title("a title")
				.rawContent("a blog post")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.facetPath("Blog")
				.facetPath("Blog/Engineering")
				.build();

		searchService.saveToIndex(blog);

		SearchResults searchResults = searchService.search("title", pageable, Arrays.asList("Guides"));

		List<SearchResult> content = searchResults.getPage().getContent();
		assertThat(content.size(), equalTo(3));
		assertThat(searchResults.getFacets().size(), equalTo(1));
		assertThat(searchResults.getFacets().get(0).getName(), equalTo("Guides"));
	}

	@Test
	public void returnsApiDocSearchResult() throws ParseException {
		SearchEntry apiDoc = SearchEntryBuilder.entry()
				.path("http://example.com/content")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.type("apiDoc")
				.build();

		searchService.saveToIndex(apiDoc);

		List<SearchResult> results = searchService.search("ApplicationContext", pageable, Collections.<String>emptyList()).getPage().getContent();
		assertThat(results.get(0).getId(), is(apiDoc.getId()));
		assertThat(results.get(0).getSummary(), is("class level description"));
		assertThat(results.get(0).getHighlight(), containsString("Application"));
		assertThat(results.get(0).getHighlight(), containsString("Context"));
		assertThat(results.get(0).getOriginalSearchTerm(), equalTo("ApplicationContext"));
	}


	@Test
	public void deleteOldApiDocs() throws ParseException {
		SearchEntry oldApiDoc1 = SearchEntryBuilder.entry()
				.path("http://example.com/content1")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("1.2.3.RELEASE")
				.notCurrent()
				.projectId("project id to delete")
				.type("apiDoc")
				.build();

		searchService.saveToIndex(oldApiDoc1);

		SearchEntry oldApiDoc2 = SearchEntryBuilder.entry()
				.path("http://example.com/content2")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("1.5.3.M1")
				.notCurrent()
				.projectId("project id to delete")
				.type("apiDoc")
				.build();

		searchService.saveToIndex(oldApiDoc2);

		SearchEntry newApiDoc1 = SearchEntryBuilder.entry()
				.path("http://example.com/content3")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("2.0.0.RELEASE")
				.notCurrent()
				.projectId("project id to delete")
				.type("apiDoc")
				.build();

		searchService.saveToIndex(newApiDoc1);

		SearchEntry newApiDoc2 = SearchEntryBuilder.entry()
				.path("http://example.com/content4")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("2.1.0.M1")
				.notCurrent()
				.projectId("project id to delete")
				.type("apiDoc")
				.build();

		searchService.saveToIndex(newApiDoc2);

		SearchEntry nonApiDoc = SearchEntryBuilder.entry()
				.path("http://example.com/content5")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.type("site")
				.build();

		searchService.saveToIndex(nonApiDoc);

		SearchEntry otherApiDoc = SearchEntryBuilder.entry()
				.path("http://example.com/content6")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("not id to delete")
				.version("3.4.5.RELEASE")
				.type("apiDoc")
				.build();

		searchService.saveToIndex(otherApiDoc);

		searchService.removeOldProjectEntriesFromIndex("project id to delete", Arrays.asList("2.0.0.RELEASE", "2.1.0.M1"));

		List<SearchResult> results = searchService.search("ApplicationContext", pageable, Collections.<String>emptyList()).getPage().getContent();
		assertThat(results.size(), equalTo(4));
	}

	@Test
	public void deleteOldReferenceDocs() throws ParseException {
		SearchEntry oldReferenceDoc1 = SearchEntryBuilder.entry()
				.path("http://example.com/content1")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("1.2.3.RELEASE")
				.notCurrent()
				.projectId("project id to delete")
				.type("site")
				.build();

		searchService.saveToIndex(oldReferenceDoc1);

		SearchEntry oldReferenceDoc2 = SearchEntryBuilder.entry()
				.path("http://example.com/content2")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("1.5.3.M1")
				.notCurrent()
				.projectId("project id to delete")
				.type("site")
				.build();

		searchService.saveToIndex(oldReferenceDoc2);

		SearchEntry newReferenceDoc1 = SearchEntryBuilder.entry()
				.path("http://example.com/content3")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("2.0.0.RELEASE")
				.notCurrent()
				.projectId("project id to delete")
				.type("site")
				.build();

		searchService.saveToIndex(newReferenceDoc1);

		SearchEntry newReferenceDoc2 = SearchEntryBuilder.entry()
				.path("http://example.com/content4")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.version("2.1.0.M1")
				.notCurrent()
				.projectId("project id to delete")
				.type("site")
				.build();

		searchService.saveToIndex(newReferenceDoc2);

		SearchEntry nonReferenceDoc = SearchEntryBuilder.entry()
				.path("http://example.com/content5")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.type("site")
				.build();

		searchService.saveToIndex(nonReferenceDoc);

		SearchEntry othersite = SearchEntryBuilder.entry()
				.path("http://example.com/content6")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("not id to delete")
				.version("3.4.5.RELEASE")
				.type("site")
				.build();

		searchService.saveToIndex(othersite);

		searchService.removeOldProjectEntriesFromIndex("project id to delete", Arrays.asList("2.0.0.RELEASE", "2.1.0.M1"));

		List<SearchResult> results = searchService.search("ApplicationContext", pageable, Collections.<String>emptyList()).getPage().getContent();
		assertThat(results.size(), equalTo(4));
	}

	@Test
	public void testSearchTwoSpecificProjects_And_Documentation() throws Exception {
		SearchEntry searchedRefDoc = SearchEntryBuilder.entry()
				.path("http://example.com/framework/refDoc")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("spring-framework")
				.version("3.4.5.RELEASE")
				.type("site")
				.facetPath("Documentation")
				.facetPath("Documentation/Reference")
				.facetPath("Projects")
				.facetPath("Projects/SpringFramework")
				.facetPath("Projects/SpringFramework/3.4.5.RELEASE")
				.build();

		searchService.saveToIndex(searchedRefDoc);

		SearchEntry notFoundRefDoc = SearchEntryBuilder.entry()
				.path("http://example.com/framework/refDocNotFound")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("spring-framework")
				.version("3.4.5.NOT_FOUND")
				.type("site")
				.facetPath("Documentation")
				.facetPath("Documentation/Reference")
				.facetPath("Projects")
				.facetPath("Projects/SpringFramework")
				.facetPath("Projects/SpringFramework/3.4.5.NOT_FOUND")
				.build();

		searchService.saveToIndex(notFoundRefDoc);

		SearchEntry apiDoc = SearchEntryBuilder.entry()
				.path("http://example.com/framework/apiDoc")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("spring-framework")
				.version("3.4.5.RELEASE")
				.type("apiDoc")
				.facetPath("Documentation")
				.facetPath("Documentation/Api")
				.facetPath("Projects")
				.facetPath("Projects/SpringFramework")
				.facetPath("Projects/SpringFramework/3.4.5.RELEASE")
				.build();

		searchService.saveToIndex(apiDoc);

		SearchEntry projectNotFound = SearchEntryBuilder.entry()
				.path("http://example.com/not_found/refDocNotFound")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("spring-not-found")
				.version("3.4.5.RELEASE")
				.type("site")
				.facetPath("Documentation")
				.facetPath("Documentation/Reference")
				.facetPath("Projects")
				.facetPath("Projects/SpringNotFound")
				.facetPath("Projects/SpringNotFound/3.4.5.RELEASE")
				.build();

		searchService.saveToIndex(projectNotFound);

		SearchEntry foundSecurityApiDoc = SearchEntryBuilder.entry()
				.path("http://example.com/security/refDoc/3.5")
				.title("ApplicationContext")
				.rawContent("This is an ref doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("spring-security")
				.version("3.5.RELEASE")
				.type("site")
				.facetPath("Documentation")
				.facetPath("Documentation/Reference")
				.facetPath("Projects")
				.facetPath("Projects/SpringSecurity")
				.facetPath("Projects/SpringSecurity/3.5.RELEASE")
				.build();

		searchService.saveToIndex(foundSecurityApiDoc);

		SearchEntry otherFoundSecurityApiDoc = SearchEntryBuilder.entry()
				.path("http://example.com/security/apiDoc/3.6")
				.title("ApplicationContext")
				.rawContent("This is an api doc for ApplicationContext.")
				.summary("class level description")
				.publishAt("2013-01-01 10:00")
				.notCurrent()
				.projectId("spring-security")
				.version("3.6.RELEASE")
				.type("apiDoc")
				.facetPath("Documentation")
				.facetPath("Documentation/Api")
				.facetPath("Projects")
				.facetPath("Projects/SpringSecurity")
				.facetPath("Projects/SpringSecurity/3.6.RELEASE")
				.build();

		searchService.saveToIndex(otherFoundSecurityApiDoc);

		List<String> facetPathFilters = new ArrayList<>();
		facetPathFilters.add("Documentation/Api");
		facetPathFilters.add("Documentation/Reference");
		facetPathFilters.add("Projects/SpringSecurity");
		facetPathFilters.add("Projects/SpringFramework/3.4.5.RELEASE");

		List<SearchResult> results = searchService.search("ApplicationContext", pageable, facetPathFilters).getPage().getContent();

		assertThat(results.size(), equalTo(4));
		assertThat(results.get(0).getPath(), equalTo("http://example.com/framework/refDoc"));
		assertThat(results.get(1).getPath(), equalTo("http://example.com/framework/apiDoc"));
		assertThat(results.get(2).getPath(), equalTo("http://example.com/security/refDoc/3.5"));
		assertThat(results.get(3).getPath(), equalTo("http://example.com/security/apiDoc/3.6"));


		facetPathFilters = new ArrayList<>();
		facetPathFilters.add("Projects");

		results = searchService.search("ApplicationContext", pageable, facetPathFilters).getPage().getContent();

		assertThat(results.size(), equalTo(6));
	}

}
