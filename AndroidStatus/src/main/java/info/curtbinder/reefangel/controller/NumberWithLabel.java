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

public class NumberWithLabel {

	private Number data;
	private String label;

	private void initValues ( byte decimalPlaces, String label ) {
		data = new Number( decimalPlaces );
		this.label = label;
	}

	public NumberWithLabel () {
		initValues( (byte) 0, "" );
	}

	public NumberWithLabel ( byte decimalPlaces ) {
		initValues( decimalPlaces, "" );
	}

	public NumberWithLabel ( byte decimalPlaces, String label ) {
		initValues( decimalPlaces, label );
	}

	public void setData ( Number data ) {
		this.data = data;
	}

	public void setData ( int value ) {
		data.setValue( value );
	}

	public String getData ( ) {
		return data.toString();
	}

	public void setLabel ( String label ) {
		this.label = label;
	}

	public String getLabel ( ) {
		return label;
	}
}
