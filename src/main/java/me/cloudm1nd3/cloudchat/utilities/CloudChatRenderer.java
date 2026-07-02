package me.cloudm1nd3.cloudchat.utilities;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CloudChatRenderer implements ChatRenderer {


    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        return message;
    }
}
