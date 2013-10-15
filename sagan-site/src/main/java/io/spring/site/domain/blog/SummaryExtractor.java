package io.spring.site.domain.blog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class SummaryExtractor {

    public String extract(String content, int maxLength) {
        Document document = Jsoup.parse(content);
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Element element : document.body().children()) {
            builder.append(element.outerHtml());
            builder.append("\n");
            count += element.text().length();
            if (count >= maxLength) break;
        }

        return builder.toString();
    }
}
