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
    private final String plainMessage;
    private final ChatChannel chatChannel;
    private final Collection<Audience> originalViewers;

    private Component formattedMessage;
    private Collection<Audience> formattedViewers;

    public MessageContext(AsyncChatEvent event){
        this.event = event;

        player = event.getPlayer();
        chatPlayer = ChatPlayerManager.getInstance().getChatPlayer(player);

        Component originalMessage = event.originalMessage();
        plainMessage = PlainTextComponentSerializer.plainText().serialize(originalMessage).trim();

        chatChannel = ChannelManager.getInstance().getChatChannelByMessage(plainMessage);

        originalViewers = new HashSet<>(event.viewers());
        formattedViewers = new HashSet<>(originalViewers);
    }

    public ChatPlayer getChatPlayer() {
        return chatPlayer;
    }

    public Player getPlayer(){
        return player;
    }

    public String getPlainMessage() {
        return plainMessage;
    }

    public Component getFormattedMessage(){
        return formattedMessage;
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

    public void setFormattedMessage(Component formattedMessage){
        this.formattedMessage = formattedMessage;
    }

    public void setFormattedViewers(Collection<Audience> formattedViewers){
        this.formattedViewers = formattedViewers;
    }

}
