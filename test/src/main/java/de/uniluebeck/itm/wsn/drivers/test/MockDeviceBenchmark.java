package de.uniluebeck.itm.wsn.drivers.test;

import com.google.common.io.Closeables;
import com.google.inject.Guice;
import de.uniluebeck.itm.tr.util.ExecutorUtils;
import de.uniluebeck.itm.tr.util.Logging;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallbackAdapter;
import de.uniluebeck.itm.wsn.drivers.factories.DeviceFactory;
import de.uniluebeck.itm.wsn.drivers.factories.DeviceType;
import de.uniluebeck.itm.wsn.drivers.mock.MockModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MockDeviceBenchmark {

	static {
		Logging.setLoggingDefaults();
	}
	
	private static final Logger log = LoggerFactory.getLogger(MockDeviceBenchmark.class);
	
	public static void main(String[] args) {

		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		DeviceFactory deviceFactory = Guice.createInjector(new MockModule()).getInstance(DeviceFactory.class);
		final Device device = deviceFactory.create(executorService, DeviceType.MOCK);

		device.send(new byte[]{1,2,3}, 5000, new OperationCallbackAdapter<Void>() {

			@Override
			public void onSuccess(final Void result) {
				log.info("success");
				Closeables.closeQuietly(device);
				ExecutorUtils.shutdown(executorService, 100, TimeUnit.MILLISECONDS);
			}

			@Override
			public void onFailure(final Throwable throwable) {
				log.error("error: {}", throwable);
				Closeables.closeQuietly(device);
				ExecutorUtils.shutdown(executorService, 100, TimeUnit.MILLISECONDS);
			}
		});
	}

}
