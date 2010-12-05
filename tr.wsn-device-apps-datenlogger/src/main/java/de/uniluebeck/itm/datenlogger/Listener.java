package de.uniluebeck.itm.datenlogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Listener extends Thread{
    boolean notStop = true;
    Datenlogger logger;
    
    public Listener(Datenlogger datenlogger){
		logger = datenlogger;
	}
    
    public void run(){
        while(notStop){
            try {
                Thread.sleep(1000);
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String input = in.readLine();
                if(input.startsWith("-klammer_filter")){
                	String delims = " ";
            		String[] tokens = input.split(delims);
                	logger.add_klammer_filter(tokens[1]);
                }else if(input.startsWith("-regex_filter")){
                	String delims = " ";
            		String[] tokens = input.split(delims);
                	logger.add_regex_filter(tokens[1]);
                }else if(input.equals("stoplog")){
                	logger.stoplog();
                	notStop = false;
                }
                else if(input.startsWith("-loction")){
                	String delims = " ";
            		String[] tokens = input.split(delims);
                	logger.setLocation(tokens[1]);
                }
                else if(input.startsWith("exit")){
                	notStop = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }          
        }
    }
}