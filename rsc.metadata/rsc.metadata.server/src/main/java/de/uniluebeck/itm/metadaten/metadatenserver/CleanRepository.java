package de.uniluebeck.itm.metadaten.metadatenserver;


import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.persistence.DatabaseToStore;
import de.uniluebeck.itm.persistence.StoreToDatabase;
/**
 * Class deletes old nodes (depending on their timestamp) in the given period
 * @author Toralf Babel
 *
 */
public class CleanRepository extends TimerTask {
	/**Logger*/
	private static Log log = LogFactory.getLog(CleanRepository.class);
	/**timeperiod that the nodes timestamp does have until it will be removed from repository*/
	private int overageperiod;
	/**Timer for regularly cleaning the repository*/
	private final Timer timer = new Timer();
	/**Constructor*/
	public CleanRepository(){
	};
	/**Constructor
	 * @param overagetime time limit -> nodes with older timestamp will be deleted
	 * 
	 */
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
	public int getOverageperiod() {
		return overageperiod;
	}
	public void setOverageperiod(final int overageperiod) {
		this.overageperiod = overageperiod;
	}
	public Timer getTimer() {
		return timer;
	}

}
