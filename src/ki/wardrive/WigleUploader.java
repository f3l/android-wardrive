/*
 *   wardrive - android wardriving application
 *   Copyright (C) 2009 Raffaele Ragni
 *   http://code.google.com/p/wardrive-android/
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ki.wardrive;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import android.util.Base64;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WigleUploader
{
	private static Object LOCK = new Object();
	
	private static final String BOUNDARY = "----MultiPartBoundary";
	
	private static final String NL = "\r\n";

	public static boolean upload(String username, String password, String dburl, File file, Handler message_handler)
	{
		if (username == null || 
			username.length() == 0 || 
			password == null || 
			password.length() == 0 || 
			dburl == null || 
			dburl.length() == 0)
		{
			return false;
		}
		
		synchronized (LOCK)
		{
			try
			{
				Message msg;
				Bundle b;
				int ct;
				long readbytes = 0;
				long filelength = file.length();
				byte[] buf = new byte[1240];

				URL url = new URL(dburl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("User-Agent","wardrive");
				String usernamePassword = username + ":" + password;
				String encodedUsernamePassword = Base64.encodeToString(usernamePassword.getBytes(), Base64.DEFAULT | Base64.NO_WRAP);
				conn.setRequestProperty("Authorization", "Basic " + encodedUsernamePassword);
				conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+BOUNDARY);
				conn.connect();

				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes("--"+BOUNDARY+NL+"Content-Disposition: form-data; name=\"file\";filename=\"wardrive.kml\""+NL+"Content-Type: application/octet-stream"+NL+NL);
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file), 1024);

				while(fis.available() > 0)
				{
					ct = fis.read(buf);
					dos.write(buf, 0, ct);
					dos.flush();
					
					readbytes += ct;
					msg = Message.obtain(message_handler, Constants.EVENT_WIGLE_UPLOAD_PROGRESS);
					b = new Bundle();
					// Use only 3/4 of the status to make the bar stop at 75% before server response
					b.putInt(Constants.EVENT_WIGLE_UPLOAD_PROGRESS_PAR_COUNT, (int) readbytes/4*3);
					b.putInt(Constants.EVENT_WIGLE_UPLOAD_PROGRESS_PAR_TOTAL, (int) filelength);
					msg.setData(b);
					message_handler.sendMessage(msg);
				}

				fis.close();
				//dos.writeBytes(NL+"--"+BOUNDARY+NL+"Content-Disposition: form-data; name=\"Send\""+NL+NL+"Send");  //not always necessary
				dos.writeBytes(NL+"--"+BOUNDARY+"--"+NL);
				dos.flush();
				dos.close();
				DataInputStream dis = new DataInputStream(conn.getInputStream());
				byte[] data = new byte[10240];
				ct = dis.read(data);
				dis.close();
				conn.disconnect();

				// Server response received: set progress-bar to 100%
				msg = Message.obtain(message_handler, Constants.EVENT_WIGLE_UPLOAD_PROGRESS);
				b = new Bundle();
				b.putInt(Constants.EVENT_WIGLE_UPLOAD_PROGRESS_PAR_COUNT, (int) readbytes);
				b.putInt(Constants.EVENT_WIGLE_UPLOAD_PROGRESS_PAR_TOTAL, (int) filelength);
				msg.setData(b);
				message_handler.sendMessage(msg);

				String response = new String(data, 0, ct);

				return response.indexOf("UPLOAD DONE") != -1;  // catch return value from server
			}
			catch (Exception e)
			{
				Logger.getAnonymousLogger().severe(e.getMessage());
				return false;
			}
		}
	}
}
