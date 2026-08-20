package me.cloudm1nd3.cloudchat.utilities;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.lang.Lang;
import me.cloudm1nd3.cloudchat.managers.CooldownManager;
import me.cloudm1nd3.cloudchat.objects.ChatChannel;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import me.cloudm1nd3.cloudchat.objects.MessageContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatProcessor {

    public void process(AsyncChatEvent event) {
        MessageContext context = getContext(event);

        if (!preProcess(context)) {
            event.setCancelled(true);
            return;
        }

        if (!checkPermission(context)) {
            context.getPlayer().sendMessage(Lang.component("channel.no-permission", "channel", context.getChatChannel().getName()));
            event.setCancelled(true);
            return;
        }

        int cooldown = getCooldown(context);
        if (cooldown > 0) {
            context.getPlayer().sendMessage(Lang.component("channel.cooldown", "seconds", cooldown));
            event.setCancelled(true);
            return;
        }

        if (isMessageEmpty(context)) {
            event.setCancelled(true);
            return;
        }

        Component finalComponent = buildFinalComponent(context);

        Collection<Audience> finalViewers = filterViewers(context);

        applyCooldown(context);

        applyChangesToEvent(event, finalViewers, finalComponent);
    }

    private Component buildFinalComponent(MessageContext context){
        return MessageFormatter.buildFinalComponent(context);
    }

    private boolean isMessageEmpty(MessageContext context) {
        String raw = context.getFormattedMessageString();
        if (raw == null) {
            return true;
        }

        return context.getPlayer().hasPermission("cloudchat.colors")
                ? ColorService.isBlankIgnoringLegacyCodes(raw)
                : raw.trim().isEmpty();
    }

    private boolean preProcess(MessageContext context){
        return context.getChatChannel() != null
                && context.getChatPlayer() != null
                && context.getFormattedMessageString() != null
                && !context.getFormattedMessageString().isEmpty();
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
        String channelName = context.getChatChannel().getName();
        return player.hasPermission("cloudchat.channel." + channelName);
    }

    private void applyChangesToEvent(AsyncChatEvent event, Collection<Audience> viewers, Component message){
        event.viewers().clear();
        event.viewers().addAll(viewers);
        event.message(message);
    }

    private MessageContext getContext(AsyncChatEvent event){
        return new MessageContext(event);
    }

    private Collection<Audience> filterViewers(MessageContext context){
        Player player = context.getPlayer();
        ChatChannel chatChannel = context.getChatChannel();
        Collection<Audience> viewers = context.getOriginalViewers();

        return filterPlayersOutsideRadius(player, chatChannel.getRadius(), viewers);
    }

    private Collection<Audience> filterPlayersOutsideRadius(Player sender, int radius, Collection<Audience> viewers){
        List<Audience> filteredViewers = new ArrayList<>(viewers);

        if(radius <= 0) {
            return filteredViewers;
        }

        Location senderLocation = sender.getLocation();
        double radiusSquared = radius * radius;

        filteredViewers.removeIf(audience -> {
            if (audience instanceof Player player) {
                Location playerLoc = player.getLocation();

                if(!playerLoc.getWorld().equals(senderLocation.getWorld())){
                    return true;
                }
                return playerLoc.distanceSquared(senderLocation) > radiusSquared;
            }
            return false;
        });

        return filteredViewers;
    }
}
