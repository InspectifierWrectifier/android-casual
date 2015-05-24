package com.casual_dev.libpitX;
/*PitOutputStream provides a set of tools designed to assist with reading PIT files
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
import java.io.OutputStream;

/**
 * PitOutputStream provides a set of tools designed to assist with reading PIT
 * files Original Files may be found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by: Adam Outler
 *
 * @author Benjamin Dobell
 */
public class PitOutputStream {

    /**
     * OutputStream used for this class
     */
    private final OutputStream outputStream;
    /**
     * buffere used to convert Java signed int to C unsigned int
     */
    private final byte[] writeBuffer = new byte[4];

    /**
     * creates an OutputStream for a PIT file
     *
     * @see OutputStream
     * @param outputStream outputstream to set 
     */
    public PitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * writes an integer as four bytes to the outputStream. C Unsigned int.
     *
     * @param value an integer or four bytes to be written
     * @see OutputStream
     * @throws IOException {@inheritDoc}
     */
    public void writeInt(int value) throws IOException {
        writeBuffer[0] = (byte) (value & 0xFF);
        writeBuffer[1] = (byte) ((value >> 8) & 0xFF);
        writeBuffer[2] = (byte) ((value >> 16) & 0xFF);
        writeBuffer[3] = (byte) (value >> 24);

        outputStream.write(writeBuffer);
    }

    /**
     * writes a short value as two bytes to the OutputStream
     *
     * @see OutputStream
     * @param value short value to be written
     * @throws IOException {@inheritDoc}
     */
    public void writeShort(short value) throws IOException {
        writeBuffer[0] = (byte) (value & 0xFF);
        writeBuffer[1] = (byte) (value >> 8);

        outputStream.write(writeBuffer, 0, 2);
    }

    /**
     * writes a parameterized buffer to the outputstream
     *
     * @param buffer the data.
     * @param offset the start offset in the data.
     * @param length the number of bytes to write.
     * @see OutputStream {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public void write(byte[] buffer, int offset, int length) throws IOException {
        outputStream.write(buffer, offset, length);
    }
}
