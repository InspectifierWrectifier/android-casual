/*
 * Copyright (C) 2014 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package CASUAL.language;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * holds a command and return value for processing
 * @author adamoutler (adamoutler@gmail.com)
 */
public class Command {

    final AtomicBoolean hasExecuted = new AtomicBoolean(false);
    boolean result = false;
    String returnValue="";
    String command;

    /**
     * initializes a new CASUAL command object
     *
     * @param command command to run
     */
    public Command(String command) {
        this.command = command.trim();
    }

    /**
     * releases all waitFor()s and resets the command to be run once again.
     */
    public void reset(){
        hasExecuted.set(false);
        result=false;
        returnValue=null;
        notifyExecuted();
    }    /**
     * releases all waitFor()s and resets the command to be run once again.
     */

    /**
     * releases all waitFor()s and resets the command to be run once again.
     * @param command modified command to be run
     */
    public void reset(String command){
        this.command=command.trim();
        hasExecuted.set(false);
        result=false;
        returnValue=null;
        notifyExecuted();
    }
    
    /**
     * @return true if command has been executed
     */
    public boolean isFinished(){
        return hasExecuted.get();
    }
    
    /**
     * @return the command to be executed
     */
    public String get(){
        return command;
    }
    
    /**
     * @return the return value from the command
     */
    public String getReturn(){
        return returnValue;
    }

    /**
     * @return true if no problems detected.  false if problem detected.
     */
    public boolean getReturnPassedOrFailed(){
        return result;
    }

    public void waitFor() throws InterruptedException {
        if (!hasExecuted.get()) {
            synchronized (hasExecuted){
                hasExecuted.wait();
            }
        }
    }


    /**
     * sets the CASUAL Command to process.
     * @param command CASUAL command
     */
    public void set(String command){
        this.command=command;
    }
    private void notifyExecuted(){
         synchronized (hasExecuted){
            hasExecuted.notifyAll();
        }
    }
    
    /**
     * sets the return value for the command.
     * @param passed true if passed
     * @param retval return value from command
     */
    public void setReturn(boolean passed, String retval) {
        result = passed;
        returnValue=retval;
        hasExecuted.set(true);
        notifyExecuted();
    }


    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("Command:");
        sb.append(command);
        sb.append("\nStatus:");
        if (isFinished()){
            sb.append(("Executed.\n"));
            sb.append((result?"Sucessful":"Not Sucessful"));
            sb.append("\nReturn:");
            sb.append(returnValue);
        } else {
            sb.append("Ready - not exeucted");
        }
        return sb.toString();
        
    }

}
