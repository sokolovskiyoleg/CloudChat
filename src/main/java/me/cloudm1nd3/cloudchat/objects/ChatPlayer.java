package me.cloudm1nd3.cloudchat.objects;

import java.util.UUID;

public class ChatPlayer {
    private UUID uuid;
    private String name;
    private boolean hiddenChat;


    public ChatPlayer(UUID uuid, String name, boolean hiddenChat){
        this.uuid = uuid;
        this. name = name;
        this.hiddenChat = hiddenChat;
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public boolean getHiddenChat(){ return hiddenChat; }

    public void setHiddenChat(boolean hiddenChat){
        this.hiddenChat = hiddenChat;
    }

}
