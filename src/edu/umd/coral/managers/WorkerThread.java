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
package edu.umd.coral.managers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class WorkerThread extends SwingWorker<List<Integer>, Integer> 
	implements PropertyChangeListener {
	
	JProgressBar pb;
	JTextArea ta;
	int numbersToFind;
	
	public WorkerThread(JProgressBar pb, JTextArea ta, int n) {
		this.pb = pb;
		this.ta = ta;
		this.numbersToFind = n;
	}

	
	protected List<Integer> doInBackground() throws Exception {
		int number = 3;
		boolean enough = false;
		List<Integer> numbers = new ArrayList<Integer>();
		
		while (!enough && ! isCancelled()) {
			number = compute(number+1);
			numbers.add(number);
            publish(number);
            setProgress(100 * numbers.size() / numbersToFind);
            enough = numbers.size() >= numbersToFind;
        }
		return numbers;
	}
	
	private int compute(int nextCandidate) {
		if (nextCandidate % 2 == 0)
			nextCandidate++;
		
		boolean foundPrime = false;
		boolean gotDivisor = false;
		int div = 2, sqrt = (int)Math.floor(Math.sqrt(nextCandidate));
		
		while (!foundPrime) {
			while (!gotDivisor && div <= sqrt) {
				if (nextCandidate % div == 0)
					gotDivisor = true;
				else
					div++;
			}
			if (gotDivisor) // test next odd number for being prime
				nextCandidate += 2; // avoid even numbers
			else
				foundPrime = true;
		}
		
		return nextCandidate; 
	}
	
	
    protected void process(List<Integer> chunks) {
        for (int number : chunks) {
            ta.append(number + "\n");
        }
    }
	
	
	protected void done() {
		System.out.println("All done");
	}

	public void propertyChange(PropertyChangeEvent e) {
		if ("progress".equals(e.getPropertyName() ) ) {
			pb.setValue((Integer)e.getNewValue());
		}
	}
}
