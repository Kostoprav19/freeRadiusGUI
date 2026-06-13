package lv.freeradiusgui.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AppConfigPlaceholderTest {

    private static final String SET_VAR = "APP_CONFIG_TEST_SET_VAR";
    private static final String MULTI_FIRST = "APP_CONFIG_TEST_MULTI_FIRST";
    private static final String MULTI_SECOND = "APP_CONFIG_TEST_MULTI_SECOND";
    private static final String NESTED_VAR = "APP_CONFIG_TEST_NESTED_VAR";

    @AfterEach
    public void clearSystemProperties() {
        System.clearProperty(SET_VAR);
        System.clearProperty(MULTI_FIRST);
        System.clearProperty(MULTI_SECOND);
        System.clearProperty(NESTED_VAR);
    }

    @Test
    public void resolvePlaceholders_returnsLiteralValueWhenNoPlaceholderExists() {
        assertEquals("plain-value", resolve("plain-value"));
    }

    @Test
    public void resolvePlaceholders_returnsFallbackWhenPropertyIsUnset() {
        assertEquals("fallback", resolve("${APP_CONFIG_TEST_UNSET_VAR:fallback}"));
    }

    @Test
    public void resolvePlaceholders_usesSystemPropertyWithoutDefault() {
        System.setProperty(SET_VAR, "resolved-value");

        assertEquals("resolved-value", resolve("${" + SET_VAR + "}"));
    }

    @Test
    public void resolvePlaceholders_prefersSystemPropertyOverDefault() {
        System.setProperty(SET_VAR, "resolved-value");

        assertEquals("resolved-value", resolve("${" + SET_VAR + ":fallback}"));
    }

    @Test
    public void resolvePlaceholders_returnsEmptyStringWhenUnsetAndNoDefaultProvided() {
        assertEquals("", resolve("${APP_CONFIG_TEST_UNSET_WITHOUT_DEFAULT}"));
    }

    @Test
    public void resolvePlaceholders_replacesMultiplePlaceholdersInSingleValue() {
        System.setProperty(MULTI_FIRST, "first-value");
        System.setProperty(MULTI_SECOND, "second-value");

        assertEquals(
                "jdbc:first-value://second-value/fallback",
                resolve(
                        "jdbc:${"
                                + MULTI_FIRST
                                + "}://${"
                                + MULTI_SECOND
                                + "}/${APP_CONFIG_TEST_UNSET_MULTI:fallback}"));
    }

    @Test
    public void resolvePlaceholders_keepsMalformedPlaceholderUnchanged() {
        assertEquals("${oops", resolve("${oops"));
    }

    @Test
    public void resolvePlaceholders_supportsJdbcUrlShapedDefault() {
        assertEquals(
                "jdbc:mysql://host:3306/db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                resolve(
                        "${DB_URL:jdbc:mysql://host:3306/db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}"));
    }

    @Test
    public void resolvePlaceholders_doesNotRescanSubstitutedText() {
        System.setProperty(NESTED_VAR, "${B:c}");

        assertEquals("${B:c}", resolve("${" + NESTED_VAR + ":fallback}"));
        assertEquals("${B:c}", resolve("${APP_CONFIG_TEST_UNSET_NESTED:${B:c}}"));
    }

    @Test
    public void resolvePlaceholders_supportsDottedAndHyphenatedSystemPropertyKeys() {
        System.setProperty("user.home-test", "/test/home");
        assertEquals("/test/home", resolve("${user.home-test:fallback}"));
        System.clearProperty("user.home-test");
    }

    private String resolve(String value) {
        try {
            AppConfig config = new AppConfig();
            Method resolver =
                    AppConfig.class.getDeclaredMethod("resolvePlaceholders", String.class);
            resolver.setAccessible(true);
            return (String) resolver.invoke(config, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
