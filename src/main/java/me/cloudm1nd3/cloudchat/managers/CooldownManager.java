package me.cloudm1nd3.cloudchat.managers;

import me.cloudm1nd3.cloudchat.objects.ChatChannel;
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

    public void setCooldown(ChatChannel chatChannel, ChatPlayer chatPlayer){
        String channelName = chatChannel.getName();
        HashMap<UUID, Long> channelCooldowns = cooldowns.computeIfAbsent(channelName, entry -> new HashMap<>());

        Long cooldownEnd = System.currentTimeMillis() + chatChannel.getCooldown() * 1000L;
        channelCooldowns.put(chatPlayer.getUUID(), cooldownEnd);
    }

    public long getCooldown(ChatChannel chatChannel, ChatPlayer chatPlayer){
        String channelName = chatChannel.getName();
        HashMap<UUID, Long> channelCooldowns = cooldowns.get(channelName);
        if(channelCooldowns == null){
            return 0;
        }
        Long playerCooldown = channelCooldowns.get(chatPlayer.getUUID());
        if(playerCooldown == null){
            return 0;
        }
        return playerCooldown - System.currentTimeMillis();
    }

}
