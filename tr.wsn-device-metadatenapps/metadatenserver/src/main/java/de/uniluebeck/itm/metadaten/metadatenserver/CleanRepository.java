package de.uniluebeck.itm.metadaten.metadatenserver;


import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.persistence.DatabaseToStore;
import de.uniluebeck.itm.persistence.StoreToDatabase;

public class CleanRepository extends TimerTask {
	private static Log log = LogFactory.getLog(CleanRepository.class);
	Timer timer = new Timer();
	int overageperiod;
	public CleanRepository(){
	};
	public CleanRepository(int overagetime){
		this.overageperiod=overagetime;
	};

	@Override
	public void run() {
		Node node = new Node();
		StoreToDatabase storeDB = new StoreToDatabase();
		DatabaseToStore fromDB = new DatabaseToStore();
        Date actDate = new Date();
        Date olddate = new Date();
        olddate.setTime(olddate.getTime()-overageperiod);
        node.setTimestamp(actDate);
        log.info("Deleting old nodes");
        try {
        	storeDB.deleteoldNodes(olddate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
