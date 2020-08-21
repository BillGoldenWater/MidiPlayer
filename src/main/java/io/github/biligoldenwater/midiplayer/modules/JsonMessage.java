//<dependency>
//    <groupId>com.alibaba</groupId>
//    <artifactId>fastjson</artifactId>
//    <version>1.2.47</version>
//</dependency>
package io.github.biligoldenwater.midiplayer.modules;

import com.alibaba.fastjson.JSONObject;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonMessage {
    private final HashMap<String,JsonMessageSingle> message = new HashMap<>();
    private final List<String> texts = new ArrayList<>();

    public JsonMessage(String text){
        message.put(text, new JsonMessageSingle(text));
        texts.add(text);
    }

    public JsonMessage(){
    }

    public void addText(String text){
        message.put(text, new JsonMessageSingle(text));
        texts.add(text);
    }

    public boolean setColor(String text,String color){
        try {
            this.message.get(text).setColor(color);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public boolean setBold(String text,boolean bold){
        try {
            this.message.get(text).setBold(bold);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public boolean setItalic(String text,boolean italic){
        try {
            this.message.get(text).setItalic(italic);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public boolean setUnderlined(String text,boolean underlined){
        try {
            this.message.get(text).setUnderlined(underlined);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public boolean setStrikethrough(String text,boolean strikethrough){
        try {
            this.message.get(text).setStrikethrough(strikethrough);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public boolean setObfuscated(String text,boolean obfuscated){
        try {
            this.message.get(text).setObfuscated(obfuscated);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public boolean addHoverEvent(String text, String action_type, Object value){
        try {
            this.message.get(text).addHoverEvent(action_type, value);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public boolean addClickEvent(String text, String action_type, Object value){
        try {
            this.message.get(text).addClickEvent(action_type, value);
            return true;
        } catch (NullPointerException e){
            return false;
        }
    }

    public List<JSONObject> getJsonArray(){
        List<JSONObject> output = new ArrayList<>();

        for (String textName : texts){
            output.add(message.get(textName).getJSONObject());
        }

        return output;
    }

    public String getJsonText(){
        List<String> output = new ArrayList<>();

        for (String textName : texts){
            output.add(message.get(textName).getStringOutput());
        }

        return output.toString();
    }

    public String getCommand(CommandSender sender){
        return "tellraw "+sender.getName()+" "+this.getJsonText();
    }

    public void sendTo(CommandSender sender){
        sender.getServer().dispatchCommand(sender.getServer().getConsoleSender(),this.getCommand(sender));
    }

    public class JsonMessageSingle {
        private final JSONObject text = new JSONObject();

        public JsonMessageSingle(String text){
            if(text == null)text = "";
            this.text.put("text",text);
        }

        public JsonMessageSingle(){

        }

        public String getStringOutput(){
            return text.toJSONString();
        }

        public JSONObject getJSONObject(){
            return text;
        }

        public void setColor(String color){
            this.text.put("color",color);
        }

        public void setBold(boolean bold){
            this.text.put("bold",bold);
        }

        public void setItalic(boolean italic){
            this.text.put("italic",italic);
        }

        public void setUnderlined(boolean underlined){
            this.text.put("underlined",underlined);
        }

        public void setStrikethrough(boolean strikethrough){
            this.text.put("strikethrough",strikethrough);
        }

        public void setObfuscated(boolean obfuscated){
            this.text.put("obfuscated",obfuscated);
        }

        public void addHoverEvent(String action_type, Object value){
            JsonMessageSingle json = new JsonMessageSingle();
            json.text.put("action",action_type);
            json.text.put("value",value);
            this.text.put("hoverEvent",json.text);
        }

        public void addClickEvent(String action_type, Object value){
            JsonMessageSingle json = new JsonMessageSingle();
            json.text.put("action",action_type);
            json.text.put("value",value);
            this.text.put("clickEvent",json.text);
        }
    }

    public static class clickEvent {
        public static class action {
            public static final String open_url = "open_url";
            public static final String run_command = "run_command";
            public static final String suggest_command = "suggest_command";
            public static final String change_page = "change_page";
        }
    }

    public static class hoverEvent {
        public static class action {
            public static final String show_text = "show_text";
            public static final String show_item = "show_item";
        }
    }
}

