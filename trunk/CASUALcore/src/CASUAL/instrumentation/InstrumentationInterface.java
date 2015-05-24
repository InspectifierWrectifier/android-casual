/*
 * Copyright (C) 2013 adamoutler
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

import CASUAL.misc.MandatoryThread;

/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public interface InstrumentationInterface {
    
    public void updateStatus(String status);
    public void trackThread(MandatoryThread thread);
    
}
