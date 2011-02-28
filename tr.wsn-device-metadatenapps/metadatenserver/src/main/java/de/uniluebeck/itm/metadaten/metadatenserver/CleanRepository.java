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
	public CleanRepository(final int overagetime){
		this.overageperiod=overagetime;
	};

	@Override
	public void run() {
		final Node node = new Node();
		final StoreToDatabase storeDB = new StoreToDatabase();
		final DatabaseToStore fromDB = new DatabaseToStore();
		final Date actDate = new Date();
		final Date olddate = new Date();
        olddate.setTime(olddate.getTime()-overageperiod);
        node.setTimestamp(actDate);
        log.info("Deleting old nodes");
        try {
        	storeDB.deleteoldNodes(olddate);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
