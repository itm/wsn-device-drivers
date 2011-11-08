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
import de.uniluebeck.itm.wsn.drivers.core.util.JarUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private String basestationPort;

    private String tempDirectory;

    private Runnable baseStationOutputListener;

    private String SunspotBuildPath;
    private boolean ftime = true;
    private Thread opExecutor;
    private Thread msgBasestation;

    private String sysBinPath;
    private String libFilePath;
    private String keyStrorePath;
    private String port;
    private String iport;
    private String workingDirectory;
    private int ControlRadiogramPort;
    private int ReceivingRadiogramPort;

    private static final Logger log = LoggerFactory.getLogger(SunspotBaseStation.class);

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
    private BlockingQueue<SunspotMessage> sendMessageQueue = new LinkedBlockingDeque<SunspotMessage>();


    private Runnable operationExecutor = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    OperationQueueEntry entry = operationQueue.take();
                    entry.callback.onExecute();
                    try {
                        entry.operationRunnable.run(null, null);
                        entry.callback.onSuccess(null);
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                        entry.callback.onFailure(new Throwable("Operation Failed"));
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }
    };

    public SunspotBaseStation() {

    }


    public void start() {
        if (this.ftime) {
            this.SunspotBuildPath = this.configuration.get("SunspotBuildPath");
            this.receivingBasestationAppPath = this.configuration.get("receivingBasestationAppPath");
            this.basestationPort = this.configuration.get("BasestationPort");
            this.tempDirectory = this.configuration.get("tempDirectory");
            this.sysBinPath = this.configuration.get("sysBinPath");
            this.libFilePath = this.configuration.get("libFilePath");
            this.keyStrorePath = this.configuration.get("keyStrorePath");
            this.workingDirectory = this.configuration.get("workingDirectory");
            this.port = "-p" + this.basestationPort;
            this.iport = "-i" + this.basestationPort;
            this.ControlRadiogramPort = Integer.parseInt(this.configuration.get("ControlRadiogramPort"));
            this.ReceivingRadiogramPort = Integer.parseInt(this.configuration.get("ReceivingRadiogramPort"));

            JarUtil.loadLibrary("rxtxSerial");
            System.setProperty("SERIAL_PORT", this.basestationPort);
            System.setProperty("executable.path", "/home/evangelos/programs/SunSPOT/sdk-red-090706/arm/vm-spot.bin");
            System.setProperty("squawk.startup.arguments", "-Xboot:268763136 -Xmxnvm:0 -isolateinit:com.sun.spot.peripheral.Spot -dma:1024");

            opExecutor = new Thread(operationExecutor);
            opExecutor.start();

            msgBasestation = new Thread(new SunspotMessageBasestation(this.ReceivingRadiogramPort, this.ControlRadiogramPort, this.listeners, sendMessageQueue));
            msgBasestation.start();
            this.ftime = false;
        }
    }

    public void stop() {
        opExecutor.stop();
        msgBasestation.stop();

    }


    public OperationFutureTask<Void> resetNode(String macAddress, long timeout, OperationCallback<Void> callback) {
        SunspotResetOperationRunnable operationRunnable = new SunspotResetOperationRunnable(macAddress, this.sysBinPath, this.libFilePath, this.keyStrorePath, this.port, this.iport);
        Operation<Void> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<Void> future = new OperationFutureTask<Void>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }

    public OperationFutureTask<Void> isNodeAlive(String macAddress, long timeout, OperationCallback<Void> callback) {
        SunspotIsAliveOperationRunnable operationRunnable = new SunspotIsAliveOperationRunnable(macAddress, this.sysBinPath, this.libFilePath, this.keyStrorePath, this.port, this.iport);
        Operation<Void> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<Void> future = new OperationFutureTask<Void>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }

    public OperationFutureTask<Void> program(String macAddress, byte[] jar, long timeout, OperationCallback<Void> callback) throws Exception {
        SunspotProgramOperationRunnable operationRunnable = new SunspotProgramOperationRunnable(macAddress, this.sysBinPath, this.libFilePath, this.keyStrorePath, this.port, this.iport, jar, this.workingDirectory);
        Operation<Void> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<Void> future = new OperationFutureTask<Void>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }

    public OperationFuture<ChipType> getChipType(String macAddress, long timeout, OperationCallback<ChipType> callback) {
        SunspotgetChipTypeOperationRunnable operationRunnable = new SunspotgetChipTypeOperationRunnable();
        Operation<ChipType> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<ChipType> future = new OperationFutureTask<ChipType>(operationContainer);
        this.operationQueue.add(new OperationQueueEntry(operationRunnable, future, callback));
        return future;
    }

    public OperationFutureTask<Void> send(String macAddress, byte[] message, long timeout, OperationCallback<Void> callback)   {
        SunspotSendOperationRunnable operationRunnable = new SunspotSendOperationRunnable(macAddress, message,this.ControlRadiogramPort);
        Operation<Void> operationContainer = factory.create(operationRunnable, timeout, callback);
        OperationFutureTask<Void> future = new OperationFutureTask<Void>(operationContainer);
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
