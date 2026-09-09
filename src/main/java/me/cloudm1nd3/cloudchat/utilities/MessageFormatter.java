package me.cloudm1nd3.cloudchat.utilities;

import me.cloudm1nd3.cloudchat.objects.ChatElement;
import me.cloudm1nd3.cloudchat.objects.FormatToken;
import me.cloudm1nd3.cloudchat.objects.MessageContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;

import java.util.List;

public class MessageFormatter {

    private static final String MESSAGE_PLACEHOLDER = "{message}";

    private MessageFormatter() {}

    public static Component buildFinalComponent(MessageContext context) {
        Component finalComponent = Component.empty();

        for(FormatToken token: context.getChatChannel().getFormat()){
            switch (token.getType()) {
                case LITERAL -> {
                    Component literalComponent = ColorService.processLegacy(PlaceholderService.apply(token.getLiteral(), context));
                    finalComponent = finalComponent.append(literalComponent);
                }
                case ELEMENT -> {
                    ChatElement element = token.getElement();
                    finalComponent = finalComponent.append(buildFromChatElement(element, context));
                }
                case MESSAGE -> {
                    finalComponent = finalComponent.append(buildMessageComponent(token.getPreviousCode(), context));
                }
            }
        }

        return finalComponent;
    }

    private static Component buildFromChatElement(ChatElement element, MessageContext context) {
        if (element == null) {
            return Component.empty();
        }

        Component component = ColorService.processLegacy(PlaceholderService.apply(element.getText(), context));
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

    public static Style styleFromLastCodes(String lastColorCode) {
        if (lastColorCode == null || lastColorCode.isEmpty()) {
            return Style.empty();
        }

        return ColorService.processLegacy(lastColorCode + "\u2060").style();
    }

    public static Component buildMessageComponent(String lastColorCode, MessageContext context) {
        String rawMessage = context.getFormattedMessageString();

        if (context.getPlayer().hasPermission("cloudchat.colors")) {
            return ColorService.processLegacy(lastColorCode + rawMessage);
        }

        return Component.text(rawMessage).applyFallbackStyle(styleFromLastCodes(lastColorCode));
    }
}
