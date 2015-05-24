/*CFAutoRootTableException is thrown when the table does not match expected values from CFAutoRoot site
 *Copyright (C) 2015  Adam Outler <adamoutler@gmail.com>
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

package CASUAL.network.CFAutoroot;

/**
 * thrown when the table does not match expected values from CFAutoRoot site
 * @author Adam Outler adamoutler@gmail.com
 */
public class CFAutorootTableException extends Exception {
    private static final long serialVersionUID = 1L;

    public CFAutorootTableException(String tables_On_autorootchainfireeu_changed) {
        System.out.println(tables_On_autorootchainfireeu_changed);
        System.out.println("Tables have changed on CFAutoRoot.");
    }
    
}
