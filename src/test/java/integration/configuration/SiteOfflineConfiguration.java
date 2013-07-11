package integration.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainRequestBuilder;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.mlt.MoreLikeThisRequest;
import org.elasticsearch.action.mlt.MoreLikeThisRequestBuilder;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateRequestBuilder;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.suggest.SuggestRequest;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.mockito.Mockito.mock;

@Configuration
@Import({ApplicationConfiguration.class})
public class SiteOfflineConfiguration {

	public static final GettingStartedGuide GETTING_STARTED_GUIDE =
			new GettingStartedGuide("awesome-guide", "Awesome getting started guide that isn't helpful", "Related resources");

	@Primary
	@Bean
	public RestTemplate mockRestTemplate() {
		return mock(RestTemplate.class);
	}

	@Primary
	@Bean
	public GettingStartedService offlineGettingStartedService() {
		return new GettingStartedService() {
			@Override
			public GettingStartedGuide loadGuide(String guideId) {
				return GETTING_STARTED_GUIDE;
			}

			@Override
			public List<GuideRepo> listGuides() {
				ObjectMapper mapper = new ObjectMapper();
				try {
					String reposJson = "/org/springframework/site/domain/guides/springframework-meta.repos.offline.json";
					InputStream json = new ClassPathResource(reposJson, getClass()).getInputStream();
					return mapper.readValue(json, new TypeReference<List<GuideRepo>>() {
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public byte[] loadImage(String guideSlug, String imageName) {
				return new byte[0];
			}
		};
	}

	@Primary
	@Bean
	public Client elasticSearchClient() throws Exception {
		return new Client() {
			@Override
			public void close() {
			}

			@Override
			public AdminClient admin() {
				return null;
			}

			@Override
			public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> ActionFuture<Response> execute(Action<Request, Response, RequestBuilder> action, Request request) {
				return null;
			}

			@Override
			public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void execute(Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
			}

			@Override
			public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> RequestBuilder prepareExecute(Action<Request, Response, RequestBuilder> action) {
				return null;
			}

			@Override
			public ActionFuture<IndexResponse> index(IndexRequest request) {
				return null;
			}

			@Override
			public void index(IndexRequest request, ActionListener<IndexResponse> listener) {
			}

			@Override
			public IndexRequestBuilder prepareIndex() {
				return null;
			}

			@Override
			public ActionFuture<UpdateResponse> update(UpdateRequest request) {
				return null;
			}

			@Override
			public void update(UpdateRequest request, ActionListener<UpdateResponse> listener) {
			}

			@Override
			public UpdateRequestBuilder prepareUpdate() {
				return null;
			}

			@Override
			public UpdateRequestBuilder prepareUpdate(String index, String type, String id) {
				return null;
			}

			@Override
			public IndexRequestBuilder prepareIndex(String index, String type) {
				return null;
			}

			@Override
			public IndexRequestBuilder prepareIndex(String index, String type, @Nullable String id) {
				return null;
			}

			@Override
			public ActionFuture<DeleteResponse> delete(DeleteRequest request) {
				return null;
			}

			@Override
			public void delete(DeleteRequest request, ActionListener<DeleteResponse> listener) {
			}

			@Override
			public DeleteRequestBuilder prepareDelete() {
				return null;
			}

			@Override
			public DeleteRequestBuilder prepareDelete(String index, String type, String id) {
				return null;
			}

			@Override
			public ActionFuture<BulkResponse> bulk(BulkRequest request) {
				return null;
			}

			@Override
			public void bulk(BulkRequest request, ActionListener<BulkResponse> listener) {
			}

			@Override
			public BulkRequestBuilder prepareBulk() {
				return null;
			}

			@Override
			public ActionFuture<DeleteByQueryResponse> deleteByQuery(DeleteByQueryRequest request) {
				return null;
			}

			@Override
			public void deleteByQuery(DeleteByQueryRequest request, ActionListener<DeleteByQueryResponse> listener) {
			}

			@Override
			public DeleteByQueryRequestBuilder prepareDeleteByQuery(String... indices) {
				return null;
			}

			@Override
			public ActionFuture<GetResponse> get(GetRequest request) {
				return null;
			}

			@Override
			public void get(GetRequest request, ActionListener<GetResponse> listener) {
			}

			@Override
			public GetRequestBuilder prepareGet() {
				return null;
			}

			@Override
			public GetRequestBuilder prepareGet(String index, @Nullable String type, String id) {
				return null;
			}

			@Override
			public ActionFuture<MultiGetResponse> multiGet(MultiGetRequest request) {
				return null;
			}

			@Override
			public void multiGet(MultiGetRequest request, ActionListener<MultiGetResponse> listener) {
			}

			@Override
			public MultiGetRequestBuilder prepareMultiGet() {
				return null;
			}

			@Override
			public ActionFuture<CountResponse> count(CountRequest request) {
				return null;
			}

			@Override
			public void count(CountRequest request, ActionListener<CountResponse> listener) {
			}

			@Override
			public CountRequestBuilder prepareCount(String... indices) {
				return null;
			}

			@Override
			public ActionFuture<SuggestResponse> suggest(SuggestRequest request) {
				return null;
			}

			@Override
			public void suggest(SuggestRequest request, ActionListener<SuggestResponse> listener) {
			}

			@Override
			public SuggestRequestBuilder prepareSuggest(String... indices) {
				return null;
			}

			@Override
			public ActionFuture<SearchResponse> search(SearchRequest request) {
				return null;
			}

			@Override
			public void search(SearchRequest request, ActionListener<SearchResponse> listener) {
			}

			@Override
			public SearchRequestBuilder prepareSearch(String... indices) {
				return null;
			}

			@Override
			public ActionFuture<SearchResponse> searchScroll(SearchScrollRequest request) {
				return null;
			}

			@Override
			public void searchScroll(SearchScrollRequest request, ActionListener<SearchResponse> listener) {
			}

			@Override
			public SearchScrollRequestBuilder prepareSearchScroll(String scrollId) {
				return null;
			}

			@Override
			public ActionFuture<MultiSearchResponse> multiSearch(MultiSearchRequest request) {
				return null;
			}

			@Override
			public void multiSearch(MultiSearchRequest request, ActionListener<MultiSearchResponse> listener) {
			}

			@Override
			public MultiSearchRequestBuilder prepareMultiSearch() {
				return null;
			}

			@Override
			public ActionFuture<SearchResponse> moreLikeThis(MoreLikeThisRequest request) {
				return null;
			}

			@Override
			public void moreLikeThis(MoreLikeThisRequest request, ActionListener<SearchResponse> listener) {
			}

			@Override
			public MoreLikeThisRequestBuilder prepareMoreLikeThis(String index, String type, String id) {
				return null;
			}

			@Override
			public ActionFuture<PercolateResponse> percolate(PercolateRequest request) {
				return null;
			}

			@Override
			public void percolate(PercolateRequest request, ActionListener<PercolateResponse> listener) {
			}

			@Override
			public PercolateRequestBuilder preparePercolate(String index, String type) {
				return null;
			}

			@Override
			public ExplainRequestBuilder prepareExplain(String index, String type, String id) {
				return null;
			}

			@Override
			public ActionFuture<ExplainResponse> explain(ExplainRequest request) {
				return null;
			}

			@Override
			public void explain(ExplainRequest request, ActionListener<ExplainResponse> listener) {
			}
		};
	}

}
