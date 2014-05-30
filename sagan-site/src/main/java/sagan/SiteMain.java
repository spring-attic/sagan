package sagan;

/**
 * The entry point for the Sagan web application.
 */
public class SiteMain {

    public static void main(String[] args) {
        new SaganApplication(SiteConfig.class).run(args);
    }
}
