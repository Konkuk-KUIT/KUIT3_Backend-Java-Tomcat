package request;
import org.junit.jupiter.api.Test;
import request.requestBody;
import java.io.BufferedReader;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class requestBodyTest {
    //given

    @Test
    void requestBodyTest(){

        String Header = "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\n" +
                "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                "Cache-Control: max-age=0\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 63\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Host: localhost\n" +
                "Origin: http://localhost\n" +
                "Referer: http://localhost/webapp/user/form.html\n" +
                "Sec-Fetch-Dest: document\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Sec-Fetch-Site: same-origin\n" +
                "Sec-Fetch-User: ?1\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36\n" +
                "sec-ch-ua: \"Google Chrome\";v=\"123\", \"Not:A-Brand\";v=\"8\", \"Chromium\";v=\"123\"\n" +
                "sec-ch-ua-mobile: ?0\n" +
                "sec-ch-ua-platform: \"Windows\"\n";
        String emptyLine = "\r\n";
        String Body = "userId=asdfasf&password=asdfaf&name=czxc&email=asdf2sasdf%40fda";

        BufferedReader br = new BufferedReader(new StringReader(Header+emptyLine+Body));

        //when
        requestHeader reqHead = requestHeader.from(br);
        requestBody reqBody = requestBody.from(br,reqHead);
        reqBody.printBodyQuery();
        //then
        assertEquals("asdfasf",reqBody.getBodyQuery("userId"));
        assertEquals("asdfaf",reqBody.getBodyQuery("password"));
        assertEquals("czxc",reqBody.getBodyQuery("name"));
        assertEquals("asdf2sasdf%40fda",reqBody.getBodyQuery("email"));
    }

}
