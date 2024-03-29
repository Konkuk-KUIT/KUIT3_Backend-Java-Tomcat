package tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeaderParser {
  public static Map<String, String> read(BufferedReader br) {
    Map<String, String> header = new HashMap<>();
    String curLine;
    try {
      while ((curLine = br.readLine()) != null) {
        if (curLine.equals("")) {
          break;
        }
        String[] contents = curLine.split(": ");
        header.put(contents[0], contents[1]);
      }
      return header;
    } catch (IOException e) {
      return new HashMap<>();
    }
  }
}
