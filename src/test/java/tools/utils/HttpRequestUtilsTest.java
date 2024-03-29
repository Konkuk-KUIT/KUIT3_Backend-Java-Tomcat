package tools.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class QueryParamParserTest {

    @Test
    void parseQuery() {
        Map<String, String> queryParameter = QueryParamParser.read("userId=1");
        assertEquals("1", queryParameter.get("userId"));
    }

    @Test
    void parseQueryMore() {
        Map<String, String> queryParameter = QueryParamParser.read("userId=1&password=1");
        assertEquals("1", queryParameter.get("userId"));
        assertEquals("1", queryParameter.get("password"));
    }

    @Test
    void parseQueryZero() {
        Map<String, String> queryParameter = QueryParamParser.read("");
    }
}