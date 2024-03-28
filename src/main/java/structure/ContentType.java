package structure;

public enum ContentType {
    HTML("text/html;charset=utf-8");

    private final String typeValue;

    ContentType(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeValue() {
        return typeValue;
    }
}
