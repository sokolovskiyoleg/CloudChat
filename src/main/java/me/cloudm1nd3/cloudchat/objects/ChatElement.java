package me.cloudm1nd3.cloudchat.objects;

import net.kyori.adventure.text.event.ClickEvent;

import java.util.ArrayList;
import java.util.List;

public class ChatElement {
    private String name = null;
    private final String text;
    private List<String> hoverLines = null;
    private ClickEvent.Action actionType = null;
    private String actionValue = null;

    public ChatElement(String text){
        this.text = text;
    }

    public ChatElement(String name, String text, List<String> hoverLines, String type, String value){
        this.name = name;
        this.text = text;
        this.hoverLines = hoverLines;
        if(type != null && value != null){
            actionType = ClickEvent.Action.valueOf(type);
            actionValue = value;
        } else {
            actionType = null;
            actionValue = null;
        }
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

    public ClickEvent.Action getActionType(){
        return actionType;
    }

    public String getActionValue(){
        return actionValue;
    }
}
