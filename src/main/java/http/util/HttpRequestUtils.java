package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    //GET 방식과 POST 방식은 request 형식에 차이가 있음
    //GET 메서드 : GET method queryString : userId=1234&password=1234&name=%EB%B0%95%EC%A2%85%EC%9D%B5&email=apap%40naver.com
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
