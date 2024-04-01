package request;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class requestHeaderTest {


    @Test
    void requestHeaderTest(){
        //given
        String reqHeader = "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                "Connection: keep-alive\n" +
                "Host: localhost\n" +
                "Referer: http://localhost/webapp/user/login.html\n" +
                "Sec-Fetch-Dest: document\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Sec-Fetch-Site: same-origin\n" +
                "Sec-Fetch-User: ?1\n"+
                "Cookies: loged-in\n";
        BufferedReader br = new BufferedReader(new StringReader(reqHeader));

        //when
        requestHeader reqHead = requestHeader.from(br);


        //then
        reqHead.printHeader();
        assertEquals("localhost", reqHead.getHeader("Host"));
        assertEquals("loged-in", reqHead.getHeader("Cookies"));
        assertEquals("document", reqHead.getHeader("Sec-Fetch-Dest"));


    }

    @Test
    void requestHeaderNullTest(){
        //given
        String reqHeader = "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                "Connection: keep-alive\n" +
                "Host: localhost\n" +
                "Referer: http://localhost/webapp/user/login.html\n" +
                "Sec-Fetch-Dest: document\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Sec-Fetch-Site: same-origin\n" +
                "Sec-Fetch-User: ?1\n"+
                "Cookies: loged-in\n";
        BufferedReader br = new BufferedReader(new StringReader(reqHeader));

        //when
        requestHeader reqHead = requestHeader.from(br);


        //then
        reqHead.printHeader();
        assertNull(reqHead.getHeader("iRealyWantNullNotErrorPlz"));


    }
}
