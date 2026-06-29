package me.cloudm1nd3.cloudchat.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.managers.ChannelManager;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.managers.CooldownManager;
import me.cloudm1nd3.cloudchat.objects.ChatChannel;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import me.cloudm1nd3.cloudchat.utilities.ChatProcessor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener, ChatRenderer {
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
            event.renderer(this);
        }

    }

    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        return message;
    }
}
