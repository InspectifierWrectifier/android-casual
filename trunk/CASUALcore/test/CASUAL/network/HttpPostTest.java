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

import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class HttpPostTest {
    
    public HttpPostTest() {
    }
    
    @Before
    public void setUp() {
    }

    /**
     * Test of post method, of class HttpPostFile.
     * @throws java.io.IOException
     */
    @Test
    public void testPost() throws IOException {
        
        File f=new File("../test/CASUAL/network/build.prop");
        System.out.println(f.getAbsolutePath());
        String url="https://builds.casual-dev.com/availableCaspacs/CASUALComms.php";
        String s=HttpPost.postFile(f, url);
        assert (s.contains("START JSON OUTPUT"));
    }
    
}
