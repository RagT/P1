class Shell extends Thread
{
    public Shell() {}
    public Shell(String[] args) {}

    // required run method for this Shell Thread
    public void run( ) {
        int count = 1;
        while(true) {
            StringBuffer buff = new StringBuffer();
            SysLib.cout("Shell[" + count + "]% ");
            SysLib.cin(buff);
            String command = new String(buff);
            if(command.compareTo("exit") == 0) {
                break;
            }
            executeCmdLine(command);
            count++;
        }

        SysLib.cout("Done!\n");
        SysLib.exit();
    }

    // Takes in a string that contains a single command to execute and carries it out.
    private void execute(String command) {
        String[] args = SysLib.stringToArgs(command);
        if(SysLib.exec(args) > 0) {
            SysLib.join(); //Wait for child completion
        } else {
            SysLib.cerr("Cannot execute command: " + command + "\n");
        }
    }

    private void executeCmdLine(String cmdLine){
        String[] delimSemi = cmdLine.split(";");
        String[] delimAmp = cmdLine.split("&");
        if(delimSemi.length > 1) {
            sequentialExe(delimSemi);
        }
        else if(delimAmp.length > 1) {
            parallelExe(delimAmp);
        } else {
            execute(cmdLine);
        }
    }

    //Sequentially runs commands provided in cmds array
    private void sequentialExe(String[] cmds){
        for(String cmd : cmds) {
            String[] delimAmp = cmd.split("&");
            if(delimAmp.length > 1) {
                parallelExe(delimAmp);
            } else {
                execute(cmd);
            }
        }
    }

    //Concurrently runs programs specified in cmds array
    private void parallelExe(String[] cmds) {
        int numProcesses = 0;
        for(String cmd: cmds) {
            String[] args = SysLib.stringToArgs(cmd);
            if(SysLib.exec(args) >= 0) {
                numProcesses++;
            } else {
                SysLib.cerr("Cannot execute command: " + cmd + "\n");
            }
        }
        for(int i = 0; i < numProcesses; i++) {
            SysLib.join();
        }
    }
}