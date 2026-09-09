package me.cloudm1nd3.cloudchat.objects;

import org.bukkit.Sound;

import java.util.List;

public class ChatChannel {
    private final String name;
    private String speakPermission;
    private Sound messageSound;
    private final int cooldown;
    private String cooldownBypassPermission;
    private final List<FormatToken> formatTokens;
    private int radius;
    private final String quickSymbol;

    public ChatChannel(String name, String speakPermission, Sound messageSound, int cooldown,
                       String cooldownBypassPermission, List<FormatToken> formatTokens, int radius, String quickSymbol){
        this.name = name;
        this.speakPermission = speakPermission;
        this.messageSound = messageSound;
        this.cooldown = cooldown;
        this.cooldownBypassPermission = cooldownBypassPermission;
        this.radius = radius;
        this.quickSymbol = quickSymbol;
        this.formatTokens = formatTokens;
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

    public List<FormatToken> getFormat(){
        return formatTokens;
    }

    public int getRadius(){
        return radius;
    }
}
