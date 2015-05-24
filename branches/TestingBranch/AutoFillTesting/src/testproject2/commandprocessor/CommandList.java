/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testproject2.commandprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author loganludington
 */
public class CommandList {
    ArrayList<String> commandNames = new ArrayList<>();
    ArrayList<Command> commandArray = new ArrayList<>();
    
    public CommandList(InputStream in) {
        Properties prop = new Properties();
        try {
            prop.load(in);
            processProps(prop);
        } catch (IOException ex) {
            Logger.getLogger(CommandList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public CommandList(Properties prop) {
        processProps(prop);
        
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                CommandList cmd = new CommandList(this.getClass().getClassLoader().getResourceAsStream("commandprocessor/resources/Commands.properties"));
            }
        });
    } 

    private void processProps(Properties prop) {
        for (String s : prop.stringPropertyNames()) {
            if (s.endsWith(".Name")) {
                Command commandToAdd =new Command(prop.getProperty(s),prop);
                commandArray.add(commandToAdd);
                commandNames.add(prop.getProperty(s));
                System.out.println("Loaded command " + commandToAdd.name + ". "+"\nDescription = "
                        + commandToAdd.description + "\nType = " +
                        commandToAdd.type + "\nSyntax = " + commandToAdd.syntax +
                        "\nScript = " + commandToAdd.script +"\nArgs = " + commandToAdd.argMap.keySet() + "\n\n");
            }
        }
    }
    public ArrayList<Command> getCommandListByStart(String startSeq) {
        ArrayList<Command> returnArray = new ArrayList<>();
        for (Command c : commandArray) {
            if (c.script.startsWith(startSeq))
                returnArray.add(c);
        }
        return returnArray;
    }
    
}
