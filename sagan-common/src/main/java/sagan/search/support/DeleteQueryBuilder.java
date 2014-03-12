package sagan.search.support;

import java.util.List;

import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;

class DeleteQueryBuilder {

    public String unsupportedProjectEntriesQuery(String projectId, List<String> supportedVersions) {
        QueryBuilder query = QueryBuilders.matchAllQuery();

        OrFilterBuilder supportedVersionsFilter = matchSupportedVersions(supportedVersions);
        NotFilterBuilder notSupportedVersionFilter = new NotFilterBuilder(supportedVersionsFilter);

        TermFilterBuilder projectFilter = new TermFilterBuilder("projectId", projectId);

        AndFilterBuilder filter = new AndFilterBuilder(notSupportedVersionFilter, projectFilter);

        FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(query, filter);

        return filteredQuery.toString();
    }

    private OrFilterBuilder matchSupportedVersions(List<String> supportedVersions) {
        OrFilterBuilder orFilter = new OrFilterBuilder();
        for (String supportedVersion : supportedVersions) {
            orFilter.add(new TermFilterBuilder("version", supportedVersion));
        }
        return orFilter;
    }
}
