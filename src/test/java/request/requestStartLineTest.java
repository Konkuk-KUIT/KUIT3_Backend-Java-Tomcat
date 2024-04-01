package request;
import request.requestStartLine;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class requestStartLineTest {


    @Test
    void generateTest(){
        //given
        String requestString = "GET /index?name=kim&Id=chris&passWord=111 HTTP/1.1";
        BufferedReader br = new BufferedReader(new StringReader(requestString));
        //when
        requestStartLine reqStartLine = requestStartLine.from(br);

        //then
        assertEquals("GET", reqStartLine.getMethod());
        assertEquals("/index", reqStartLine.getPath());
        assertEquals("kim", reqStartLine.getQuery("name"));
        assertEquals("chris", reqStartLine.getQuery("Id"));
        assertEquals("111", reqStartLine.getQuery("passWord"));
        assertEquals("HTTP/1.1", reqStartLine.getHTTPversion());

    }

    @Test
    void queryNullTest(){
        //given
        String requestString = "GET /index?name=kim&Id=chris&passWord=111 HTTP/1.1";
        BufferedReader br = new BufferedReader(new StringReader(requestString));

        //when
        requestStartLine reqStartLine = requestStartLine.from(br);

        //then
        assertEquals("kim", reqStartLine.getQuery("name"));
        assertEquals("chris", reqStartLine.getQuery("Id"));
        assertEquals("111", reqStartLine.getQuery("passWord"));
        assertNull( reqStartLine.getQuery("iWantNullNotError"));

    }
}
