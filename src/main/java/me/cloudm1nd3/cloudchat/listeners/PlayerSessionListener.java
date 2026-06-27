package me.cloudm1nd3.cloudchat.listeners;

import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import me.cloudm1nd3.cloudchat.storage.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerSessionListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        ChatPlayer chatPlayer = PlayerData.loadPlayerData(player);

        if(chatPlayer == null){
            chatPlayer = new ChatPlayer(player.getUniqueId(), player.getName(), false);
            PlayerData.savePlayerData(chatPlayer);
        }

        ChatPlayerManager.getInstance().addPlayerToCache(chatPlayer);
        ChatPlayerManager.getInstance().addOnlinePlayerToMap(chatPlayer);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        ChatPlayer chatPlayer = ChatPlayerManager.getInstance().getChatPlayer(player);

        if(chatPlayer == null) return;

        PlayerData.savePlayerData(chatPlayer);
        ChatPlayerManager.getInstance().removeOnlinePlayerFromMap(chatPlayer);
    }

}
