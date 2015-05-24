/*
 *Exception for math operations
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
package CASUAL.misc.math;

/**
 * Exception for math operations
 *
 * @author adamoutler
 */
public class CASUALMathOperationException extends Exception {

    final static long serialVersionUID = 3423411232341L;

    CASUALMathOperationException(String problemDescription) {
        super(problemDescription);
    }
}
