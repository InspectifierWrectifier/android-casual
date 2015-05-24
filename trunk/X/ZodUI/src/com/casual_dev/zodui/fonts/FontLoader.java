/*Handles font loading in ZodUI
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
package com.casual_dev.zodui.fonts;

import java.io.InputStream;
import javafx.scene.text.Font;

/**
 *
 * @author adamoutler
 */
public class FontLoader {

    final static Font openEmoji;

    static {
        InputStream is = new FontLoader().getClass().getResourceAsStream("/com/casual_dev/zodui/fonts/OpenEmojiFont.ttf");
        openEmoji = Font.loadFont(is, 40);
        
        
    }

    public static Font getOpenEmoji() {
        return openEmoji;
    }

}
