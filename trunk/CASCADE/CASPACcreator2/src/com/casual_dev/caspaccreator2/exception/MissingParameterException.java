/*MissingParameterException provides a notification that a missing parameter has occurred. 
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
package com.casual_dev.caspaccreator2.exception;

/**
 *
 * @author adamoutler
 */
public class MissingParameterException extends Exception{
    private static final long serialVersionUID = 753243134134134127L;
     public MissingParameterException(String missingParameterName){
         super("Mandatory parameter: \"--"+missingParameterName+ "\" missing.");
     }
}
