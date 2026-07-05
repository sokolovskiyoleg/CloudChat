package me.cloudm1nd3.cloudchat.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.cloudm1nd3.cloudchat.objects.MessageContext;
import org.bukkit.Bukkit;

public class PlaceholderService {
    private static boolean papiEnabled;

    private PlaceholderService() {}

    public static void init() {
        papiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static String apply(String template, MessageContext context) {
        if (papiEnabled) {
            template = applyPAPI(template, context);
        }

        template = applyOwn(template, context);

        return template;
    }

    private static String applyPAPI(String template, MessageContext context){
        template = PlaceholderAPI.setPlaceholders(context.getPlayer(), template);
        template = PlaceholderAPI.setBracketPlaceholders(context.getPlayer(), template);
        return template;
    }

    private static String applyOwn(String template, MessageContext context) {
        template = applyBoth(template, "channel_prefix", context.getChatChannel().getPrefix());
        return template;
    }

    private static String applyBoth(String template, String key, String value) {
        return template.replace("{" + key + "}", value)
                .replace("%" + key + "%", value);
    }
}
