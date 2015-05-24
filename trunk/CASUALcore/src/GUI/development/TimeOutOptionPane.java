/*TimeOutOptionPane provides an option pane with timeout
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
package GUI.development;

import CASUAL.Log;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * TimeOutOptionPane provides an option pane with timeout
 *
 * @author modified heavily by Adam Outler adamoutler@gmail.com based on
 * uncopyrighted work http://www.jguru.com/faq/view.jsp?EID=266182
 */
public class TimeOutOptionPane extends JOptionPane {

    static final long serialVersionUID = 9876543234567L;

    static int PRESET_TIME = 335;
    /*
     * int showTimeoutDialog = timeOutOptionPane.showTimeoutDialog( 5, //timeout
     * null, //parentComponent "My Message", //Display Message "My Title",
     * //DisplayTitle TimeOutOptionPane.YES_OPTION, // Options buttons
     * TimeOutOptionPane.INFORMATION_MESSAGE, //Icon new String[]{"blah", "hey",
     * "yo"}, // option buttons "yo"); //seconds before auto "yo"
     *     
     *
     */
    private boolean isSelected = false;

    /**
     * instantiates a timeout option pane
     */
    public TimeOutOptionPane() {
        super();
    }

    /**
     * timeout option pane
     *
     * @param time time limit to wait
     * @param parentComponent display over
     * @param message message to be displayed
     * @param title title of message
     * @param optionType JOptionPane.OPTION
     * @param messageType JOptionPane.TYPE
     * @param options button values
     * @param initialValue default value to select if time runs out
     * @return integer representing user selection
     */
    public int timeoutDialog(final int time, Component parentComponent, Object message, final String title, int optionType, int messageType, Object[] options, final Object initialValue) {
        PRESET_TIME = time;
        JOptionPane pane = new JOptionPane(message, messageType, optionType, null, options, initialValue);
        pane.setInitialValue(initialValue);
        final JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pane.selectInitialValue();
        Thread t = new Thread() {
            @Override
            public void run() {

                for (int i = time; i >= 0; i--) {
                    if (isSelected) {
                        break;
                    }
                    doSleep();
                    if (dialog.isVisible() && i < 300) {
                        dialog.setTitle(title + "  (" + i + " seconds before auto \"" + initialValue + "\")");
                    }
                }
                dialog.setVisible(false);
                dialog.dispose();

            }

            void doSleep() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Log.errorHandler(ex);

                }
            }
        };
        t.setName("Time Out Dialog");
        t.setDaemon(true);
        t.start();
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        isSelected = true;

        if (options == null) {
            if (selectedValue instanceof Integer) {
                return ((Number) selectedValue).intValue();
            }
            return CLOSED_OPTION;
        }
        if (selectedValue.equals("uninitializedValue")) {
            selectedValue = initialValue;
        }
        if (selectedValue.equals("cancel")) {
            return CLOSED_OPTION;
        }

        for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return CLOSED_OPTION;
    }
}
