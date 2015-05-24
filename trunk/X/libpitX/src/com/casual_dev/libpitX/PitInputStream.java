package com.casual_dev.libpitX;
/*PitInputStream provides tools used for writing a pit.
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

import java.io.IOException;
import java.io.InputStream;

/**
 * PitInputStream provides tools used for writing a pit. Original Files may be
 * found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by: Adam Outler
 *
 * @author Benjamin Dobell
 */
public class PitInputStream {

    private final InputStream inputStream;

    /**
     * Constructs a PitInputStream
     *
     * @see InputStream
     * @param inputStream PIT as InputStream
     */
    public PitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * reads an int (four bytes) from the input stream
     *
     * @return integer value from four bytes
     * @see InputStream
     * @throws IOException  {@inheritDoc}
     */
    public int readInt() throws IOException {
        return (inputStream.read() | (inputStream.read() << 8) | (inputStream.read() << 16)
                | (inputStream.read() << 24));
    }

    /**
     * reads a short (two bytes) from the inputstream
     *
     * @return short value from two bytes
     * @see InputStream
     * @throws IOException  {@inheritDoc}
     */
    public short readShort() throws IOException {
        return ((short) (inputStream.read() | (inputStream.read() << 8)));
    }

    /**
     * reads parameterized bytes from the InputStream
     *
     * @param buffer byte buffer
     * @param offset bytes to discard
     * @param length number of bytes to read
     * @return value requested from stream, specified by buffer, offset and
     * length
     * @see InputStream
     * @throws IOException  {@inheritDoc}
     */
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return (inputStream.read(buffer, offset, length));
    }

    /**
     * reads a byte from the InputStream
     *
     * @see InputStream
     * @return one byte
     */
    public int read() {
        try {
            return inputStream.read();
        } catch (IOException ex) {
            return -1; //if this happens the whole thing blew up or we are at the end
        }
    }
}
