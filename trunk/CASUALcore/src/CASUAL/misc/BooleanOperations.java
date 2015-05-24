/*BooleanOperations contains accellerators for booleans
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

/**
 * contains accellerators for booleans
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class BooleanOperations {

    /**
     * tests a bolean array to see if it contains a true value
     *
     * @param array to be tested
     * @return true if contains true
     */
    public static boolean containsTrue(boolean[] array) {
        for (boolean b : array) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests a boolean array to see if it contains a falase value
     *
     * @param array to be tested
     * @return true if contains false
     */
    public static boolean containsFalse(boolean[] array) {
        for (boolean b : array) {
            if (!b) {
                return true;
            }
        }
        return false;
    }

    /**
     * tests a boolean array to verify all values are true
     *
     * @param array to be tested
     * @return true if all values are true in array
     */
    public static boolean containsAllTrue(boolean[] array) {
        for (boolean b : array) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    /**
     * tests a boolean array to verify all values are false
     *
     * @param array to be tested
     * @return true if all values are false
     */
    public static boolean containsAllFalse(boolean[] array) {
        for (boolean b : array) {
            if (b) {
                return false;
            }
        }
        return true;
    }
}
