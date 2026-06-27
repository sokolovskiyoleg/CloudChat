package me.cloudm1nd3.cloudchat.managers;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;


public class ChatPlayerManager {
    private static ChatPlayerManager instance;
    private final CloudChat plugin;
    private static HashMap<UUID, ChatPlayer> cachedPlayers = new HashMap<>();
    private static HashMap<UUID, ChatPlayer> onlinePlayers = new HashMap<>();


    private ChatPlayerManager(CloudChat plugin) {
        this.plugin = plugin;
    }

    public static void init(CloudChat plugin) {
        if (instance != null) {
            throw new IllegalStateException("ChatPlayerManager already initialized");
        }

        instance = new ChatPlayerManager(plugin);
    }

    public static ChatPlayerManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ChatPlayerManager not initialized");
        }

        return instance;
    }

    public void addPlayerToCache(ChatPlayer chatPlayer){
        cachedPlayers.put(chatPlayer.getUUID(), chatPlayer);
    }

    public void addOnlinePlayerToMap(ChatPlayer chatPlayer){
        onlinePlayers.put(chatPlayer.getUUID(), chatPlayer);
    }

    public void removeOnlinePlayerFromMap(ChatPlayer chatPlayer){
        onlinePlayers.remove(chatPlayer.getUUID());
    }

    public ChatPlayer getChatPlayer(UUID uuid){
        return cachedPlayers.get(uuid);
    }

    public ChatPlayer getChatPlayer(Player player){
        if(player == null) return null;
        UUID uuid = player.getUniqueId();
        return cachedPlayers.get(uuid);
    }

    public Collection<ChatPlayer> getAllPlayers(){
        return cachedPlayers.values();
    }

    public Collection<ChatPlayer> getOnlinePlayers(){
        return onlinePlayers.values();
    }
}
