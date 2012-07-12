package de.uniluebeck.itm.wsn.drivers.core;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.io.SendOutputStreamWrapper;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Basic setup for a single Device.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class DeviceModule extends AbstractModule {

	private static final int DEFAULT_POOL_SIZE = 3;

	private final ExecutorService executorService;

	public DeviceModule() {
		executorService = Executors.newScheduledThreadPool(DEFAULT_POOL_SIZE,
				new ThreadFactoryBuilder().setNameFormat("DeviceModule-Thread %d").build()
		);
	}

	@Inject
	public DeviceModule(final ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	protected void configure() {

		PipedInputStream driverInputStream = new PipedInputStream();
		PipedOutputStream pipedOutputStreamToDriverInputStream = new PipedOutputStream();

		PipedOutputStream driverOutputStream = new PipedOutputStream();
		PipedInputStream pipedInputStreamFromDriverOutputStream = new PipedInputStream();

		try {
			driverInputStream.connect(pipedOutputStreamToDriverInputStream);
			driverOutputStream.connect(pipedInputStreamFromDriverOutputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		bind(PipedInputStream.class).annotatedWith(Names.named("driverInputStream")).toInstance(driverInputStream);
		bind(PipedOutputStream.class).annotatedWith(Names.named("driverOutputStream")).toInstance(driverOutputStream);

		bind(PipedOutputStream.class)
				.annotatedWith(Names.named("pipedOutputStreamToDriverInputStream"))
				.toInstance(pipedOutputStreamToDriverInputStream);

		bind(PipedInputStream.class)
				.annotatedWith(Names.named("pipedInputStreamFromDriverOutputStream"))
				.toInstance(pipedInputStreamFromDriverOutputStream);

		bind(ExecutorService.class).toInstance(executorService);
		bind(TimeLimiter.class).toInstance(new SimpleTimeLimiter(executorService));
		bind(OutputStream.class).to(SendOutputStreamWrapper.class);
	}

}
