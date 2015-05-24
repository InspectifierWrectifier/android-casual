/*Creates a LoremIpsum message
 *Copyright (C) 2014 CASUAL-Dev or Adam Outler
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

package com.casual_dev.zodui.messagepanel;

import CASUAL.CASUALMessageObject;
import CASUAL.iCASUALUI;
import com.casual_dev.zodui.contentpanel.ZodPanelContent;
import java.util.Random;

/**
 * creates a LoremIpsum message
 * @author adamoutler
 */
public class LoremIpsumMessage {
     /**
     * Creates a random message 
     * @return random CASUALMessageObject
     */
    public static CASUALMessageObject createLoremIpsumMessage() {
        ZodPanelContent zpc = new ZodPanelContent();
        Random rn = new Random();
        int min = 0;
        int max = iCASUALUI.MessageCategory.values().length-1;
        int type = rn.nextInt(max - min + 1) + min;
        int tmax=randomTitle.length-1;
        int titlenumber = rn.nextInt(tmax - min + 1) + min;
        int mmax=randomMessage.length-1;
        int messageNumber=rn.nextInt(mmax - min + 1) + min;       
        String titl=randomTitle[titlenumber];
        String messg=randomMessage[messageNumber];
        CASUALMessageObject cmo=new CASUALMessageObject(titl,messg);
        cmo.category=iCASUALUI.MessageCategory.values()[type];
        return cmo;
    }
    static String[] randomTitle = new String[]{"de Finibus Bonorum et Malorum",
        "de Finibus Bonorum et Malorum",
        "Maecenas suscipit hendrerit feugiat.",
        " Integer molestie leo at ante tristique congue.",
        " Aliquam in augue fringilla diam sodales fringilla."};

    static String[] randomMessage = new String[]{
        "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt",
        "Morbi id nibh sapien. Duis vel mi pellentesque massa sollicitudin gravida. Maecenas eu nisi dictum, volutpat metus feugiat, convallis tellus. Sed laoreet ipsum vel dolor varius gravida.",
        "Donec lacinia, quam a convallis convallis, felis lacus gravida magna, ac blandit mi augue at purus. ",
        "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",
        "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."};

}
