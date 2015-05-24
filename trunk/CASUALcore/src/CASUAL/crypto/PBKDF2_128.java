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
package CASUAL.crypto;

import CASUAL.Log;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author adamoutler
 */
public class PBKDF2_128 {

    /**
     * provides a one-way hash on a password.
     * 
     *
     * @param input your password
     * @param salt salt to be used
     * @return PBKDF2 with HMAC SHA1 password
     */
    public  byte[] oneWayHash(char[] input, String salt) {
        if (null==salt|| salt.isEmpty()){
            salt="--salt--";
        }
        try {
            int maxSecurity = Cipher.getMaxAllowedKeyLength("AES");
            Log.level4Debug("The maximum security allowed on this system is AES " + maxSecurity);
            if (maxSecurity > 128) {
                maxSecurity = 128;
            }
            Log.level4Debug("For the sake of compatibility with US Import/Export laws we are using AES " + maxSecurity);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keyspec = new PBEKeySpec(input, salt.getBytes(), 100000, maxSecurity);
            Key key = factory.generateSecret(keyspec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            Log.errorHandler(ex);
        } catch (InvalidKeySpecException ex) {
            Log.errorHandler(ex);
        }
        return null;
    }
    
}
