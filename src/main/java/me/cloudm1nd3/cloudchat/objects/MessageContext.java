package me.cloudm1nd3.cloudchat.objects;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.managers.ChannelManager;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.utilities.ColorService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class MessageContext {
    private final AsyncChatEvent event;

    private final Player player;
    private final ChatPlayer chatPlayer;
    private final ChatChannel chatChannel;

    private final String formattedMessageString;

    private final Collection<Audience> originalViewers;

    public MessageContext(AsyncChatEvent event){
        this.event = event;

        player = event.getPlayer();
        chatPlayer = ChatPlayerManager.getInstance().getChatPlayer(player);

        Component originalMessageComponent = event.originalMessage();
        String originalMessageString = ColorService.toPlain(originalMessageComponent).trim();

        chatChannel = ChannelManager.getInstance().getChatChannelByMessage(originalMessageString);

        formattedMessageString = originalMessageString.substring(chatChannel.getQuickSymbol().length()).trim();

        originalViewers = new HashSet<>(event.viewers());
    }

    public ChatPlayer getChatPlayer() {
        return chatPlayer;
    }

    public Player getPlayer(){
        return player;
    }

    public String getFormattedMessageString(){
        return formattedMessageString;
    }

    public ChatChannel getChatChannel(){
        return chatChannel;
    }

    public Collection<Audience> getOriginalViewers(){
        return originalViewers;
    }

    public AsyncChatEvent getEvent(){
        return event;
    }
}
