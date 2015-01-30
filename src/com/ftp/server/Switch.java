package com.ftp.server;

import java.util.ArrayList;
import java.util.List;

public class Switch {
	 
	   private List<Command> history = new ArrayList<Command>();
	 
	   public Switch() {
	   }
	 
	   public void storeAndExecute(Command cmd) {
	      this.history.add(cmd); // optional 
	      cmd.execute();        
	   }
	}