package me.cloudm1nd3.cloudchat.utilities;

import me.cloudm1nd3.cloudchat.objects.MessageContext;
import net.kyori.adventure.text.Component;

public class MessageFormatter {

    private static final String MESSAGE_PLACEHOLDER = "{message}";

    private MessageFormatter() {}

    public static Component buildComponent(MessageContext context, Component messageComponent) {
        String resolved = PlaceholderService.apply(context.getChatChannel().getFormat(), context);

        int index = resolved.indexOf(MESSAGE_PLACEHOLDER);
        String prefix = resolved.substring(0, index);
        String suffix = resolved.substring(index + MESSAGE_PLACEHOLDER.length());

        Component prefixComponent = ColorService.processLegacy(prefix);
        Component suffixComponent = ColorService.processLegacy(suffix);

        if(ColorService.toPlain(messageComponent).trim().isEmpty()){
            return null;
        }

        return prefixComponent.append(messageComponent).append(suffixComponent);
    }

    public static Component buildMessageComponent(MessageContext context) {
        String rawMessage = context.getFormattedMessageString();

        if (context.getPlayer().hasPermission("cloudchat.colors")) {
            return ColorService.processLegacy(rawMessage);
        }

        return Component.text(rawMessage);
    }
}
