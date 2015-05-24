/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testproject2.commandprocessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author loganludington
 */
    public class Command{
        public String name = "";
        public String type = "";
        public String script="";
        public String syntax="";
        public String description="";
        public Map<String,String> argMap;

        public Command(String name, Properties prop){
            this.name = name.trim();
            this.type = prop.getProperty("Commands."+ name + ".Type");
            this.script = prop.getProperty("Commands."+ name + ".Script");
            this.syntax = prop.getProperty("Commands."+ name + ".Syntax");
            this.description = prop.getProperty("Commands."+ name + ".Description");
            this.argMap = new HashMap();
            for (String s : syntax.split(" ")) {
                if (!s.startsWith("$") && !s.trim().isEmpty())
                    argMap.put(s.replace(",", "").trim(), "");
            }
        }
        public Command(String name) {
            this.name = name.trim();

        }

        @Override
        public String toString() {
            return name;
        }
}
