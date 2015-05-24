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
package CASUAL.network.CASUALDevIntegration;

import CASUAL.network.NetworkProperties;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * creates a CASUAL Pacakge object.
 *
 * @author adamoutler
 */
public class CASUALPackage {

    final String url;
    private AtomicBoolean validCASPAC = new AtomicBoolean(false);
    private String[] validCASPACExtensions = new String[]{"caspac", "zip"};
    Properties p = new Properties();

    private String developer;
    private String donateTo;
    private String donateLink;
    private String windowTitle;
    private String description = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    private String revision;
    private String supportURL;
    private String uniqueID;
    private String caspacSHA256sum;

    public CASUALPackage(String url) {
        this.url = url;
        try {

            processCASPAC(url);
            validCASPAC.set(true);
            return;
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } catch (NoMetaException ex) {
        }  //no need to catch any of these exceptions.  validCASPAC is set false.
        validCASPAC.set(false);
    }

    /**
     * if not true, this should not be used.
     *
     * @return true if valid
     */
    public boolean isValid() {
        return validCASPAC.get();
    }

    public String getDescritpion() {
        return getDescription();
    }

    /**
     * @return the developer
     */
    public String getDeveloper() {
        return developer;
    }

    /**
     * @return the donateTo
     */
    public String getDonateTo() {
        return donateTo;
    }

    /**
     * @return the donateLink
     */
    public String getDonateLink() {
        return donateLink;
    }

    /**
     * @return the windowTitle
     */
    public String getWindowTitle() {
        return windowTitle;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @return the supportURL
     */
    public String getSupportURL() {
        return supportURL;
    }

    /**
     * @return the uniqueID
     */
    public String getUniqueID() {
        return uniqueID;
    }

    private void processCASPAC(String link) throws MalformedURLException, IOException, NoMetaException {
        String noExtensionLink = getNoExtensionCaspacName(link);
        loadProperties(noExtensionLink);

        // LEAVE BLANK until implemented String description=p.getProperty("","");
    }

    private void loadProperties(String noExtensionLink) throws IOException, NoMetaException {
        NetworkProperties dotMeta = new NetworkProperties();
        try {
            dotMeta = new NetworkProperties(noExtensionLink + ".properties");
        } catch (IOException e) {
        }

        uniqueID = dotMeta.getProperties().getProperty("Script.ID", "");
        if (getUniqueID().isEmpty()) {
            throw new NoMetaException("no script id");
        }
        revision = dotMeta.getProperties().getProperty("Script.Revision", "");
        supportURL = dotMeta.getProperties().getProperty("Script.SupportURL", "");
         description=dotMeta.getProperties().getProperty("CASPAC.Overview", "");
        developer = dotMeta.getProperties().getProperty("Developer.Name", "");
        donateTo = dotMeta.getProperties().getProperty("Developer.DonateToButtonText", "");
        donateLink = dotMeta.getProperties().getProperty("Developer.DonateLink", "");
        windowTitle = dotMeta.getProperties().getProperty("Window.Title", "");
        caspacSHA256sum = dotMeta.getProperties().getProperty("CASPAC.SHA256sum", "");

        System.out.println();
    }

    private String getNoExtensionCaspacName(String filename) {
        for (String extension : validCASPACExtensions) {
            if (filename.toLowerCase().endsWith("." + extension)) {
                filename = filename.replace("." + extension, "");
            }
        }
        return filename;
    }

    @Override
    public String toString() {
        return this.windowTitle + "\nby: " + this.developer
                + "\n SHA256sum:" + caspacSHA256sum
                + "\n Script ID:" + uniqueID
                + "\n revision:" + revision
                + "\n donate to:" + donateTo
                + "\n donations:" + donateLink
                + "\n support: " + supportURL
                + "\n" + description;

    }

    /**
     * @return the caspacSHA256sum
     */
    public String getCaspacSHA256sum() {
        return caspacSHA256sum;
    }
}
