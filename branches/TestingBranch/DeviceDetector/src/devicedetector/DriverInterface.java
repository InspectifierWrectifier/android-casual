/*
 * Copyright (C) 2013 Logan Ludington loglud@casual-dev.org
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package devicedetector;

import devicedetector.Windows.Cmd.CmdInterface;

/**
 *
 * @author loganludington
 */
public class DriverInterface {
    private final String PID;
    
    public DriverInterface(String PID) {
        this.PID = PID;
    }
    
    public boolean queryInstall() {
    CmdInterface cmd = new CmdInterface("cmd /C reg.exe /Query "
            + "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
            + PID);
    cmd.run();
    String outString = cmd.getOuputString();
    if(outString.contains("ERROR"))
        return false;
    else
        return true;
}
    
    
}
