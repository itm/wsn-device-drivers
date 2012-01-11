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

import de.uniluebeck.itm.wsn.drivers.core.concurrent.IdleRunnable;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.InputStreamCopyRunnable;
import de.uniluebeck.itm.wsn.drivers.core.io.SendOutputStreamWrapper;


/**
 * Basic setup for a single Device.
 * 
 * @author Malte Legenhausen
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
		PipedInputStream inputStream = new PipedInputStream();
		PipedOutputStream outputStream = new PipedOutputStream();
		try {
			inputStream.connect(outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		bind(ExecutorService.class).toInstance(executorService);
		bind(TimeLimiter.class).toInstance(new SimpleTimeLimiter(executorService));
		bind(Runnable.class).annotatedWith(IdleRunnable.class).to(InputStreamCopyRunnable.class);
		bind(InputStream.class).toInstance(inputStream);
		bind(OutputStream.class).annotatedWith(IdleRunnable.class).toInstance(outputStream);
		bind(OutputStream.class).to(SendOutputStreamWrapper.class);
	}

}
