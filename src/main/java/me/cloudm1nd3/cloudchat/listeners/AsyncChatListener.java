package me.cloudm1nd3.cloudchat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.utilities.ChatProcessor;
import me.cloudm1nd3.cloudchat.utilities.CloudChatRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {
    private CloudChat plugin;

    private final ChatProcessor chatProcessor;

    public AsyncChatListener(CloudChat plugin){
        this.plugin = plugin;
        chatProcessor = new ChatProcessor();
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        chatProcessor.process(event);

        if(!event.isCancelled()){
            event.renderer(new CloudChatRenderer());
        }

    }
}
