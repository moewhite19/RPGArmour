package cn.whiteg.rpgArmour.utils;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonBuilder {

    private static final Pattern PART_PATTERN = Pattern.compile("(([§][a-fA-Fl-oL-OkK0-9])+)([^§]*)");
    private Part rootPart;

    public JsonBuilder() {
    }

    public static Part parse(String text) {
        Matcher matcher = PART_PATTERN.matcher(text);

        if (!matcher.find()){
            return new Part(text);
        }

        matcher.reset();

        PartArray array = new PartArray();
        int lastEndIndex = 0;

        while (matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            if (lastEndIndex != startIndex){
                String betweenMatches = text.substring(lastEndIndex,startIndex);
                array.addPart(new Part(betweenMatches));
            }

            String format = matcher.group(1);
            String value = matcher.group(3);

            PartMap part = new PartMap();
            part.setValue("text",new Part(value));

            String[] formats = format.split("§");
            for (String f : formats) {
                switch (f.toLowerCase()) {
                    case "":
                        break;
//                    case "k":
//                        part.setValue("obuscated",new Part(true));
//                        break;
//                    case "b":
//                        part.setValue("bold",new Part(true));
//                        break;
//                    case "l":
//                        part.setValue("strikethrough",new Part(true));
//                        break;
//                    case "n":
//                        part.setValue("underlined",new Part(true));
//                        break;
//                    case "o":
//                        part.setValue("italic",new Part(true));
//                        break;
                    case "r":
                        part.removeValue("obfuscated");
                        part.removeValue("bold");
                        part.removeValue("strikethrough");
                        part.removeValue("underlined");
                        part.removeValue("italic");
                        part.removeValue("color");
                        break;
                    default:
                        part.setValue("color",new Part(ChatColor.getByChar(f).name().toLowerCase()));
                }
            }

            array.addPart(part);
            lastEndIndex = endIndex;
        }

        return array;
    }

    @Override
    public String toString() {
        return rootPart.toString();
    }

    public Part getRootPart() {
        return rootPart;
    }

    public void setRootPart(Part rootPart) {
        this.rootPart = rootPart;
    }

    public void sendJson(Player p) {
        final PacketPlayOutChat pc = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toString()));
        Utils.sendPacket(pc,p);
//            plugin.debug("Sent JSON: " + toString());
    }

    public static class Part {
        private String value;

        public Part() {
            this("",true);
        }

        public Part(Object value) {
            this(value,value instanceof CharSequence);
        }

        public Part(Object value,boolean appendQuotes) {
            if (appendQuotes){
                this.value = "\"" + value + "\"";
            } else {
                this.value = String.valueOf(value);
            }
        }

        @Override
        public String toString() {
            return value;
        }

        public PartArray toArray() {
            return new PartArray(this);
        }

        public PartMap toMap() {
            PartMap map = new PartMap();
            map.setValue("text",new Part());
            map.setValue("extra",toArray());
            return map;
        }
    }

    public static class PartMap extends Part {
        private Map<String, Part> values = new HashMap<>();

        public PartMap() {
        }

        public PartMap(Map<String, Part> values) {
            this.values.putAll(values);
        }

        public void setValue(String key,Part value) {
            values.put(key,value);
        }

        public void removeValue(String key) {
            values.remove(key);
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(",","{","}");
            values.forEach((key,value) -> joiner.add("\"" + key + "\":" + value.toString()));
            return joiner.toString();
        }

        @Override
        public PartMap toMap() {
            return this;
        }
    }

    public static class PartArray extends Part {
        private List<Part> parts = new ArrayList<>();

        public PartArray(Part... parts) {
            this.parts.addAll(Arrays.asList(parts));
        }

        public void addPart(Part part) {
            parts.add(part);
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(",","[","]");
            parts.forEach(part -> joiner.add(part.toString()));
            return joiner.toString();
        }

        @Override
        public PartArray toArray() {
            return this;
        }
    }

}
