/*CorruptOdinFileException is thrown when an Odin File is corrupt. 
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
package CASUAL.communicationstools.heimdall.odin;

/**
 * thrown when an Odin File is corrupt.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CorruptOdinFileException extends Exception {

    static final long serialVersionUID = 23456873412341L;

    /**
     * Occurs when a corrupt odin file is processed
     *
     * @param error text to add as description of failure.
     */
    public CorruptOdinFileException(String error) {
        System.out.println("The odin File Is corrupt.");
        System.out.println(error);
    }
}
