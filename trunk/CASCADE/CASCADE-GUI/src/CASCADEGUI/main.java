/*CASCADEGUI is CASUAL's Automated Scripting Action Development Environment GUI
 *Copyright (C) 2013  Adam Outler & Logan Ludington
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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CASCADEGUI;

import CASUAL.Log;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Provides a launcher for CASCADE
 * @author adam
 */
public class main {

    /**
     *  Launches CASCADEGUI. 
     * @param args no arguments taken
     */
    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Log.errorHandler(ex);
        } catch (InstantiationException ex) {
            Log.errorHandler(ex);
        } catch (IllegalAccessException ex) {
            Log.errorHandler(ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Log.errorHandler(ex);
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                CASCADEGUI cg = new CASCADEGUI();
            }
        };
        Thread t = new Thread(r);
        t.setName("GUI");
        t.start();

    }
}
