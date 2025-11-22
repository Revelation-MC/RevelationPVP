package org.mesmeralis.revelationPVP.Utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColourUtils {

    private static final Pattern HEX_CODE_PATTERN = Pattern.compile("#([A-Fa-f\\d]{6}|[A-Fa-f\\d]{3})");

    private ColourUtils() {
        throw new UnsupportedOperationException();
    }

    public static String colour(String s) {
        if (s == null || s.isBlank()) {
            return s;
        }

        final Matcher matcher = HEX_CODE_PATTERN.matcher(s);
        final StringBuilder builder = new StringBuilder();

        int lastEndIndex = 0;
        while (matcher.find()) {
            builder.append(s, lastEndIndex, matcher.start())
                    .append(ChatColor.of(matcher.group()).toString());

            lastEndIndex = matcher.end();
        }
        builder.append(s, lastEndIndex, s.length());

        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }
}
