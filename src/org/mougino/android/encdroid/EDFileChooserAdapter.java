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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EDFileChooserAdapter extends ArrayAdapter<EDFileChooserItem> {

	private Context context;
	private int resourceId;
	private List<EDFileChooserItem> items;

	public EDFileChooserAdapter(Context context, int resourceId,
			List<EDFileChooserItem> items) {
		super(context, resourceId, items);
		this.context = context;
		this.resourceId = resourceId;
		this.items = items;
	}

	public EDFileChooserItem getItem(int i) {
		return items.get(i);
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(resourceId, null);
		}

		final EDFileChooserItem item = items.get(position);

		if (item != null) {
			TextView fileName = 	(TextView) row.findViewById(R.id.file_chooser_item_name);
			ImageView fileIcon = (ImageView) 	row.findViewById(R.id.file_chooser_item_icon);
			TextView fileSize = 	(TextView) row.findViewById(R.id.file_chooser_item_size);

			if (fileName != null) {
				fileName.setText(item.getName());
			}

			if (fileSize != null) {
				if (!item.isDirectory()) {
					fileSize.setText(humanReadableByteCount(item.getSize(),
							false));
				} else {
					fileSize.setText(null);
				}
			}

			if (fileIcon != null) {
				if (item.isDirectory()) {
					fileIcon.setImageResource(R.drawable.ic_folder);
				} else {
					//fileIcon.setImageResource(R.drawable.ic_file);
					
					String[] types = {"apk","avi","doc","docx","flv","gif","gz","htm","html","jpg","mp3","mpg","pdf","png","txt","xls","zip"};
					boolean found = false;
					for (String type: types){
						int resource = context.getResources().getIdentifier("mime_"+type, "drawable", context.getPackageName());
						if (item.getName().toLowerCase().endsWith(type) && resource!=0) {
							fileIcon.setImageResource(resource);
							found=true;
						}
					}
					if (!found)
						fileIcon.setImageResource(R.drawable.ic_file);
					
				}
			}

		}

		return row;
	}
}
