package tools.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParamParser {
  public static Map<String, String> read(String queryString) {
    try {
      String[] queryStrings = queryString.split("&");

      return Arrays.stream(queryStrings)
          .map(q -> q.split("="))
          .collect(Collectors.toMap(queries -> queries[0], queries -> {
            if (queries[0].equals("email")) {
              return String.valueOf(queries[1]).replace("%40", "@");
            }
            return queries[1];
          }));
    } catch (Exception e) {
      return new HashMap<>();
    }
  }
}
