/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Curt Binder
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

//import java.text.DecimalFormatSymbols;

public class Number {
	private int value;
	private int whole;
	private int fraction;
	private byte decimalPlaces;

	public Number () {
		value = 0;
		whole = 0;
		fraction = 0;
		decimalPlaces = 0;
	}

	public Number ( byte decimalPlaces ) {
		this.decimalPlaces = decimalPlaces;
	}

	public Number ( int value, byte decimalPlaces ) {
		this.value = value;
		this.decimalPlaces = decimalPlaces;
		computeNumber();
	}

	private void computeNumber ( ) {
		int divisor = 1;
		switch ( decimalPlaces ) {
			case 2:
				divisor = 100;
				break;
			case 1:
				divisor = 10;
				break;
			default:
				divisor = 1;
				break;
		}
		whole = value / divisor;
		fraction = value % divisor;
	}

	public void setValue ( int value ) {
		this.value = value;
		computeNumber();
	}

	public void setValue ( int value, byte decimalPlaces ) {
		this.value = value;
		this.decimalPlaces = decimalPlaces;
		computeNumber();
	}

	public void setDecimalPlaces ( byte decimalPlaces ) {
		this.decimalPlaces = decimalPlaces;
		computeNumber();
	}

	public String toString ( ) {
		String s = "";
		switch ( decimalPlaces ) {
			case 2:
				// TODO change to be locale independent
				s = String.format( "%d%c%02d", whole, '.', fraction );
				break;
			case 1:
				// TODO change to be locale independent
				s = String.format( "%d%c%01d", whole, '.', fraction );
				break;
			default:
				s = String.format( "%d", whole );
				break;
		}
		return s;
	}
}
