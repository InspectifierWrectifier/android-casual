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
package CASUAL.instrumentation;

import CASUAL.instrumentation.ModeTrackerInterface.Mode;

/**
 * Provides a way to set the mode for CASUAL for logging and other projects for
 * display.
 *
 * @author adamoutler
 */
public class Track {

    public static ModeTrackerInterface track = new ModeTrackerDefaultImpl();

    /*
     *redirects output to a new ModeTracker implementation
     */
    public static void setTrackerImpl(ModeTrackerInterface modeTracker) {
        track = modeTracker;
    }

    /*
     * sets the mode for CASUAL logging and UI usage. 
     */
    public static void setMode(Mode mode) {
        track.setMode(mode);
    }

}
