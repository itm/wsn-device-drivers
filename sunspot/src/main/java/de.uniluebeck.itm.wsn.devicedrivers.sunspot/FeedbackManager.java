package de.uniluebeck.itm.wsn.devicedrivers.sunspot;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class FeedbackManager {
    private HashMap devices;
    private ant_project p;
    private Thread t;
    private static final Logger log = LogManager.getLogger(FeedbackManager.class);
    private static transient volatile FeedbackManager instance;

      public synchronized static FeedbackManager getInstance(){
        if(instance==null)
            synchronized(FeedbackManager.class){
            if(instance==null){
                instance = new FeedbackManager();
            }
        }
        return instance;
    }

        Runnable host;

    {
        host = new Runnable() {
            @Override
            public void run() {
                String build_path = "." + File.separator + "host" + File.separator + "build.xml";
                ant_project p = new ant_project(build_path);
                p.call_host();

            }
        };
    }

    private FeedbackManager(){
        devices=new HashMap();
    }

    public synchronized void addDevice(SunspotDevice nd) {
        devices.put(nd.getNodeName(),nd);
    }

    public synchronized SunspotDevice getDevice(String nodeName) {
        return (SunspotDevice) devices.get(nodeName);
    }

    public synchronized void start(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("SchedulerService-Thread %d").build());
        scheduler.execute(host);
    }

       public synchronized void stop(){
       t.stop();
    }

}

