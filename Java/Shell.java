class Shell extends Thread
{
	// required run method for this Shell Thread
	public void run( ) {

        boolean running = true;

        for(int count = 1; running; count++) {
            SysLib.cout("shell[" + count + "]%\n");
            StringBuffer buff = new StringBuffer();
            SysLib.cin(buff);
            String command = buff.toString();
            running = checkExit(command); //Check if exit is inputted

            if(running) {
                executeCmdLine(command);
            }
        }

		// wait for completion then exit back to ThreadOS
		SysLib.join();
		SysLib.cout("Done!\n");
		SysLib.exit();
	}

    //Checks for exit code to terminate shell
    private boolean checkExit(String cmd) {
        return cmd.compareTo("exit") == 0;
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
       } else if(delimSemi.length == 1 && delimAmp.length == 1) {
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