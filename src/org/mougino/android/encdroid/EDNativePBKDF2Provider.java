package org.mougino.android.encdroid;

import org.mrpdaemon.sec.encfs.EncFSPBKDF2Provider;

import android.util.Log;

public class EDNativePBKDF2Provider extends EncFSPBKDF2Provider {

	static {
		try {
			System.loadLibrary("pbkdf2");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	native byte[] pbkdf2(int passwordLen, String password, int saltLen,
			byte[] salt, int iterations, int keyLen);

	/* calling here forces class initialization */
	public static void checkAvailable() {
	}

	@Override
	public byte[] doPBKDF2(int passwordLen, String password, int saltLen,
			byte[] salt, int iterations, int keyLen) {
		Log.d("EDNativePBKDF2Provider", "Calling into native PBKDF2 function!");
		return pbkdf2(passwordLen, password, saltLen, salt, iterations, keyLen);
	}
}
