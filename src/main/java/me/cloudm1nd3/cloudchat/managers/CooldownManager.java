package me.cloudm1nd3.cloudchat.managers;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private static CooldownManager instance;

    private final HashMap<String, HashMap<UUID, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {}

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException("CooldownManager already initialized");
        }
        instance = new CooldownManager();
    }

    public static CooldownManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CooldownManager not initialized");
        }
        return instance;
    }

    public void setCooldown(String channelName, ChatPlayer chatPlayer, int time){
        HashMap<UUID, Long> channelCooldowns = cooldowns.computeIfAbsent(channelName, entry -> new HashMap<>());

        Long cooldownEnd = System.currentTimeMillis() + time * 1000L;
        channelCooldowns.put(chatPlayer.getUUID(), cooldownEnd);
    }

    public boolean isOnCooldown(String channelName, ChatPlayer chatPlayer){
        HashMap<UUID, Long> channelCooldowns = cooldowns.get(channelName);
        if(channelCooldowns == null){
            return false;
        }
        Long playerCooldown = channelCooldowns.get(chatPlayer.getUUID());
        if(playerCooldown == null){
            return false;
        }
        return System.currentTimeMillis() < playerCooldown;
    }
}
