/*LinkedProperties keeps a .properties file in the order it was created even after subsequent updates.
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
package CASUAL.misc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * LinkedProperties allows for a static order to be placed on a properties file
 * rather than random/last-modified order generally used in writing a properties
 * file.
 *
 * @author Adam Outler adamoutler@gmail.com inspiredBy
 * http://stackoverflow.com/questions/1312383/pulling-values-from-a-java-properties-file-in-order
 */
public class LinkedProperties extends Properties {

    static final long serialVersionUID = 34112324234412341L;
    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    /**
     * gets the keys in the properties list
     *
     * @return all the keys in the properties list
     */
    @Override
    public Enumeration<Object> keys() {
        return Collections.<Object>enumeration(keys);
    }

    /**
     * puts a value into the properties list
     *
     * @param key key to set in ordered list
     * @param value value to set ordered list
     * @return the previous value or null if one did not exist.
     */
    @Override
    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }

    /**
     * gets names of the properties in the list
     *
     * @return set of propertiy names
     */
    @Override
    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<String>();

        for (Object key : this.keys) {
            set.add((String) key);
        }

        return set;
    }
}
