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

public interface IScanService
{
	/**
	 * 
	 * @return
	 */
	public boolean getNotificationsEnabled();
	
	/**
	 * 
	 * @param notificationsEnabled
	 */
	public void setNotificationsEnabled(boolean notificationsEnabled);
	
	/**
	 * 
	 * @return
	 */
	public int getGpsSeconds();
	
	/**
	 * 
	 * @return
	 */
	public int getGpsMeters();
	
	/**
	 * 
	 * @param s
	 * @param m
	 */
	public void setGpsTimes(int s, int m);
	
	/**
	 * 
	 */
	public void start_services();
	
	/**
	 * 
	 */
	public void stop_services();
	
	/**
	 * 
	 * @return
	 */
	public int getGpsAccuracy();
	
	/**
	 * 
	 * @return
	 */
	public int getWiFiMinStrength();
	
	/**
	 * 
	 * @param x
	 */
	public void setGpsAccuracy(int x);
	
	/**
	 * 
	 * @param x
	 */
	public void setWiFiMinStrength(int x);
}
