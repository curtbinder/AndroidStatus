/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012 Curt Binder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.curtbinder.reefangel.controller;

import info.curtbinder.reefangel.service.RequestCommands;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTime {
	private int hour;
	private int minute;
	private int month; // month is 1 based on controller, 0 based in java
	private int day;
	private int year; // year is to be set as years since 2000
	private String updateStatus; // status set when updating date/time, either
									// OK or ERR

	public DateTime () {
		this.hour = 0;
		this.minute = 0;
		this.month = 0;
		this.day = 0;
		this.year = 0;
		this.updateStatus = "";
	}

	public DateTime ( int hr, int min, int mon, int day, int yr ) {
		this.hour = hr;
		this.minute = min;
		this.month = mon;
		this.day = day;
		this.year = yr;
		this.updateStatus = "";
	}

	public void setHour ( int hr ) {
		this.hour = hr;
	}

	public int getHour ( ) {
		return hour;
	}

	public void setMinute ( int min ) {
		this.minute = min;
	}

	public int getMinute ( ) {
		return minute;
	}

	public void setMonth ( int month ) {
		this.month = month;
	}

	public int getMonth ( ) {
		return month;
	}

	public void setDay ( int day ) {
		this.day = day;
	}

	public int getDay ( ) {
		return day;
	}

	public void setYear ( int year ) {
		this.year = year;
	}

	public int getYear ( ) {
		return year;
	}

	public void setStatus ( String s ) {
		this.updateStatus = s;
	}

	public String getUpdateStatus ( ) {
		return updateStatus;
	}

	public String getDateString ( ) {
		// TODO confirm proper month formatting
		DateFormat dft =
				DateFormat.getDateInstance( DateFormat.SHORT,
											Locale.getDefault() );
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set( year, month, day );
		return dft.format( c.getTime() );
	}

	public String getTimeString ( ) {
		DateFormat dft =
				DateFormat.getTimeInstance( DateFormat.SHORT,
											Locale.getDefault() );
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set( 0, Calendar.JANUARY, 0, hour, minute );
		return dft.format( c.getTime() );
	}

	public String getDateTimeString ( ) {
		DateFormat dft =
				DateFormat.getDateTimeInstance( DateFormat.DEFAULT,
												DateFormat.DEFAULT,
												Locale.getDefault() );
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set( year, month, day, hour, minute );
		return dft.format( c.getTime() );
	}

	public void setWithCurrentDateTime ( ) {
		Calendar now = Calendar.getInstance();
		hour = now.get( Calendar.HOUR );
		if ( now.get( Calendar.AM_PM ) == 1 ) {
			hour += 12;
		}
		minute = now.get( Calendar.MINUTE );
		month = now.get( Calendar.MONTH );
		day = now.get( Calendar.DAY_OF_MONTH );
		year = now.get( Calendar.YEAR );
	}

	public String getSetCommand ( ) {
		return generateSetDateTimeCommand( hour, minute, month, day, year );
	}

	public static String generateSetDateTimeCommand (
			int hr,
			int min,
			int mon,
			int day,
			int yr ) {
		String cmd = RequestCommands.DateTime;
		cmd +=
				String.format(	"%02d%02d,%02d%02d,%02d", hr, min, mon + 1,
								day,
								yr - 2000 );
		System.out.println( "DateTime: '" + cmd + "'" );
		return cmd;
	}
}
