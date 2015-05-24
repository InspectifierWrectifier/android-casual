package CASUAL;
/*CASUALSettings decodes args and creates an object used for CASUAL settings
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
import java.io.File;


/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALSettings {

    
    
    
    private CASUALMode CASPACType = CASUALMode.CASUAL;
    private File caspacLocation;
    private boolean useGUI = false;
    private boolean execute = false;
    private String executeCommand = "";
    private String password = "";
    private MonitorMode monitor=MonitorMode.ADB;

    private void reset() {
        this.CASPACType = CASUALMode.CASUAL;
        this.caspacLocation = null;
        this.useGUI = false;
        this.execute = false;
        this.executeCommand = "";
        this.password = "";
    }



    private void setDefaultCASUALOperation() {
        this.useGUI = true;
    }

    /**
     *Takes command-line arguments and turns them into a CASUALSettings object. 
     * @param args commands to be processed. 
     */
    public void checkArguments(String[] args) {
        reset();
        if (args == null || args.length == 0) {
            setDefaultCASUALOperation();
            return;
        }
        for (int i = 0; i < args.length; i++) {

            /**
             * if the previous argument set the EXIT mode, then stop parsing
             * arguments.
             */
            if (CASPACType.equals(CASUALMode.EXIT)) {
                return;
            }
            //begin argument parsing
            try {
                String check = lower(args[i]);
                //exit switches
                if (check.equals("help") || check.equals("h") || check.equals("?")) {
                    argReaction(ArgOptions.HELP, "");
                } else if (check.equals("license")) {
                    argReaction(ArgOptions.LICENSE, "");

                    //argument-not-required switches
                } else if (check.equals("gui") || check.equals("g")) {
                    argReaction(ArgOptions.GUI, "");
                } else if (check.equals("nosound")) {
                    argReaction(ArgOptions.NOSOUND, "");
                    
                    //start mode arguments
                } else if (check.equals("adb")) {
                    this.monitor=MonitorMode.ADB;
                } else if (check.equals("heimdall")) {
                    this.monitor=MonitorMode.HEIMDALL;
                } else if (check.equals("fastboot")) {
                    this.monitor=MonitorMode.FASTBOOT;
                    //switches with argument required
                } else if (check.equals("temp") || check.equals("t")) {
                    argReaction(ArgOptions.TEMP, getNextArg(++i, args));
                } else if (check.equals("password") || check.equals("p")) {
                    argReaction(ArgOptions.PASSWORD, getNextArg(++i, args));
                } else if (check.equals("caspac") || check.equals("c")) {
                    argReaction(ArgOptions.CASPAC, getNextArg(++i, args));
                } else if (check.equals("execute") || check.equals("e")) {
                    argReaction(ArgOptions.EXECUTE, getNextArg(++i, args));

                    //error
                } else {
                    argReaction(ArgOptions.INVALID, getNextArg(i++, args));
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                argRequiresParameterExit(args, i);
            }
        }
    }

    private String getNextArg(int i, String args[]) {
        if (args[i] == null || args[i].startsWith("-")) {
            argRequiresParameterExit(args, i);
        }
        return args[i];
    }

    private void argRequiresParameterExit(String[] args, int i) {
        Log.level0Error("Argument " + args[i - 1] + " requires a parameter");
        showHelpMessageAndExit();
    }

    private void argReaction(ArgOptions action, String arg) {
        switch (action) {
            case HELP:
                showHelpMessageAndExit();
                break;
            case LICENSE:
                showLicenseAndExit();
                break;
            case TEMP:
                setTempFolder(arg);
                break;
            case PASSWORD:
                this.setPassword(arg);
                break;
            case CASPAC:
                setCASPACMode(arg);
                break;
            case GUI:
                setUseGUI(true);
                break;
            case NOSOUND:
                AudioHandler.useSound = false;
                break;
            case EXECUTE:
                setupExecuteMode(arg);
                break;
            default:
                Log.level0Error("Invalid Option: " + arg);
                showHelpMessageAndExit();
                break;
        }

    }

    private void setupExecuteMode(String arg) {
        setCASPACType(CASUALMode.EXECUTE);
        this.setExecute(true);
        this.setExecuteCommand(arg);
    }

    private void setCASPACMode(String arg) {
        this.setCaspacLocation(new File(arg));
        if (!caspacLocation.isFile()) {
            Log.level0Error("ERROR: CASPAC Not Found Please spcify a valid CASPAC");
            setCASPACType(CASUALMode.EXIT);
        } else {
            setCASPACType(CASUALMode.CASPAC);

            Log.level4Debug("Setting CASPAC location to " + getCaspacLocation().getAbsolutePath());
        }
    }

    /**
     * checkArgOptionsArgs is a primary switch before any real actions happen.
     * Here we check for switches that will either change the mode of CASUAL or
     * display something quick and exit.
     *
     * @param args
     * @return true if shutdown is commanded;
     */
    
    /**
     * getMonitorMode is used to determine the appropriate mode for CASUAL monitoring.
     * ADB, Heimdall or Fastboot
     * @return mode to be monitored.
     */
    public MonitorMode getMonitorMode(){
        return this.monitor;
    }
    
    /**
     * @return the CASPACType
     */
    public CASUALMode getCASPACType() {
        return CASPACType;
    }

    /**
     * @param CASPACType the CASPACType to set
     */
    public void setCASPACType(CASUALMode CASPACType) {
        this.CASPACType = CASPACType;
    }

    /**
     * @return the caspacLocation
     */
    public File getCaspacLocation() {
        return caspacLocation;
    }

    /**
     * @param caspacLocation the caspacLocation to set
     */
    public void setCaspacLocation(File caspacLocation) {
        this.caspacLocation = caspacLocation;
    }

    /**
     * @return the useGUI
     */
    public boolean isUseGUI() {
        return useGUI;
    }

    /**
     * @param useGUI the useGUI to set
     */
    public void setUseGUI(boolean useGUI) {
        this.useGUI = useGUI;
    }

    /**
     * @return the execute
     */
    public boolean isExecute() {
        return execute;
    }

    /**
     * @param execute the execute to set
     */
    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    /**
     * @return the executeCommand
     */
    public String getExecuteCommand() {
        return executeCommand;
    }

    /**
     * @param executeCommand the executeCommand to set
     */
    public void setExecuteCommand(String executeCommand) {
        this.executeCommand = executeCommand;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    void setTempFolder(String tempFolder) {
//TODO: make this work again.  this is inop. 
    }

    private void showLicenseAndExit() {
        Log.level2Information("\n" + "    This program is free software: you can redistribute it and/or modify\n" + "    it under the terms of the GNU General Public License as published by\n" + "    the Free Software Foundation, either version 3 of the License, or\n" + "    (at your option) any later version.\n" + "\n" + "    This program is distributed in the hope that it will be useful,\n" + "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" + "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" + "    GNU General Public License for more details.");
        setCASPACType(CASUALMode.EXIT);
    }

    private void showHelpMessageAndExit() {
        Log.level2Information("\n" + " Usage: casual.jar [optional parameters]\n" + " without arguments - Launch the GUI\n" + " [--help] shows this message and exits\n" + " [--license] -shows license and exits\n" + " [--execute/-e \"command\"]-executes any CASUAL command and exits. Launch CASUAL GUI to read about commands" + " [--caspac/-c path_to" + CASUALSessionData.slash + "CASPACzip] -launches CASUAL with a CASPAC" + " [--gui/-g)] - performs actions with a GUI\n");
        setCASPACType(CASUALMode.EXIT);
    }

    private String lower(String arg) {
        return arg.toLowerCase().replace("-", "");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String n = "\n";
        sb.append("Type:").append(this.CASPACType).append(n);
        sb.append("Monitor:").append(this.monitor).append(n);
        sb.append("UseGUI:").append(useGUI).append(n);
        sb.append("executeMode:").append(execute).append(n);
        sb.append("executeCommand:").append(executeCommand).append(n);
        sb.append("password:").append(password).append(n);
        sb.append("CASPACLocation:").append(caspacLocation).append(n);
        return sb.toString();
    }

    /**
     * Options used for arguments.  HELP, LICENSE, TEMP, PASSWORD, CASPAC, GUI, NOSOUND, EXECUTE. INVALID is specified if a argument is not proper.
     */
    public static enum ArgOptions {

        /**
         * command to display help message.
         */
        HELP, /**
         * command to display license.
         */
        LICENSE, /**
         * parameter to set temp folder.
         */
        TEMP, /**
         * parameter to set password for use on CASPAC.
         */
        PASSWORD, /**
         * parameter to set caspac.
         */
        CASPAC, /**
         * parameter to set GUI usage.
         */
        GUI, /**
         * parameter to kill sound usage in CASUAL.
         */
        NOSOUND, /**
         * Parameter to specify a CASUAL language command.
         */
        EXECUTE, /**
         * Default parameter which causes an error in parameters.
         */
        INVALID
    }

    /**
     * Options used to determine the major mode of CASUAL CASUAL, CASPAC, EXECUTE, EXIT.  If a problem is detected EXIT is specified.   CASUAL is default. Execute and CASPAC are specified by commandline args.
     */
    public static enum CASUALMode {

        /**
         *  CASUAL Mode.
         */
        CASUAL, /**
         * CASPAC Mode.
         */
        CASPAC, /**
         * Execute Command mode.
         */
        EXECUTE, /**
         * No mode, just exit.
         */
        EXIT
    }

    /**
     * Initial monitoring mode of CASUAL.
     */
    public static enum MonitorMode {

        /**
         * ADB mode will use ADB tools for monitoring Device Status.
         */
        ADB, /**
         * Heimdall mode will use HeimdallTools for monitoring device status.
         */
        HEIMDALL, /**
         * Fastboot mode will use FastbootTools for monitoring device status.
         */
        FASTBOOT, /**
         * always enable controls.
         */
        NONE
    }
}
