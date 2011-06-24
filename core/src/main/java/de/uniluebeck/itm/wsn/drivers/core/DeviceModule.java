package de.uniluebeck.itm.wsn.drivers.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.async.Idle;
import de.uniluebeck.itm.wsn.drivers.core.async.InputStreamCopyRunnable;

public class DeviceModule extends AbstractModule {

	private final ScheduledExecutorService executorService;
	
	public DeviceModule() {
		executorService = Executors.newScheduledThreadPool(3, 
				new ThreadFactoryBuilder().setNameFormat("GenericDeviceExample-Thread %d").build()
		);
	}
	
	@Inject
	public DeviceModule(ScheduledExecutorService executorService) {
		this.executorService = executorService;
	}
	
	@Override
	protected void configure() {
		PipedInputStream inputStream = new PipedInputStream();
		PipedOutputStream outputStream = new PipedOutputStream();
		try {
			inputStream.connect(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		TimeLimiter timeLimiter = new SimpleTimeLimiter(executorService);
		bind(ExecutorService.class).toInstance(executorService);
		bind(ScheduledExecutorService.class).toInstance(executorService);
		bind(TimeLimiter.class).toInstance(timeLimiter);
		bind(Runnable.class).annotatedWith(Idle.class).to(InputStreamCopyRunnable.class);
		bind(InputStream.class).toInstance(inputStream);
		bind(OutputStream.class).toInstance(outputStream);
	}

}
