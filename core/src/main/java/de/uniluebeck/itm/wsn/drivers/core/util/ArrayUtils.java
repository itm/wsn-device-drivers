package de.uniluebeck.itm.wsn.drivers.core.util;

import java.util.Arrays;

/**
 * @author TLMAT UC
 */
public class ArrayUtils {

    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static byte[] clone(byte[] array) {
        return Arrays.copyOf(array, array.length);
    }
}
