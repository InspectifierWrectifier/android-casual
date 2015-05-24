/*CASUALInstrumentationTimer provides a timer for Instrumentation to use
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
package CasualInstrumentation;

import CASUAL.CASUALConnectionStatusMonitor;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 *
 * @author adamoutler
 */
public class CASUALInstrumentationTimer {

    Timer timer;
    Toolkit toolkit;

    public CASUALInstrumentationTimer() {

    }

    class StatusUpdate extends TimerTask {

        @Override
        public void run() {
            final String status = CASUALConnectionStatusMonitor.getStatus();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    CASUALInstrumentation.doc.monitorStatus.setText(status);
                }
            });
        }
    }

    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                toolkit = Toolkit.getDefaultToolkit();
                timer = new Timer();
                timer.schedule(new StatusUpdate(),
                        0, //initial delay
                        2 * 1000);  //subsequent rate
            }
        });
        t.start();
    }

}
