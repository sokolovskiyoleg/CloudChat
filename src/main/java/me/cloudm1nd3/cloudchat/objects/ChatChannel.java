package me.cloudm1nd3.cloudchat.objects;

import org.bukkit.Sound;

public class ChatChannel {
    private final String name;
    private String speakPermission;
    private Sound messageSound;
    private final int cooldown;
    private String cooldownBypassPermission;
    private final String format;
    private int radius;
    private final String quickSymbol;


    public ChatChannel(String name, String speakPermission, Sound messageSound, int cooldown,
                       String cooldownBypassPermission, String format, int radius, String quickSymbol){
        this.name = name;
        this.speakPermission = speakPermission;
        this.messageSound = messageSound;
        this.cooldown = cooldown;
        this.cooldownBypassPermission = cooldownBypassPermission;
        this.format = format;
        this.radius = radius;
        this.quickSymbol = quickSymbol;
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

    public String getFormat(){
        return format;
    }
}
