import java.io.*;
import java.util.*;

public class Shell extends Thread {

	//stores full command
	private String cmdLine;

	//Shell default no argument constructor
	public Shell() {
		cmdLine = "";
	}	

	public void run() {


		SysLib.cout("Done");
		Syslib.exit();
	}
}