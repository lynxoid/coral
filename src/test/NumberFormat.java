/*******************************************************************************
 * Copyright (c) 2012 Darya Filippova.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Darya Filippova - initial API and implementation
 ******************************************************************************/
package test;

import java.text.DecimalFormat;

public class NumberFormat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new DecimalFormat("#0.00").format(0.0));
		System.out.println(new DecimalFormat("#0.00").format(0.00));
		System.out.println(new DecimalFormat("#0.00").format(0.00000));
		System.out.println(new DecimalFormat("#0.00").format(1.00003));
		System.out.println(new DecimalFormat("#0.00").format(1.00500));
		System.out.println(new DecimalFormat("#0.00").format(10.00500));
	}

}
