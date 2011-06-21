package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;

public class JennicGetFlashHeaderOperation extends AbstractOperation<byte[]> implements GetFlashHeaderOperation {

	private final Provider<GetChipTypeOperation> getChipTypeProvider;
	
	private final Provider<ReadFlashOperation> readFlashProvider;
	
	@Inject
	public JennicGetFlashHeaderOperation(Provider<GetChipTypeOperation> getChipTypeProvider,
			Provider<ReadFlashOperation> readFlashProvider) {
		this.getChipTypeProvider = getChipTypeProvider;
		this.readFlashProvider = readFlashProvider;
	}
	
	@Override
	public byte[] execute(final ProgressManager progressManager) throws Exception {
		final ChipType chipType = executeSubOperation(getChipTypeProvider.get(), progressManager.createSub(0.5f));
		
		final int address = chipType.getHeaderStart();
		final int length = chipType.getHeaderLength();
		
		final ReadFlashOperation readFlashOperation = readFlashProvider.get();
		readFlashOperation.setAddress(address, length);
		return executeSubOperation(readFlashOperation, progressManager.createSub(0.5f));
	}

}
