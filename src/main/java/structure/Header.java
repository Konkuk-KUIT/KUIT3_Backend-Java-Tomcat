package structure;

import java.util.HashMap;
import java.util.Map;

public class Header {

    public Header() {
    }

    Map<String ,String> attributes = new HashMap<>();   // TODO: Better naming..?

    public void addAttribute(String line) {
        String[] attribute = line.split(": ");;

        attributes.put(attribute[0], attribute[1]);
    }

    public String parseAttributeValue(String key) {   // Exception 처리
        return attributes.get(key);
    }
}
