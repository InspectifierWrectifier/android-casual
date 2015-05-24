/*MandatoryThread extends the functionality of thread for better monitoring. 
 *Copyright (C) 2015  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package CASUAL.misc;

import CASUAL.Log;
import CASUAL.instrumentation.Instrumentation;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * extends the functionality of thread for better monitoring.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class MandatoryThread extends Thread {

    AtomicBoolean hasStarted = new AtomicBoolean(false);
    boolean nullThread = false;

    /**
     * constructor for thread sets up a blank Mandatory Thread.
     */
    public MandatoryThread() {
        hasStarted.set(true);
        nullThread = true;
    }

    /**
     * constructor accepts a runnable to be used for the thread.
     *
     * @param r to be run in a different thread
     */
    public MandatoryThread(Runnable r) {
        super(r);
    }

    /**
     * starts the thread and sets the "hasStarted" boolean.
     */
    @Override
    public synchronized void start() {
        if (nullThread) {
            return;
        }
        hasStarted.set(true);
        super.start();
        Log.level4Debug("Started MandatoryThread " + this.getName());
        notify();
        Instrumentation.trackThread(this);
    }

    /**
     * isComplete allows for monitoring of the progress of a thread. If the
     * thread has started and is no longer alive this will return true. The
     * MandatoryThread has done its job.
     *
     * @return true if MandatoryThread is complete
     */
    public boolean isComplete() {
        return hasStarted.get() && !super.isAlive();
    }

    /**
     * halts the current thread until the mandatoryThread has completed. If the
     * thread has not started, it will wait for the thread to start.
     */
    public synchronized void waitFor() {
        try {
            if (nullThread) {
                return;
            }
            while (!hasStarted.get()) {
                wait();
            }
            if (this.isAlive()) {
                System.out.println("waiting for " + this.getName());
                super.join();
                System.out.println(this.getName() + " has completed");
            }
        } catch (InterruptedException ex) {

        }
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        String n="\n";
        sb.append("Name:").append(getName()).append(n);
        sb.append("Started:").append(this.hasStarted).append(n);
        sb.append("Complete:").append(this.isComplete()).append(n);
        
        return sb.toString();
    }

};
