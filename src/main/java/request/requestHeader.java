package request;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class requestHeader {
    private static final Logger log = Logger.getLogger(requestHeader.class.getName());
    private Map<String,String> requestHeader;

    private requestHeader(BufferedReader br){
        requestHeader = parseHeader(br);
    }

    public static requestHeader from(BufferedReader br){
        return new requestHeader(br);
    }

    public String getHeader(String key){
        if(containsKeyHeader(key)) {
            return requestHeader.get(key);
        }else{
            return null;
        }
    }
    public Map<String,String> getAllHeader(){
        return requestHeader;
    }
    public boolean containsKeyHeader(String key){
        return requestHeader.containsKey(key);
    }


    private static Map<String,String> parseHeader(BufferedReader br/*, Map<String,String> reqHeader*/){
        String line=null;
        String[] pair;
        Map<String,String> reqHeader= new HashMap<String,String>();
        try {
            while((line = br.readLine()) != null && !line.isEmpty() ){
                //System.out.println("["+line+"]");
                pair = line.split(":");
                reqHeader.put(pair[0].trim(),pair[1].trim());
                //System.out.println("get header ["+pair[0]+":"+pair[1]+"]");
            }
        }catch(Exception e){
            System.out.println("getHeader error : "+ e);
            log.log(Level.SEVERE,e.getMessage());
        }
        System.out.println("get header end");
        return reqHeader;
    }
    public void printHeader(){
        System.out.println("req Header----");
        requestHeader.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        System.out.println("req Header----end");
    }
}
