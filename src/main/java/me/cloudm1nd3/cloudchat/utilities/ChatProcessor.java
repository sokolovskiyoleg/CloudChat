package me.cloudm1nd3.cloudchat.utilities;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.managers.CooldownManager;
import me.cloudm1nd3.cloudchat.objects.ChatChannel;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import me.cloudm1nd3.cloudchat.objects.MessageContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ChatProcessor {

    public void process(AsyncChatEvent event){
        MessageContext context = getContext(event);

        if(!preProcess(context)){
            event.setCancelled(true);
            return;
        }

        if(!checkPermission(context)){
            context.getPlayer().sendMessage("Нет прав!");
            event.setCancelled(true);
            return;
        }

        int cooldown = getCooldown(context);
        if(cooldown > 0){
            context.getPlayer().sendMessage("Остуди своё траханье, друг. Остужать еще " + cooldown + " секунд!");
            event.setCancelled(true);
            return;
        }

        filterViewers(context);

        filterMessage(context);

        applyCooldown(context);

        applyChangesToEvent(event, context);
    }

    private void filterMessage(MessageContext context){
        ChatPlayer chatPlayer = context.getChatPlayer();
        ChatChannel chatChannel = context.getChatChannel();
        String plainMessage = context.getPlainMessage();

        String format = chatChannel.getFormat();
        plainMessage = plainMessage.substring(chatChannel.getQuickSymbol().length());
        format = format.replace("{message}", plainMessage);
        format = format.replace("{PLACEHOLDERS}", chatPlayer.getName());
        Component formattedComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(format);

        context.setFormattedMessage(formattedComponent);
    }


    private boolean preProcess(MessageContext context){
        return context.getChatChannel() != null
                && context.getChatPlayer() != null
                && context.getPlainMessage() != null
                && !context.getPlainMessage().isEmpty();
    }

    private void applyCooldown(MessageContext context){
        ChatChannel chatChannel = context.getChatChannel();
        ChatPlayer chatPlayer = context.getChatPlayer();

        CooldownManager.getInstance().setCooldown(chatChannel, chatPlayer);
    }

    private int getCooldown(MessageContext context){
        ChatPlayer chatPlayer = context.getChatPlayer();
        long cooldown = CooldownManager.getInstance().getCooldown(context.getChatChannel(), chatPlayer);
        return (int) Math.ceil(cooldown / 1000.0);
    }

    private boolean checkPermission(MessageContext context){
        Player player = context.getPlayer();
        return player.hasPermission("cloudchat.speak");
    }

    private void applyChangesToEvent(AsyncChatEvent event, MessageContext context){
        event.viewers().clear();
        event.viewers().addAll(context.getFormattedViewers());

        event.message(context.getFormattedMessage());
    }

    private MessageContext getContext(AsyncChatEvent event){
        return new MessageContext(event);
    }

    private void filterViewers(MessageContext context){
        Collection<Audience> viewers = context.getFormattedViewers();

        filterPlayersOutsideRadius(context.getPlayer(), context.getChatChannel().getRadius(), viewers);
    }

    private void filterPlayersOutsideRadius(Player sender, int radius, Collection<Audience> viewers){
        if(radius <= 0) return;

        Location senderLocation = sender.getLocation();
        double radiusSquared = radius * radius;

        viewers.removeIf(audience -> {
            if (audience instanceof Player player) {
                Location playerLoc = player.getLocation();

                if(!playerLoc.getWorld().equals(senderLocation.getWorld())){
                    return true;
                }
                return playerLoc.distanceSquared(senderLocation) > radiusSquared;
            }
            return false;
        });
    }
}
