package me.cloudm1nd3.cloudchat.objects;

public final class FormatToken {
    public enum Type {
        LITERAL,
        ELEMENT,
        MESSAGE
    }

    private final Type type;
    private final String literal;
    private final ChatElement element;
    private final String previousCodes;

    private FormatToken(Type type, String literal, ChatElement element, String previousCodes) {
        this.type = type;
        this.literal = literal;
        this.element = element;
        this.previousCodes = previousCodes == null ? "" : previousCodes;
    }

    public static FormatToken literal(String text, String previousCodes) {
        return new FormatToken(Type.LITERAL, text, null, previousCodes);
    }

    public static FormatToken element(ChatElement element, String previousCodes) {
        return new FormatToken(Type.ELEMENT, null, element, previousCodes);
    }

    public static FormatToken message(String previousCodes) {
        return new FormatToken(Type.MESSAGE, null, null, previousCodes);
    }

    public Type getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    public ChatElement getElement() {
        return element;
    }

    public String getPreviousCode() {
        return previousCodes;
    }
}
