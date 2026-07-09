package me.cloudm1nd3.cloudchat.utilities;

import me.cloudm1nd3.cloudchat.objects.ChatElement;
import me.cloudm1nd3.cloudchat.objects.MessageContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class MessageFormatter {

    private static final String MESSAGE_PLACEHOLDER = "{message}";

    private MessageFormatter() {}

    public static Component buildFinalComponent(MessageContext context) {
        Component finalComponent = Component.empty();

        for(ChatElement element : context.getChatChannel().getFormat()){
            Component elementComponent = buildFromChatElement(element, context);
            finalComponent = finalComponent.append(elementComponent);
        }

        return finalComponent;
    }

    private static Component buildFromChatElement(ChatElement element, MessageContext context) {
        if (element == null) {
            return Component.empty();
        }
        String finalText;
        Component component;

        if(element.getText().contains("{message}")){
            component = buildMessageComponent(element, context);
        } else {
            finalText = PlaceholderService.apply(element.getText(), context);
            component = ColorService.processLegacy(finalText);
        }


        List<String> hoverLines = element.getHoverLines();
        if (hoverLines != null && !hoverLines.isEmpty()) {
            StringBuilder hoverBuilder = new StringBuilder();

            for (int i = 0; i < hoverLines.size(); i++) {
                String line = hoverLines.get(i);
                String processed = PlaceholderService.apply(line, context);
                hoverBuilder.append(processed);

                if (i < hoverLines.size() - 1) {
                    hoverBuilder.append("\n");
                }
            }

            Component hoverComponent = ColorService.processLegacy(hoverBuilder.toString());
            component = component.hoverEvent(HoverEvent.showText(hoverComponent));
        }

        if (element.getActionType() != null) {
            String processedActionValue = PlaceholderService.apply(element.getActionValue(), context);
            ClickEvent.Action action = element.getActionType();
            ClickEvent.Payload payload = ClickEvent.Payload.string(processedActionValue);

            ClickEvent clickEvent = ClickEvent.clickEvent(action, payload);

            component = component.clickEvent(clickEvent);
        }

        return component;
    }

    public static Component buildMessageComponent(ChatElement element, MessageContext context) {
        String rawMessage = context.getFormattedMessageString();



        if (context.getPlayer().hasPermission("cloudchat.colors")) {
            return ColorService.processLegacy(rawMessage);
        }

        return Component.text(rawMessage);
    }
}
