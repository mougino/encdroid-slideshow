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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Base64;
import android.util.Log;

public class EDDBHelper extends SQLiteOpenHelper {

	// Logger tag
	private final String TAG = "EDDBHelper";

	// Database name
	public static final String DB_NAME = "volume.db";

	// Database version
	public static final int DB_VERSION = 3;

	// Volume table name
	public static final String DB_TABLE = "volumes";

	// Column names
	public static final String DB_COL_ID = BaseColumns._ID;
	public static final String DB_COL_NAME = "name";
	public static final String DB_COL_PATH = "path";
	public static final String DB_COL_TYPE = "type";
	public static final String DB_COL_KEY = "key";

	private static final String[] NO_ARGS = {};

	public EDDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCmd = "CREATE TABLE " + DB_TABLE + " (" + DB_COL_ID
				+ " int primary key, " + DB_COL_NAME + " text, " + DB_COL_PATH
				+ " text, " + DB_COL_KEY + " text, " + DB_COL_TYPE + " int)";
		Log.d(TAG, "onCreate() executing SQL: " + sqlCmd);
		db.execSQL(sqlCmd);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		Log.d(TAG, "onUpgrade() recreating DB");
		onCreate(db);
	}

	public void insertVolume(EDVolume volume) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.clear();
		values.put(DB_COL_NAME, volume.getName());
		values.put(DB_COL_PATH, volume.getPath());
		values.put(DB_COL_TYPE, volume.getType());

		Log.d(TAG, "insertVolume() name: '" + volume.getName() + "' path: '"
				+ volume.getPath() + "'");

		// TODO: Make sure path is unique
		db.insertOrThrow(DB_TABLE, null, values);
	}

	public void deleteVolume(EDVolume volume) {
		SQLiteDatabase db = getWritableDatabase();

		Log.d(TAG, "deleteVolume() " + volume.getName());

		db.delete(DB_TABLE, DB_COL_NAME + "=? AND " + DB_COL_PATH + "=?",
				new String[] { volume.getName(), volume.getPath() });
	}

	public void renameVolume(EDVolume volume, String newName) {
		SQLiteDatabase db = getWritableDatabase();

		Log.d(TAG, "renameVolume() " + volume.getName() + " to " + newName);

		ContentValues values = new ContentValues();
		values.put(DB_COL_NAME, newName);
		db.update(DB_TABLE, values, DB_COL_NAME + "=? AND " + DB_COL_PATH
				+ "=?", new String[] { volume.getName(), volume.getPath() });
	}

	public void cacheKey(EDVolume volume, byte[] key) {
		SQLiteDatabase db = getWritableDatabase();

		Log.d(TAG, "cacheKey() for volume" + volume.getName());

		ContentValues values = new ContentValues();
		values.put(DB_COL_KEY, Base64.encodeToString(key, Base64.DEFAULT));
		db.update(DB_TABLE, values, DB_COL_NAME + "=? AND " + DB_COL_PATH
				+ "=?", new String[] { volume.getName(), volume.getPath() });
	}

	public void clearKey(EDVolume volume) {
		SQLiteDatabase db = getWritableDatabase();

		Log.d(TAG, "clearKey() for volume" + volume.getName());

		ContentValues values = new ContentValues();
		values.putNull(DB_COL_KEY);
		db.update(DB_TABLE, values, DB_COL_NAME + "=? AND " + DB_COL_PATH
				+ "=?", new String[] { volume.getName(), volume.getPath() });
	}

	public void clearAllKeys() {
		SQLiteDatabase db = getWritableDatabase();

		Log.d(TAG, "clearAllKeys()");

		db.execSQL("UPDATE " + DB_TABLE + " SET " + DB_COL_KEY + " = NULL");
	}

	public byte[] getCachedKey(EDVolume volume) {
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(DB_TABLE, NO_ARGS, DB_COL_NAME + "=? AND "
				+ DB_COL_PATH + "=?",
				new String[] { volume.getName(), volume.getPath() }, null,
				null, null);

		if (cursor.moveToFirst()) {
			String keyStr = cursor.getString(cursor.getColumnIndex(DB_COL_KEY));
			if (keyStr != null) {
				return Base64.decode(keyStr, Base64.DEFAULT);
			}
		}

		return null;
	}

	public List<EDVolume> getVolumes() {
		ArrayList<EDVolume> volumes = new ArrayList<EDVolume>();
		SQLiteDatabase db = getReadableDatabase();

		// SELECT *, loop over each, create EDVolume
		Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE, NO_ARGS);

		int nameColId = cursor.getColumnIndex(DB_COL_NAME);
		int pathColId = cursor.getColumnIndex(DB_COL_PATH);
		int typeColId = cursor.getColumnIndex(DB_COL_TYPE);

		if (cursor.moveToFirst()) {
			do {
				String volName = cursor.getString(nameColId);
				String volPath = cursor.getString(pathColId);
				int volType = cursor.getInt(typeColId);

				Log.d(TAG, "getVolume() name: '" + volName + "' path: '"
						+ volPath + "'");

				EDVolume volume = new EDVolume(volName, volPath, volType);

				volumes.add(volume);
			} while (cursor.moveToNext());
		}

		return volumes;
	}
}