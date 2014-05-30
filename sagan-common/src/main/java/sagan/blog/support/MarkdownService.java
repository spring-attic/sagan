package sagan.blog.support;

/**
 * Abstraction representing a service capable of translating markdown to HTML.
 */
interface MarkdownService {
    String renderToHtml(String markdownSource);
}
