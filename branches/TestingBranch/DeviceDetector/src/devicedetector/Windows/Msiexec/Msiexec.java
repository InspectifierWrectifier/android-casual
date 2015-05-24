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
package devicedetector.Windows.Msiexec;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *An object representation of the MSI file.
 * @author loganludington
 */
public class Msiexec {
    
    //Operation varibles

    /**
     * Different operations that can be performed through msiexec.
     */
    public enum Operation {

        /**
         * Corresponds to the /i command for msiexec.
         */
        INSTALL,

        /**
         * Corresponds to the /f command for msiexec.
         */
        REPAIR,

        /**
         * Corresponds to the /x command for msiexec.
         */
        UNINSTALL
    }

    /**
     * Holder for the operation to be performed on the current MSI.
     */
    public Operation operation = Operation.INSTALL;
    
    //Repair Options

    /**
     * Different repair operations that can be performed through msiexec.
     */
    public enum RepairOptions {

        /**
         * Corresponds to the <b>p</b> repair option for msiexec.
         */
        ONLY_IF_MISSING,

        /**
         * Corresponds to the <b>o</b> repair option for msiexec.
         */
        OLDER_VERSION,

        /**
         * Corresponds to the <b>e</b> repair option for msiexec.
         */
        OLDER_OR_EQUAL_VERSION,

        /**
         *  Corresponds to the <b>d</b> repair option for msiexec.
         */
        DIFFERENT_VERSION,

        /**
         *  Corresponds to the <b>c</b> repair option for msiexec.
         */
        CHECKSUM_MISMATCH,

        /**
         *  Corresponds to the <b>a</b> repair option for msiexec.
         */
        ALL_FILES
    }

    /**
     * Holder for the repair operation to be performed on the current MSI.
     */
    public RepairOptions repairOptions;
    

    /**
     * Holder for whether or not the install will be quiet on the current MSI.
     * <p>
     * <b>quiet = true</b>
     * <br>
     * There will be no gui.
     * <p>
     * <b>quiet = false</b>
     * <br>
     * There will be a full gui.
     * 
     */
        public boolean quite = true;
    
    //Logging Options

    /**
     * Different logging operations that can be performed through msiexec.
     */
    public enum LoggingOption {

        /**
         * Corresponds to the <b>i</b>  logging option for msiexec.
         */
        STATUS_MESSAGES,

        /**
         * Corresponds to the <b>w</b>  logging option for msiexec.
         */
        NONFATAL_WARNINGS,

        /**
         * Corresponds to the <b>e</b>  logging option for msiexec.
         */
        ALL_ERROR_MESSAGES,

        /**
         * Corresponds to the <b>a</b>  logging option for msiexec.
         */
        STARTUP_OF_ACTIONS,

        /**
         * Corresponds to the <b>r</b>  logging option for msiexec.
         */
        ACTION_SPECIFIC_RECORDS, 

        /**
         * Corresponds to the <b>u</b>  logging option for msiexec.
         */
        USER_REQUESTS, 

        /**
         * Corresponds to the <b>c</b>  logging option for msiexec.
         */
        INITIAL_USER_INTERFACE_PARAMS, 

        /**
         * Corresponds to the <b>m</b> and <b>o</b>  logging option for msiexec.
         */
        OUT_OF_MEMORY,

        /**
         * Corresponds to the <b>p</b>  logging option for msiexec.
         */
        TERMINAL_PROPS,

        /**
         * Corresponds to the <b>v</b>  logging option for msiexec.
         */
        VERBOSE,

        /**
         * Corresponds to the <b>+</b>  logging option for msiexec.
         */
        APPEND,

        /**
         * Corresponds to the <b>!</b>  logging option for msiexec.
         */
        FLUSH,

        /**
         * Corresponds to the <b>*</b>  logging option for msiexec.
         */
        ALL
        }
     /**
     * Holder for the logging operations to be performed on the current MSI.
     */
    private Set<LoggingOption> loggingOptions = new HashSet<>();


    
   /**
     * Holder for whether or not the install will have logging on the current MSI actions.
     * <p>
     * <b>loggingEnabled = true</b>
     * <br>
     * A log file will be generated and then can be printed.
     * <p>
     * <b>loggingEnabled =  false</b>
     * <br>
     * A log file will be generated and then cannot be printed. 
     */
    public boolean loggingEnabled =false;

    /**
     * Location of the log file that will be created form the MSI run.
     */
    public File logFile;
    
    //MSI String Location

    /**
     *Location of the MSI file that will be run.
     */
        public final String msiFile;


    /**
     *
     * @param msiFile Location of the MSI file.
     * <p>
     * Constructor used to initiate a MSI file.
     */
    public Msiexec(String msiFile) {
        if (new File(msiFile).exists())
            this.msiFile= msiFile;
        else
            this.msiFile = null;
    }

    /**
     *
     * @return Logging options for the MSI.
     */
    public Set<LoggingOption> getLoggingOptions() {
        return loggingOptions;
    }

    /**
     *
     * @param loggingOptions Hand in as many Logging options as needed and they will be
     * added to the list.
     */
    public void setLoggingOptions(LoggingOption... loggingOptions) {
        this.loggingOptions.addAll(Arrays.asList(loggingOptions));
    }
    
    /**
     *Will clear all logging options for MSI
     */
    public void clearLoggingOptions() {
        this.loggingOptions.clear();
    }
    
}
