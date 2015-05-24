/*
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
package CASUAL.communicationstools.adb.twrprecovery;

/**
 *
 * @author adamoutler
 */
public class OpenRecoveryScript {

    StringBuilder script = new StringBuilder();

    /**
     * adds mount system command
     *
     * @return script in its entirety.
     */
    public String mountSystem() {
        append("mount system");
        return toString();
    }

    /**
     * adds wipedata command
     *
     * @return script in its entirety.
     */
    public String unmountSystem() {
        append("unmount system");
        return toString();
    }

    /**
     * adds wipedata command
     *
     * @return script in its entirety.
     */
    public String wipeData() {
        append("wipe data");
        return toString();
    }

    /**
     * adds wipe cache command
     *
     * @return script in its entirety.
     */
    public String wipeCache() {
        append("wipe cache");
        return toString();
    }

    /**
     * adds wipe dalvik comand
     *
     * @return script in its entirety.
     */
    public String wipeDalvik() {
        append("wipe dalvik");
        return toString();
    }

    /**
     * wipes the contents of the script
     *
     * @return script in its entirety;
     */
    public String clear() {
        script = new StringBuilder();
        return toString();
    }

    /**
     * Adds a command to the script, followed by a \n.
     *
     * @param text command to append
     * @return script in its entirety.
     */
    public String append(String text) {
        script.append(text).append("\n");
        return toString();
    }

    @Override
    public String toString() {
        return script.toString();
    }
}
