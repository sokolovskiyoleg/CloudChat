package me.cloudm1nd3.cloudchat.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.managers.ChannelManager;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.managers.CooldownManager;
import me.cloudm1nd3.cloudchat.objects.ChatChannel;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener, ChatRenderer {
    CloudChat plugin;

    public AsyncChatListener(CloudChat plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ChatPlayer chatPlayer =  ChatPlayerManager.getInstance().getChatPlayer(event.getPlayer());
        if(chatPlayer == null) return;

        Component original = event.originalMessage();
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(original).trim();
        if (plainMessage.isEmpty()) {
            return;
        }

        ChatChannel chatChannel = ChannelManager.getInstance().getChatChannelByMessage(plainMessage);
        if (chatChannel == null) {
            event.setCancelled(true);
            return;
        }

        if(CooldownManager.getInstance().isOnCooldown(chatChannel.getName(), chatPlayer)){
            event.setCancelled(true);
            Bukkit.getPlayer(chatPlayer.getUUID()).sendMessage("Остуди своё траханье, дружок");
            return;
        }

        event.viewers().removeIf(audience -> {
            if (!(audience instanceof Player audiPlayer))
                return false;

            ChatPlayer tempChatPlayer = ChatPlayerManager.getInstance().getChatPlayer(audiPlayer);
            return tempChatPlayer != null && tempChatPlayer.getHiddenChat();
        });

        CooldownManager.getInstance().setCooldown(chatChannel.getName(), chatPlayer, chatChannel.getCooldown());

        String format = chatChannel.getFormat();
        format = format.replace("{message}", plainMessage);
        format = format.replace("{PLACEHOLDERS}", chatPlayer.getName());
        Component formattedComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(format);

        event.message(formattedComponent);

        event.renderer(this);
    }

    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        return message;
    }
}
