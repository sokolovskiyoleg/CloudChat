package me.cloudm1nd3.cloudchat.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class ColorService {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    public static Component processLegacy(String message) {
        return LEGACY.deserialize(message);
    }

    public static Component processMiniMessage(String message) {
        return MINI_MESSAGE.deserialize(message);
    }

    public static Component processPlain(String message) {
        return PLAIN.deserialize(message);
    }

    public static String toPlain(Component message){
        return PLAIN.serialize(message);
    }

}
