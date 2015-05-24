/*CommitDetails holds information about a commit
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
package com.casual_dev.CommitDescription;

import java.util.Random;

/**
 * collects commit details and returns results through toString() method.
 *
 * @author adamoutler
 */
public class CommitDetails {

    /**
     * true if commit was made to /trunk.
     */
    boolean isTrunk = false;
    /**
     * true if commit was made to a branch.
     */
    boolean isBranch = false;
    /**
     * true if documentation words were used during commit.
     */
    boolean isDocumentation = false;
    /**
     * true if maintenance words were used during commmit.
     */
    boolean isMaintenance = false;
    /**
     * message left by culprit.
     */
    String message = "made changes in such and such.  did this to that. Eated all the CASUAL.";
    /**
     * culprit's name.
     */
    String culprit = "AdamOutler";
    /**
     * files changed during revision.
     */
    String[] files = new String[]{"/trunk/file", "/branch/file"};
    /**
     * revision number from SVN.
     */
    String revision = "999";
    /**
     * changes were made to CASUALCore project.
     */
    boolean modCASUALCore = false;
    /**
     * changes were made to CASCADE project.
     */
    boolean modCASCADE = false;
    /**
     * changes were made to CASPACkager project.
     */
    boolean modCASPACkager = false;
    /**
     * changes were made to branch.
     */
    boolean modBranch = false;

    /**
     * changes were made to CASUALInstrumentation
     */
    boolean modInstrumentation=false;
    
    /**
     * changes were made to Jodin3
     */
     boolean modJodin3;
    private String trunkHeader() {
        String[] credit = new String[]{
            "Just now, " + culprit + " committed Revision " + revision,
            "Moments ago " + culprit + " updated the trunk to " + revision,
            "New Trunk commit by " + culprit + ". Revision " + revision,
            "Source Change by " + culprit + ". Revision " + revision,
            "Revision " + revision + " was created by " + culprit,
            "New Source Code Modification by " + culprit + ". Revision " + revision,
            "The latest source code release by " + culprit + " is revision " + revision,
            "Revision " + revision + " was just revisioned by " + culprit,
            "New source mods. Revision " + revision + " by " + culprit,
            culprit + " just committed Revision " + revision
        };

        String header = credit[getRandom(credit.length)];
        header = "*" + header + "*";
        return header;
    }

    private String branchHeader() {
        String[] credit = new String[]{
            culprit + " is working on something special in Revision " + revision,
            "Its nice to see new features in development by " + culprit,
            "R" + revision + " committed by " + culprit + " is something new",
            culprit + " is developing some unreleased features in Revision " + revision
        };

        String header = credit[getRandom(credit.length)];
        header = "*" + header + "*";
        return header;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isTrunk) {
            sb.append(trunkHeader());
        } else {
            sb.append(branchHeader());
        }
        sb.append("\n\n");
        sb.append(getBodyIntro());

        return sb.toString();
    }

    private int getRandom(int max) {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(max);
        return randomInt;
    }

    private Object getBody() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Object getBodyIntro() {
        StringBuilder sb = new StringBuilder();
        //state the intro
        if (files.length > 6) {
            String[] modified = new String[]{
                "Quite a few files were modified in this commit, including files contained within ",
                files.length + " files were modified in this commit. These changes included revisions to files in ",
                "This is a large commit including " + files.length + " files, with changes to ",
                "A large number of files were changed in Revision " + revision + " including parts of "
            };
            sb.append(modified[getRandom(modified.length)]);
        } else {
            String[] description = new String[]{
                "This is an average size commit and contains patches to ",
                "This commit modifies ",
                "The changes included modicication to ",};
            sb.append(description[getRandom(description.length)]);
        }

        int partsChanged = 0;
        if (this.modBranch) {
            partsChanged++;
        }
        if (this.modCASCADE) {
            partsChanged++;
        }
        if (this.modCASPACkager) {
            partsChanged++;
        }
        if (this.modCASUALCore) {
            partsChanged++;
        }
        if (this.modJodin3){
            partsChanged++;
        }
        if (this.modInstrumentation){
            partsChanged++;
        }

        if (partsChanged == 0) {
            sb.append(" a rather new project. ");
        } else if (partsChanged == 1) {
            if (modBranch) {
                sb.append(" a branch");
            }
            if (modInstrumentation){
                sb.append(" CASUAL Instrumentation");
            }
            if (modJodin3){
                sb.append(" JOdin3");
            }
            if (modCASCADE) {
                sb.append(" CASCADE");
            }
            if (modCASPACkager) {
                sb.append(" CASPACkager");
            }
            if (modCASUALCore) {
                sb.append(" CASUALcore");
            }
            sb.append(". ");
        } else {
            int partsMentioned = 0;
            if (modBranch) {
                sb.append(partsMentioned == partsChanged - 1 ? " and" : "");
                sb.append(" a branch");
                partsMentioned++;
            }

            if (modCASCADE) {
                sb.append(partsMentioned == partsChanged - 1 ? " and" : "");
                sb.append(" CASCADE");
                partsMentioned++;
            }
            if (modCASPACkager) {
                sb.append(partsMentioned == partsChanged - 1 ? "and" : "");
                sb.append(" CASPACkager");
                partsMentioned++;
            }
            if (modCASUALCore) {
                sb.append(partsMentioned == partsChanged - 1 ? " and" : "");
                sb.append(" CASUALcore");
                partsMentioned++;
            }
            sb.append(". ");
        }

        if (this.isDocumentation && this.isMaintenance) {
            String[] description = new String[]{
                "This commit not only includes upgrades and fixes, but also documentation.",
                culprit + " refined procedures and included documentation.",
                "Not only did " + culprit + " refine the code, he also included documentation."};
            sb.append(description[getRandom(description.length)]);
        } else if (this.isDocumentation) {
            String[] description = new String[]{
                "This code release included documentation.",
                "The source is documented."};
            sb.append(description[getRandom(description.length)]);
        } else if (this.isMaintenance) {
            String[] description = new String[]{
                "It's always nice to see something fixed.",
                "This code release maintained existing features and enhanced them.",
                "The best part of this release is knowing that things will work better"};
            sb.append(description[getRandom(description.length)]);
        }

        sb.append(" ");
        String[] commitInfo = new String[]{
            culprit + " had this to say about the commit ",
            culprit + " left this message with the code ",
            "When " + culprit + " committed the changes, he left the following message ",};
        sb.append(commitInfo[getRandom(commitInfo.length)]);
        sb.append("\n");
        sb.append("_\"").append(message).append("\"_");
        sb.append("\n");
        String[] closing = new String[]{
            "You can read more about Revision " + revision + " at this link:",
            "Full details are available here:",
            "If you'd like to read more, you can read about it here:",};
        sb.append(closing[getRandom(closing.length)]).append(" ");
        sb.append("https://code.google.com/p/android-casual/source/detail?r=").append(revision);

        sb.append("\n\n");

        if (modCASUALCore) {
            String[] about = new String[]{
                "The CASUALCore aka. \"The Core\" is responsible for several things including providing base libraries for all other projects.  Changes to The Core make changes throughout the CASUAL project. These are generally important changes. ",
                "CASUALCore provides the CASUAL which most people are familiar with.  The Core also provides libraries for other CASUAL projects.  These changes make changes throught the CASUAL project. ",
                "Whenever a change occurs in CASUALCore, these changes ripple throughout the entire project.  This is the reason it is also known as \"The Core\".  When changes occur to The Core, they not only affect CASUAL, bu also CASCADE, CASPACkager and just about everything else. "};
            sb.append(about[getRandom(about.length)]).append("You can read more about CASUAL here: http://casual-dev.com").append("\n");
        }
        if (this.modCASCADE) {
            String[] about = new String[]{
                "CASCADE provides a method of packaging hacks into CASPACs and CASUALs.  CASUAL is what most people see as the main application.  CASCADE provides a method of creating distributable CASUALs",
                "CASCADE is the CASUAL IDE.  You can use CASCADE to create CASUAL and CASPACs which people can use.  The CASCADE could always use some work, but it does what's on the tin.",
                "CASCADE stands for CASUAL's Automated Scripting Action Development Environment GUI.  It is used to create CASPACs and CASUALs.  A CASPAC is a distributable hack without an attached CASUAL application."};
            sb.append(about[getRandom(about.length)]).append("You can read more about CASCADE here: http://casual-dev.com/cascade/ ").append("\n");
        }
        if (this.modCASPACkager) {
            String[] about = new String[]{
                "The CASPACkager packages CASPACs.  The CASPAC is basically a zip file which provides instructions and resources to CASUAL. As a side-note, the CASPACkager can also be used to modify package during packageing",
                "The CASPACkager is a very useful command-line utility which can be used as a part of a developer's packaging system. It turns CASPACs into CASUALs which the developer can distribute",
                "The CASPACKager does what the name suggests. When fed a CASPAC, it packages it into a CASUAL. For the uninitiated, a CASPAC is a set of instructions and resources which contain hacks and exploits"};
            sb.append(about[getRandom(about.length)]).append(" You can read moreabout CASPACkager here: http://casual-dev.com/caspackager/").append("\n");
        }
        sb.append("\n");
        sb.append("Thanks ").append(culprit).append("!");

        return sb.toString();
    }
}
