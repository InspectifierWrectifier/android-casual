/*Instrumentation provides an optional development GUI for CASUAL.
 * Copyright (C) 2013 adamoutler
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

package CASUAL.instrumentation;

import CASUAL.misc.MandatoryThread;

/**
 * Provides a method of instrumentation plugin. 
 * @author Adam Outler adamoutler@gmail.com
 */
public class Instrumentation {
    //Form data
    //public static boolean TargetScriptIsResource = true;  //true if resource, false if file
    /**
     * static reference for optional instrumentation class.
     */
    public static InstrumentationInterface instrumentation;
    
    /**
     * Adds a message to status in instrumentation
     * @param status message to add. 
     */
    public static void updateStatus(String status){
        if(verifyInstrumentation()){
        instrumentation.updateStatus(status);
        }
    }
    
    /**
     * Adds a thread to the thread tracker. 
     * @param t thread to be monitored. 
     */
    public static void trackThread(MandatoryThread t){
        if (verifyInstrumentation()){
            instrumentation.trackThread(t);
        }
    }

    private static boolean verifyInstrumentation() {
        return instrumentation!=null;
    }
    
}
