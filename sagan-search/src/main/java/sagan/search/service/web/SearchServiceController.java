package sagan.search.service.web;

import sagan.search.service.SearchQuery;
import sagan.search.service.SearchResults;
import sagan.search.service.SearchService;
import sagan.search.types.SearchEntry;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.UrlPathHelper;

@RestController
public class SearchServiceController {

    private final SearchService service;

    private UrlPathHelper helper = new UrlPathHelper();

    @Autowired
    public SearchServiceController(SearchService service) {
        this.service = service;
        this.helper.setAlwaysUseFullPath(true);
    }

    @DeleteMapping("/index/{type}/**")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void remove(@PathVariable String type, ServletWebRequest request) {
        service.removeFromIndex(new GenericSearchEntry(type, getPath(request, type)));
    }

    @PostMapping("/index")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody GenericSearchEntry entry) {
        service.saveToIndex(entry);
    }

    @DeleteMapping("/projects/{project}/**")
    @ResponseStatus(HttpStatus.CREATED)
    public void delete(@PathVariable String project, ServletWebRequest request) {
        String version = getPath(request, project);
        service.removeOldProjectEntriesFromIndex(project, Arrays.asList(version));
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.CREATED)
    public SearchResults search(@RequestBody SearchQuery query, Pageable pageable) {
        return service.search(query.getTerm(), pageable, query.getFilter());
    }

    private String getPath(ServletWebRequest request, String type) {
        String stem = "/" + type + "/";
        String path = this.helper.getPathWithinApplication(request.getRequest());
        path = path.substring(path.indexOf(stem) + stem.length());
        return path;
    }

    static class GenericSearchEntry extends SearchEntry {

        private String type;
        
        GenericSearchEntry() {
        }

        public GenericSearchEntry(String type, String path) {
            this.type = type;
            setPath(path);
        }

        @Override
        public String getType() {
            return type;
        }

    }
}
