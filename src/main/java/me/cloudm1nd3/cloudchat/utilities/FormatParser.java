package me.cloudm1nd3.cloudchat.utilities;

import me.cloudm1nd3.cloudchat.managers.ChatElementManager;
import me.cloudm1nd3.cloudchat.objects.ChatElement;
import me.cloudm1nd3.cloudchat.objects.FormatToken;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatParser {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z0-9_]+)\\}");
    private static final char COLOR_CHAR = '&';

    private FormatParser() {}

    public static List<FormatToken> parse(String format) {
        if (format == null || format.isEmpty()) {
            return List.of();
        }

        List<FormatToken> tokens = new ArrayList<>();
        Matcher matcher = PLACEHOLDER.matcher(format);
        int lastEnd = 0;
        String runningCodes = "";

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String literal = format.substring(lastEnd, matcher.start());
                tokens.add(FormatToken.literal(literal, runningCodes));
                runningCodes = getLastCode(runningCodes + literal);
            }

            String key = matcher.group(1);
            String raw = matcher.group(0);
            TokenPiece piece = resolvePlaceholder(key, raw, runningCodes);
            tokens.add(piece.token());
            runningCodes = piece.runningCodes();
            lastEnd = matcher.end();
        }

        if (lastEnd < format.length()) {
            String literal = format.substring(lastEnd);
            tokens.add(FormatToken.literal(literal, runningCodes));
        }

        return List.copyOf(tokens);
    }

    private static TokenPiece resolvePlaceholder(String key, String raw, String runningCodes) {
        if (key.equalsIgnoreCase("message")) {
            return new TokenPiece(FormatToken.message(runningCodes), runningCodes);
        }

        ChatElement element = ChatElementManager.getInstance().getChatElementByName(key.toLowerCase());
        if (element != null) {
            String after = getLastCode(runningCodes + element.getText());
            return new TokenPiece(FormatToken.element(element, runningCodes), after);
        }

        String after = getLastCode(runningCodes + raw);
        return new TokenPiece(FormatToken.literal(raw, runningCodes), after);
    }

    public static String getLastCode(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        String last = "";
        char[] ch = s.toCharArray();

        for (int i = 0; i < ch.length - 1; i++) {
            if (ch[i] != COLOR_CHAR) {
                continue;
            }

            char next = ch[i + 1];

            if (isHexSequence(ch, i)) {
                last = s.substring(i, i + 14);
                i += 13;
                continue;
            }

            if (isColorOrReset(next)) {
                last = "" + COLOR_CHAR + next;
                i++;
                continue;
            }

            if (isFormat(next)) {
                last += "" + COLOR_CHAR + next;
                i++;
            }
        }

        return last;
    }

    private static boolean isColorOrReset(char code) {
        char c = Character.toLowerCase(code);
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r';
    }

    private static boolean isFormat(char code) {
        char c = Character.toLowerCase(code);
        return c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o';
    }

    private static boolean isHexSequence(char[] ch, int i) {
        if (i + 13 >= ch.length || Character.toLowerCase(ch[i + 1]) != 'x') {
            return false;
        }
        for (int n = 0; n < 6; n++) {
            int p = i + 2 + n * 2;
            if (ch[p] != COLOR_CHAR || !isHexDigit(ch[p + 1])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isHexDigit(char c) {
        c = Character.toLowerCase(c);
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
    }

    private record TokenPiece(FormatToken token, String runningCodes) {}
}
