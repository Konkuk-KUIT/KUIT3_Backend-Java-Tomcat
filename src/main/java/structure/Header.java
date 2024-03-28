package structure;

import java.util.HashMap;
import java.util.Map;

public class Header {

    public Header() {
    }

    Map<String, String> attributes = new HashMap<>();   // TODO: Better naming..? 모든 값을 enum으로 갔고 있긴 무리

    public void refineAttribute(String line) {
        String[] attribute = line.split(": ");
        ;

        attributes.put(attribute[0], attribute[1]);
    }

    public String parseAttributeValue(HeaderKey headerKey) {   // Exception 처리
        return attributes.get(headerKey.getHeaderKey());
    }

    public void addAttribute(HeaderKey key, String value) {
        attributes.put(key.getHeaderKey(), value);
    }

    public byte[] getFinalByteHeader() {
        StringBuilder ret = new StringBuilder();
        for (Map.Entry<String,String> attribute : attributes.entrySet()) {
            ret.append(attribute.getKey()).append(": ").append(attribute.getValue()).append("\r\n");
        }
        ret.append("\r\n");
        return ret.toString().getBytes();
    }
}