package sagan.support;

import org.junit.rules.ExternalResource;

public class SetSystemProperty extends ExternalResource {
    public String originalValue;
    private String key;
    private String value;

    public SetSystemProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    protected void before() throws Throwable {
        originalValue = System.getProperty(key);
        System.setProperty(key, value);
    }

    @Override
    protected void after() {
        if (originalValue == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, originalValue);
        }
    }
}
