package me.KrazyManJ.KrazyBoard.Utils;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {
    public static String colorize(String t){
        Matcher m = Pattern.compile("(?i)&#[0-9a-f]{6}").matcher(t);
        while (m.find()){
            t = t.replace(t.substring(m.start(),m.end()), ""+ ChatColor.of(t.substring(m.start(),m.end()).replace("&", "")));
            m = Pattern.compile("(?i)&#[0-9a-f]{6}").matcher(t);
        }
        m = Pattern.compile("(?i)\\{#[0-9a-f]{6}}").matcher(t);
        while (m.find()){
            t = t.replace(t.substring(m.start(),m.end()), ""+ ChatColor.of(t.substring(m.start(),m.end()).replaceAll("[{}]", "")));
            m = Pattern.compile("(?i)\\{#[0-9a-f]{6}}").matcher(t);
        }
        return ChatColor.translateAlternateColorCodes('&', t);
    }
}
