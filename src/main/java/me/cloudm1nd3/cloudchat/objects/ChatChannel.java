package me.cloudm1nd3.cloudchat.objects;

import org.bukkit.Sound;

import java.util.List;

public class ChatChannel {
    private final String name;
    private String speakPermission;
    private Sound messageSound;
    private final int cooldown;
    private String cooldownBypassPermission;
    private final String prefix;
    private final List<ChatElement> format;
    private int radius;
    private final String quickSymbol;

    public ChatChannel(String name, String speakPermission, Sound messageSound, int cooldown,
                       String cooldownBypassPermission, String prefix, List<ChatElement> format, int radius, String quickSymbol){
        this.name = name;
        this.speakPermission = speakPermission;
        this.messageSound = messageSound;
        this.cooldown = cooldown;
        this.cooldownBypassPermission = cooldownBypassPermission;
        this.prefix = prefix;
        this.radius = radius;
        this.quickSymbol = quickSymbol;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getQuickSymbol() {
        return quickSymbol;
    }

    public int getCooldown(){
        return cooldown;
    }

    public List<ChatElement> getFormat(){
        return format;
    }

    public String getPrefix(){
        return prefix;
    }

    public int getRadius(){
        return radius;
    }



}
