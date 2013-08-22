/*
 * encdroid - EncFS client application for Android
 * Copyright (C) 2012  Mark R. Pariente
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mougino.android.encdroid;

import java.util.List;

import android.app.Application;
import android.util.Log;

public class EDApplication extends Application {

	// Logger tag
	private final static String TAG = "EDApplication";

	// Volume list
	private List<EDVolume> volumeList;

	// DB helper
	private EDDBHelper dbHelper;

	// Dropbox context
	private EDDropbox mDropbox;

	// Whether action bar is available
	private static boolean mActionBarAvailable;

	// PBKDF2 provider
	private EDNativePBKDF2Provider mNativePBKDF2Provider;

	// Whether native PBKDF2 provider is available
	private static boolean mNativePBKDF2ProviderAvailable;

	static {
		try {
			EDActionBar.checkAvailable();
			mActionBarAvailable = true;
			Log.d(TAG, "Action bar class is available");
		} catch (Throwable t) {
			mActionBarAvailable = false;
			Log.d(TAG, "Action bar class is NOT unavailable");
		}

		try {
			EDNativePBKDF2Provider.checkAvailable();
			mNativePBKDF2ProviderAvailable = true;
			Log.d(TAG, "Native PBKDF2 provider is available");
		} catch (Throwable t) {
			mNativePBKDF2ProviderAvailable = false;
			Log.d(TAG, "Native PBKDF2 provider is NOT available");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		this.dbHelper = new EDDBHelper(this);
		this.volumeList = dbHelper.getVolumes();
		this.mDropbox = new EDDropbox(this);

		if (mNativePBKDF2ProviderAvailable) {
			mNativePBKDF2Provider = new EDNativePBKDF2Provider();
		} else {
			mNativePBKDF2Provider = null;
		}

		Log.d(TAG, "EDApplication initialized");
	}

	/**
	 * @return whether action bar class is available
	 */
	public boolean isActionBarAvailable() {
		return mActionBarAvailable;
	}

	/**
	 * @return the volumeList
	 */
	public List<EDVolume> getVolumeList() {
		return volumeList;
	}

	/**
	 * @return the dbHelper
	 */
	public EDDBHelper getDbHelper() {
		return dbHelper;
	}

	/**
	 * @return the dropbox context
	 */
	public EDDropbox getDropbox() {
		return mDropbox;
	}

	/**
	 * @return whether native PBKDF2 provider is available
	 */
	public boolean isNativePBKDF2ProviderAvailable() {
		return mNativePBKDF2ProviderAvailable;
	}

	/**
	 * @return the native PBKDF2 provider
	 */
	public EDNativePBKDF2Provider getNativePBKDF2Provider() {
		return mNativePBKDF2Provider;
	}
}
