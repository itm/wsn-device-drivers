package de.uniluebeck.itm.wsn.drivers.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.io.SendOutputStreamWrapper;


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
	public DeviceModule(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	@Override
	protected void configure() {		

		PipedInputStream driverInputStream = new PipedInputStream();
		PipedOutputStream pipeOutputStreamToDriverInputStream = new PipedOutputStream();

		PipedOutputStream driverOutputStream = new PipedOutputStream();
		PipedInputStream pipeInputStreamFromDriverOutputStream = new PipedInputStream();

		try {
			driverInputStream.connect(pipeOutputStreamToDriverInputStream);
			driverOutputStream.connect(pipeInputStreamFromDriverOutputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		bind(InputStream.class).annotatedWith(Names.named("driverInputStream")).toInstance(driverInputStream);
		bind(OutputStream.class).annotatedWith(Names.named("driverOutputStream")).toInstance(driverOutputStream);

		bind(OutputStream.class)
				.annotatedWith(Names.named("pipeOutputStreamToDriverInputStream"))
				.toInstance(pipeOutputStreamToDriverInputStream);

		bind(InputStream.class)
				.annotatedWith(Names.named("pipeInputStreamFromDriverOutputStream"))
				.toInstance(pipeInputStreamFromDriverOutputStream);

		bind(ExecutorService.class).toInstance(executorService);
		bind(TimeLimiter.class).toInstance(new SimpleTimeLimiter(executorService));
		bind(OutputStream.class).to(SendOutputStreamWrapper.class);
	}

}
