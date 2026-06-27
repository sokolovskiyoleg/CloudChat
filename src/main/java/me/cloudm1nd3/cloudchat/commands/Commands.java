package me.cloudm1nd3.cloudchat.commands;

import me.cloudm1nd3.cloudchat.CloudChat;
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
        Player player = (Player) sender;
        if(player == null) return false;

        ChatPlayer chatPlayer = ChatPlayerManager.getInstance().getChatPlayer(player);
        if(args[0].equalsIgnoreCase("hide")){
            boolean hiddenChat = chatPlayer.getHiddenChat();
            chatPlayer.setHiddenChat(!hiddenChat);
            PlayerData.savePlayerData(chatPlayer);
            plugin.getServer().broadcastMessage("Значение HiddenChat изменено на " + !hiddenChat);
        }
        return true;
    }
}
