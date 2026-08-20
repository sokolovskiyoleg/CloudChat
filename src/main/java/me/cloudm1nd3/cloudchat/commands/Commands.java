package me.cloudm1nd3.cloudchat.commands;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.lang.Lang;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import me.cloudm1nd3.cloudchat.storage.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private CloudChat plugin = CloudChat.getInstance();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("command.player-only"));
            return true;
        }

        if(args[0].equalsIgnoreCase("hide")){
            ChatPlayer chatPlayer = ChatPlayerManager.getInstance().getChatPlayer(player);
            boolean hiddenChat = chatPlayer.getHiddenChat();
            chatPlayer.setHiddenChat(!hiddenChat);
            PlayerData.savePlayerData(chatPlayer);
            sender.sendMessage(Lang.component("chat.hidden-toggled", "value", !hiddenChat));
            return true;
        }

        sender.sendMessage(Lang.get("command.unknown"));
        return true;
    }
}
