package de.uniluebeck.itm.Datenlogger;

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
                if(input.startsWith("-filter")){
                	String delims = " ";
            		String[] tokens = input.split(delims);
                	logger.addfilter(tokens[1]);
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