package me.cloudm1nd3.cloudchat.objects;

import net.kyori.adventure.text.event.ClickEvent;

import java.util.List;
import java.util.Locale;

public class ChatElement {
    private String name = null;
    private final String text;
    private List<String> hoverLines = null;
    private ClickEvent.Action<?> actionType = null;
    private String actionValue = null;

    public ChatElement(String text){
        this.text = text;
    }

    public ChatElement(String name, String text, List<String> hoverLines, String type, String value){
        this.name = name;
        this.text = text;
        this.hoverLines = hoverLines;
        if(type != null && value != null){
            actionType = parseAction(type);
            actionValue = value;
        } else {
            actionType = null;
            actionValue = null;
        }
    }

    private static ClickEvent.Action<?> parseAction(String type){
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "OPEN_URL" -> ClickEvent.Action.OPEN_URL;
            case "OPEN_FILE" -> ClickEvent.Action.OPEN_FILE;
            case "RUN_COMMAND" -> ClickEvent.Action.RUN_COMMAND;
            case "SUGGEST_COMMAND" -> ClickEvent.Action.SUGGEST_COMMAND;
            case "CHANGE_PAGE" -> ClickEvent.Action.CHANGE_PAGE;
            case "COPY_TO_CLIPBOARD" -> ClickEvent.Action.COPY_TO_CLIPBOARD;
            default -> throw new IllegalArgumentException("Unknown click event action type '" + type + "'. Valid values: OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD");
        };
    }

    public String getName(){
        return name;
    }

    public String getText(){
        return  text;
    }

    public List<String> getHoverLines(){
        return hoverLines;
    }

    public ClickEvent.Action<?> getActionType(){
        return actionType;
    }

    public String getActionValue(){
        return actionValue;
    }
}
