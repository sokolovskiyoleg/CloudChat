package me.cloudm1nd3.cloudchat.objects;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.managers.ChannelManager;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
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

    private final String originalMessageString;
    private String formattedMessageString;

    private final ChatChannel chatChannel;
    private final Collection<Audience> originalViewers;

    private Collection<Audience> formattedViewers;

    public MessageContext(AsyncChatEvent event){
        this.event = event;

        player = event.getPlayer();
        chatPlayer = ChatPlayerManager.getInstance().getChatPlayer(player);

        Component originalMessage = event.originalMessage();
        originalMessageString = PlainTextComponentSerializer.plainText().serialize(originalMessage).trim();

        chatChannel = ChannelManager.getInstance().getChatChannelByMessage(originalMessageString);
        formattedMessageString = originalMessageString.substring(chatChannel.getQuickSymbol().length());

        originalViewers = new HashSet<>(event.viewers());
        formattedViewers = new HashSet<>(originalViewers);
    }

    public ChatPlayer getChatPlayer() {
        return chatPlayer;
    }

    public Player getPlayer(){
        return player;
    }

    public String getOriginalMessageString() {
        return originalMessageString;
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

    public Collection<Audience> getFormattedViewers(){
        return formattedViewers;
    }

    public AsyncChatEvent getEvent(){
        return event;
    }

    public void setFormattedMessageString(String formattedMessage){
        this.formattedMessageString = formattedMessage;
    }

    public void setFormattedViewers(Collection<Audience> formattedViewers){
        this.formattedViewers = formattedViewers;
    }

}
