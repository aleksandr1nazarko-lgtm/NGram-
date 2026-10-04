/*
 * Based on the source code of AyuGram for Android (Radolyn Labs, 2023).
 * https://github.com/AyuGram/AyuGram4A  -- licensed under GPL, keep this notice.
 * Trimmed to the ghost-mode subset.
 */

package com.radolyn.ayugram.utils;

import com.radolyn.ayugram.AyuConfig;

/** Only the "allow one read packet through" switch is kept. */
public class AyuState {
    private static final AyuStateVariable allowReadPacket = new AyuStateVariable();

    public static void setAllowReadPacket(boolean val, int resetAfter) {
        allowReadPacket.val = val;
        allowReadPacket.resetAfter = resetAfter;
    }

    public static boolean getAllowReadPacket() {
        return AyuConfig.sendReadPackets || allowReadPacket.process();
    }
}
