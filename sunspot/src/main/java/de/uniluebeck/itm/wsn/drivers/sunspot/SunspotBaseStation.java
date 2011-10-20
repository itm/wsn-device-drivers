package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.OperationFutureTask;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallback;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationRunnable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


@Singleton
public class SunspotBaseStation {

    @Inject
    @Named("baseStationConfiguration")
    private Map<String, String> configuration;

    @Inject
    private OperationFactory factory;

    private final Multimap<String, SunspotBaseStationListener> listeners = HashMultimap.create();

    private String receivingBasestationAppPath;

    private Runnable baseStationOutputListener = new Runnable() {
        @Override
        public void run() {
            ant_project p = new ant_project(receivingBasestationAppPath);
            try {
                p.call_host(listeners);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private String SunspotBuildPath;
    private boolean ftime = true;

    private static class OperationQueueEntry {
        public OperationRunnable operationRunnable;
        public OperationFuture future;
        public OperationCallback callback;

        private OperationQueueEntry(OperationRunnable operationRunnable, OperationFuture future, OperationCallback callback) {
            this.operationRunnable = operationRunnable;
            this.future = future;
            this.callback = callback;
        }
    }

    private BlockingQueue<OperationQueueEntry> operationQueue = new LinkedBlockingDeque<OperationQueueEntry>();

    private Runnable operationExecutor = new Runnable() {
        @Override
        public void run() {
            try {

                OperationQueueEntry entry = operationQueue.take();
                entry.callback.onExecute();
                try {
                    entry.operationRunnable.run(null, null);
                    entry.callback.onSuccess(null);
                } catch (Exception ex) {
                    entry.callback.onFailure(new Throwable("Operation Failed"));
                }
            } catch (InterruptedException e) {
                // stop working
            }
        }
    };

    public SunspotBaseStation() {

    }


    public void start() {
        if (this.ftime) {
            this.SunspotBuildPath = this.configuration.get("SunspotBuildPath");
            this.receivingBasestationAppPath = this.configuration.get("receivingBasestationAppPath");
            new Thread(operationExecutor).start();
            new Thread(baseStationOutputListener).start();
            this.ftime = false;
        }
    }


    public OperationFutureTask<Void> resetNode(String macAddress, long timeout, OperationCallback<Void> callback) {
        SunspotResetOperationRunnable operationRunnable = new SunspotResetOperationRunnable(macAddress, this.SunspotBuildPath);
        Operation<Void> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<Void> future = new OperationFutureTask<Void>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }

    public OperationFutureTask<Void> isNodeAlive(String macAddress, long timeout, OperationCallback<Void> callback) {
        SunspotIsAliveOperationRunnable operationRunnable = new SunspotIsAliveOperationRunnable(macAddress, this.SunspotBuildPath);
        Operation<Void> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<Void> future = new OperationFutureTask<Void>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }

    public OperationFutureTask<Void> program(String macAddress, byte[] jar, long timeout, OperationCallback<Void> callback) throws Exception {
        SunspotProgramOperationRunnable operationRunnable = new SunspotProgramOperationRunnable(macAddress, this.SunspotBuildPath, jar);
        Operation<Void> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<Void> future = new OperationFutureTask<Void>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }

    public OperationFuture<ChipType> getChipType(String macAddress, long timeout, OperationCallback<ChipType> callback)   {
        SunspotgetChipTypeOperationRunnable operationRunnable = new SunspotgetChipTypeOperationRunnable();
        Operation<ChipType> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<ChipType> future = new OperationFutureTask<ChipType>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }


    public void addListener(SunspotBaseStationListener sunspotDevice) {
        synchronized (listeners) {
            listeners.put(sunspotDevice.getMacAddress(), sunspotDevice);
        }
    }

    public void removeListener(SunspotDevice sunspotDevice) {
        synchronized (listeners) {
            listeners.remove(sunspotDevice.getMacAddress(), sunspotDevice);
        }
    }
}
