package me.cloudm1nd3.cloudchat.commands;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.configs.Config;
import me.cloudm1nd3.cloudchat.configs.Messages;
import me.cloudm1nd3.cloudchat.managers.ChannelManager;
import me.cloudm1nd3.cloudchat.managers.ChatElementManager;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import me.cloudm1nd3.cloudchat.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.nio.channels.Channel;

public class Commands implements CommandExecutor {
    private CloudChat plugin = CloudChat.getInstance();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            return false;
        }

        Player player = (Player) sender;

        String commandText = args[0].toLowerCase();

        switch (commandText) {
            case "reload" -> handleReload(args, sender);
            default -> {
                return false;
            }
        }

        return true;
    }

    private void handleReload(String[] args, CommandSender sender) {
        if (!sender.hasPermission("cloudchat.admin.reload")) {
            Messages.NO_PERMISSION.send(sender);
            return;
        }

        try {
            Config.reload();
            Messages.reload();
            ChatElementManager.getInstance().reload();
            ChannelManager.getInstance().reload();
            Messages.RELOAD_SUCCESS.send(sender);
        } catch (Exception e) {
            plugin.getLogger().severe("[CloudChat] Reload failed: " + e.getMessage());
            Messages.RELOAD_FAILED.send(sender);
        }
    }


}
