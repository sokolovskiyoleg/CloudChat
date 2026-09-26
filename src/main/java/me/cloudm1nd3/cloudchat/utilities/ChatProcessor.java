package me.cloudm1nd3.cloudchat.utilities;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.configs.Messages;
import me.cloudm1nd3.cloudchat.managers.CooldownManager;
import me.cloudm1nd3.cloudchat.objects.ChatChannel;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import me.cloudm1nd3.cloudchat.objects.MessageContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class ChatProcessor {

    public void process(AsyncChatEvent event) {
        MessageContext context = getContext(event);

        if (!preProcess(context)) {
            event.setCancelled(true);
            return;
        }

        if (!checkPermission(context)) {
            Messages.CHANNEL_NO_PERMISSION.send(context.getPlayer());
            event.setCancelled(true);
            return;
        }

        long cooldown = getCooldown(context);
        if (cooldown > 0) {
            Map<String, String> timeFormat = durationPlaceholders(cooldown);
            Messages.CHANNEL_COOLDOWN.send(context.getPlayer(),
                    "%d%", timeFormat.get("%d%"),
                    "%h%", timeFormat.get("%h%"),
                    "%m%", timeFormat.get("%m%"),
                    "%s%", timeFormat.get("%s%"));
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

    private Map<String, String> durationPlaceholders(long millis) {
        long totalSeconds = millis / 1000;
        int days    = (int) (totalSeconds / 86400);
        int hours   = (int) ((totalSeconds % 86400) / 3600);
        int minutes = (int) ((totalSeconds % 3600) / 60);
        int seconds = (int) (totalSeconds % 60 + 1);

        Map<String, String> map = new HashMap<>();
        map.put("%d%", String.valueOf(days));
        map.put("%h%", String.valueOf(hours));
        map.put("%m%", String.valueOf(minutes));
        map.put("%s%", String.valueOf(seconds));
        return map;
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

    private long getCooldown(MessageContext context){
        ChatPlayer chatPlayer = context.getChatPlayer();
        return CooldownManager.getInstance().getCooldown(context.getChatChannel(), chatPlayer);
    }

    private boolean checkPermission(MessageContext context){
        Player player = context.getPlayer();
        ChatChannel channel = context.getChatChannel();
        return player.hasPermission(channel.getSpeakPermission());
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
