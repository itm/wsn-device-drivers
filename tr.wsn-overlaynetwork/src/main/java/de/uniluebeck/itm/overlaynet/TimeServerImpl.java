package de.uniluebeck.itm.overlaynet;

import org.apache.thrift.TException;

public class TimeServerImpl implements TimeServer.Iface {

	@Override
	public long time() throws TException {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis();
		System.out.println("time() called: " + time);
		return time;
	}

}
