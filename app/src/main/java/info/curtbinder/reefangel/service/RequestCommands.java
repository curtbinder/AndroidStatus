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

package info.curtbinder.reefangel.service;

public final class RequestCommands {
	// Request commands sent to the controller
	public static final String MemoryByte = "/mb";
	public static final String MemoryInt = "/mi";
	public static final String Status = "/r99";
	// public static final String Status = "/sr";
	public static final String DateTime = "/d";
	public static final String Version = "/v";
	public static final String FeedingMode = "/mf";
	public static final String WaterMode = "/mw";
	public static final String AtoClear = "/mt";
	public static final String OverheatClear = "/mo";
	public static final String ExitMode = "/bp";
	public static final String Relay = "/r";
	public static final String LightsOn = "/l1";
	public static final String LightsOff = "/l0";
    public static final String Reboot = "/boot";
    public static final String Calibrate = "/cal";
    public static final String PwmOverride = "/po";
	public static final String None = "";
	public static final String ReefAngel = "ra";
}
