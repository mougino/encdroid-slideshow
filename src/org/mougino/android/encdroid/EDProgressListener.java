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

import org.mrpdaemon.sec.encfs.EncFSProgressListener;

import android.app.Activity;

public class EDProgressListener extends EncFSProgressListener {

	// Task that owns this progress listener
	private EDAsyncTask<Void, Void, Boolean> myTask;

	public EDProgressListener(EDAsyncTask<Void, Void, Boolean> task) {
		myTask = task;
	}

	@Override
	public void handleEvent(int eventType) {

		// Get the current activity that owns the task
		Activity activity = myTask.getActivity();
		if (activity == null) {
			return;
		}

		switch (eventType) {
		case EDProgressListener.FILES_COUNTED_EVENT:
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					myTask.setMaxProgress(getNumFiles());
				}
			});
			break;
		case EDProgressListener.NEW_FILE_EVENT:
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					myTask.setProgressMessage(getCurrentFile());
				}
			});
			break;
		case EDProgressListener.FILE_PROCESS_EVENT:
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					myTask.incrementProgressBy(1);
				}
			});
			break;
		case EDProgressListener.OP_COMPLETE_EVENT:
			break;
		default:
			break;
		}
	}
}
