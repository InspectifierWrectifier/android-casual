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



package CASUAL.network;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author adamoutler
 */
public class NetworkProperties {
    Properties   p = new Properties();
    final String networkName;

    public NetworkProperties() {
        networkName = "";
    }

    public NetworkProperties(String location) throws IOException {
        networkName = location;
        setProperties(location);
    }

    private void setProperties(String location) throws MalformedURLException, IOException {
        URL         url = new URL(location);
        InputStream is  = url.openStream();

        p.load(is);
        is.close();
    }

    public Properties getProperties() {
        return p;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
