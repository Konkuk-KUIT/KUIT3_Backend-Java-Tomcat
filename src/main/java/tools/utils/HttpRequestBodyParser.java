package tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBodyParser {

  public static Map<String, String> read(BufferedReader br, String contentLength) throws IOException {
    Map<String, String> body = new HashMap<>();
    if (contentLength == null) {
      body.put("Content-Length", "0");
      return body;
    }

    int length = Integer.parseInt(contentLength);
    char[] bytes = new char[length];
    br.read(bytes, 0, length);
    return QueryParamParser.read(String.copyValueOf(bytes));
  }
}
