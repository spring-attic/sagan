package sagan.projects.support;

import sagan.projects.Project;
import sagan.support.github.GitHubClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller that handles requests from GitHub webhook set up at <a
 * href="https://github.com/spring-projects/gh-pages#readme">the shared gh-pages
 * repository</a> and notifies project leads to merge those new changes into their own
 * projects. This notification happens by adding a new GH Issue to each project under the
 * spring-projects org that has a gh-pages branch.
 *
 * The {token} path variable establishes a shared secret between the webhook and this
 * controller, ensuring that only GitHub can POST and thus create new issues in the
 * various Spring project repositories.
 */
@Controller
@RequestMapping("/webhook/gh-pages/{token}")
class GhPagesWebhookController {

    private static final Log logger = LogFactory.getLog(GhPagesWebhookController.class);

    private final ProjectMetadataService projectMetadataService;
    private final GitHub gitHub;
    private final String template;
    private final ObjectMapper objectMapper;

    @Value("${WEBHOOK_ACCESS_TOKEN:default}")
    private String accessToken;

    @Autowired
    public GhPagesWebhookController(ProjectMetadataService service, GitHub gitHub,
                                    ObjectMapper objectMapper) throws IOException {
        this.projectMetadataService = service;
        this.gitHub = gitHub;
        this.template = StreamUtils.copyToString(
                new ClassPathResource("notifications/gh-pages-updated.md").getInputStream(),
                Charset.defaultCharset());
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = POST, headers = "content-type=application/x-www-form-urlencoded")
    @ResponseBody
    public HttpEntity<String> processUpdate(@RequestParam String payload, @PathVariable String token)
            throws IOException {
        HttpHeaders headers = new HttpHeaders();
        if (!accessToken.equals(token)) {
            headers.set("Status", "403 Forbidden");
            return new HttpEntity<>("{ \"message\": \"Forbidden\" }\n", headers);
        }
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression spel = parser.parseExpression(template, new TemplateParserContext());

        Map<?, ?> push;
        try {
            push = this.objectMapper.readValue(payload, Map.class);
            logger.info("Received new webhook payload for push with head_commit message: "
                    + ((Map<?, ?>) push.get("head_commit")).get("message"));
        } catch (JsonParseException ex) {
            headers.set("Status", "400 Bad Request");
            return new HttpEntity<>("{ \"message\": \"Bad Request\" }\n", headers);
        }

        StringBuilder commits = new StringBuilder();
        for (Map<?, ?> commit : (List<Map<?, ?>>) push.get("commits")) {
            commits.append(format(" - %s (%s)\n", commit.get("message"), commit.get("id")));
        }
        Map<String, Object> root = new HashMap<>();
        root.put("push", push);
        root.put("commits", commits);
        projectMetadataService.getProjects().stream()
                .filter(project -> hasGhPagesBranch(project))
                .forEach(project -> createGitHubIssue(project, this.objectMapper, root, spel));
        headers.set("Status", "200 OK");
        return new HttpEntity<>("{ \"message\": \"Successfully processed update\" }\n", headers);
    }

    private boolean hasGhPagesBranch(Project project) {
        if (project.hasSite() && project.getSiteUrl().startsWith("http://projects.spring.io")) {
            String ghPagesBranchUrl = format("%s/repos/%s/%s/branches/gh-pages",
                    GitHubClient.API_URL_BASE, "spring-projects", project.getId());
            try {
                HttpHeaders headers = gitHub.restOperations().headForHeaders(ghPagesBranchUrl);
                return "200 OK".equals(headers.getFirst("Status"));
            } catch (Exception ex) {
                // RestTemplate call above logs at WARN level if anything goes wrong
            }
        }
        return false;
    }

    private void createGitHubIssue(Project project, ObjectMapper jsonMapper, Map<String, Object> root, Expression spel) {
        root.put("project", project);
        Map<String, String> newIssue = new HashMap<>();
        newIssue.put("title", "Please merge the latest changes to common gh-pages");
        newIssue.put("body", spel.getValue(root, String.class));
        String projectIssuesUrl =
                format("%s/repos/spring-projects/%s/issues", GitHubClient.API_URL_BASE, project.getId());
        try {
            URI newIssueUrl =
                    gitHub.restOperations().postForLocation(projectIssuesUrl,
                            jsonMapper.writeValueAsString(newIssue));
            logger.info("Notification of new gh-pages changes created at " + newIssueUrl);
        } catch (RuntimeException | JsonProcessingException ex) {
            logger.warn("Unable to POST new issue to " + projectIssuesUrl);
        }
    }

}
