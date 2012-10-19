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
package edu.umd.coral.model.data;


public class BitSymmetricMatrix extends Matrix {
	
	/**
	 * vectors that store 0's and 1's for the matrix
	 */
	long [] bitVectors;
	
	/**
	 * 0 if either a column or a row were in a grab bag, 1 if neither were in a
	 * grab bag
	 */
//	long [] isInGrabBag;
	
	/**
	 * number of rows/columns in the matrix
	 */
	private int N;
	
	/**
	 * length of every bit vector
	 */
	final private int LEN = 63;
	
	/**
	 * Creates a symmetric binary matrix stored in bit vectors with N 
	 * columns/rows and with <code>names</code> for column names.
	 *  
	 * @param N - number of columns/rows in the symmetric matrix
	 * @param names - column names in the symmetric matrix
	 */
	public BitSymmetricMatrix(int N, String [] names) {
		super();
		super.row_count = super.column_count = N;	
		this.N = N;
		super.rowNames = super.columnNames = names;
		// only need to store the top triangle of the matrix (symmetric)
		// N + N-1 + N-2 + ... + 2 + 1 = (N + 1) * N / 2 
		int numVectors = (int) Math.ceil(N * (N+1) * 1.0 / (2 * LEN) );
		if (numVectors == 0)
			numVectors = 1;	// have to have a min of one vector
		bitVectors = new long[numVectors];
//		isInGrabBag = new long[numVectors];
	}
	
	public int getVectorCount() {
		if (bitVectors == null)
			return 0;
		return bitVectors.length;
	}
	
	public double getMax() {
		long res = 0;
		for (int i = 0; i < bitVectors.length; i++)
			res = res | bitVectors[i];
		return (res > 0) ? 1 : 0;
	}
	
	/**
	 * Returns element at row i, column j. If i, j refers to the lower triangle,
	 * returns a symmetric value for (j, i).
	 */
	public double getElement(int i, int j) {
//		System.out.println("getting elem");
		if (i > j) {// swap
			int temp = i;
			i = j;
			j = temp;
		}
		int index = getIndex(i, j);
		int vectorIndex = index / LEN; // divide by size of a long - 64 bits
		int remainder = LEN - (index - vectorIndex * LEN) - 1;
		assert(remainder < LEN);
		long intermediate = (bitVectors[vectorIndex] >>> remainder ) & 0x1;
		return intermediate;
	}
	
	private int getIndex(int i, int j) {
		//return i * N + j;
		/**
		 * Since hte matrix is symmetric, we are only interested in storing 
		 * parts of each row. For row 0, we store all N bits. For row 1, we 
		 * store [1, N-1] bits, for row 2 - [2, N-1] bits, row i - [i, N-1] bits
		 * If we were storing matrix values on an infinite tape of cells, then
		 * it would look like:
		 * [0, N-1] [1, N-1] [2, N-1] ... [i, N-1] [i+1, N-1]
		 * 
		 * Given a row index i, we want to find the row's offset on this tape.
		 * The offsets form an arithmetic progression: 
		 * 0, 
		 * N, 
		 * N + (N-1)
		 * N + (N-1) + (N-2)
		 * ...
		 * N + (N-1) + (N-2) + ... + (N-(k-1)) = (first + last) * count / 2 = 
		 * = (N + (N-k+1) ) * k / 2
		 * 
		 *  Given an arbitrary row i, we can calculate its offset as:
		 */
		return (2*N - i + 1) * i / 2 + (j-i);
	}

	/**
	 * 
	 */
	public void setElement(int i, int j, int value, boolean inGrabBag) {
		assert(value == 0 || value == 1);
		if (i > j) { // lower triangle - swap the values of row/column
			int temp = i;
			i = j;
			j = temp;
		}
		long index = getIndex(i, j);
		long vectorIndex = index / LEN; // divide by size of a long - 64 bits
		long remainder = LEN - (index - vectorIndex * LEN) - 1;
		
		long one = 1;
		long mask = 0;
		
		if (value == 0) {
			mask = ~(one << remainder);
			bitVectors[(int)vectorIndex] = bitVectors[(int)vectorIndex] & mask;
		}
		else {// value is 1
			mask = one << remainder;
			bitVectors[(int)vectorIndex] = bitVectors[(int)vectorIndex] | mask;
		}
		/*
		int isIn = 0;
		if (!inGrabBag) isIn = 1;
		if (isIn == 0) {
			mask = ~(one << remainder);
			isInGrabBag[(int)vectorIndex] = isInGrabBag[(int)vectorIndex] & mask;
		}
		else {// value is 1
			mask = one << remainder;
			isInGrabBag[(int)vectorIndex] = isInGrabBag[(int)vectorIndex] | mask;
		}
		*/
	}
	
	/**
	 * Set element by looking up its row and column indices by names
	 * (SLOW becuase assumes unsorted array for the rowNames)
	 */
	public void setElement(String row, String column, int value){
		int i=0, j=0;
		// BUG FIX: assumed that rowNames are sorted
//		i = Arrays.binarySearch(rowNames,row);
//		j = Arrays.binarySearch(rowNames, column);
		boolean foundRow = false, foundCol = false;
		for (int k = 0; k < rowNames.length && (!foundRow || !foundCol); k++) {
			if (rowNames[k].equals(row))
				i = k;
			if (rowNames[k].equals(column))
				j = k;
		}
		setElement(i,j,value);
	}
	
	public Matrix addMatrix(BitSymmetricMatrix m) {
		Matrix bsm = new Matrix();
		// TODO implement matrix addition
		return bsm;
	}
	
	public Matrix subtractMatrix(BitSymmetricMatrix m) {
		Matrix bsm = new Matrix();
		// TODO: implement matrix subtraction
		return bsm;
	}
	
	/**
	 * Nice string representation of bits
	 * 
	 */
	public String toString() {
		String s = "";
		
		int i;
		for (i = 0; i < bitVectors.length; i++) {
			s += BitSymmetricMatrix.printBits(bitVectors[i]);
		}
		
		// insert newlines
		int lastIndex = 0;
		String out = "";
		for (i = 0; i < N; i++) {
			out += s.substring(lastIndex, lastIndex+N) + "\n";
			lastIndex += N;
		}
		
		return out;
	}
	
	
//	public String toString() {
//		String s = "";
//		
//		int i;
//		for (i = 0; i < isInGrabBag.length; i++) {
//			s += BitSymmetricMatrix.printBits(isInGrabBag[i]);
//		}
//		
//		// insert newlines
//		int lastIndex = 0;
//		String out = "";
//		for (i = 0; i < N; i++) {
//			out += s.substring(lastIndex, lastIndex+N) + "\n";
//			lastIndex += N;
//		}
//		
//		return out;
//	}
	
	/**
	 * 
	 * @param l
	 * @return
	 */
	public static String printBits(long l) {
		String s = "";
		long t;
		for (int i = 0; i < 63; i++) {
			t = (l >>> i) & 1;
			s = String.valueOf(t) + s;
		}
		return s;
	}
	
	/**
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	public static float jaccard(BitSymmetricMatrix A,
			BitSymmetricMatrix B, boolean includeGB) {
		// calculate number of 1-1
		assert(A.N == B.N);
		int i,j;
		long sum;
		int a = 0, bc = 0;
		// calculate number of 1-1
//		if (!includeGB) 
		{
//			System.out.println("Jacc w/o grab bag");
			for (i = 0; i < A.bitVectors.length; i++) {
				sum = A.bitVectors[i] & B.bitVectors[i];
				for (j = 0; j < 63; j++) {
					a += ((sum >>> j) & 1);
				}
			}
			// calculate number of 1-0 and 0-1
			for (i = 0; i < A.bitVectors.length; i++) {
				sum = A.bitVectors[i] ^ B.bitVectors[i];
				for (j = 0; j < 63; j++) {
					bc += ((sum >>> j) & 1);
				}
			}
		}
//		else {
////			System.out.println("Jacc w/ grab bag");
//			for (i = 0; i < A.bitVectors.length; i++) {
//				sum = A.bitVectors[i] & B.bitVectors[i] & A.isInGrabBag[i] & B.isInGrabBag[i];
//				for (j = 0; j < 63; j++) {
//					a += ((sum >>> j) & 1);
//				}
//			}
//			// calculate number of 1-0 and 0-1
//			for (i = 0; i < A.bitVectors.length; i++) {
//				sum = (A.bitVectors[i] & A.isInGrabBag[i]) ^ (B.bitVectors[i] & B.isInGrabBag[i]);
//				for (j = 0; j < 63; j++) {
//					bc += ((sum >>> j) & 1);
//				}
//			}
//		}
		return a * 1.0f / (a + bc);
	}
	
	/***
	 * Calculates a Mirkin metric between two clusterings encoded in their 
	 * respective co-cluster matrices.
	 * 
	 * @param A - co-cluster matrix for clustering A
	 * @param B - co-cluster matrix for clustering B
	 * @return Mirkin metric (2 * pair in different clusters / size^2)
	 */
	public static float mirkin(BitSymmetricMatrix A,
			BitSymmetricMatrix B, boolean includeGB/*TODO*/) {
		assert(A.N == B.N);
		int i,j;
		long sum;
		int bc = 0;
		
		// calculate number of 1-0 and 0-1
		for (i = 0; i < A.bitVectors.length; i++) {
			sum = A.bitVectors[i] ^ B.bitVectors[i];
			for (j = 0; j < 63; j++) {
				bc += ((sum >>> j) & 1);
			}
		}
		return 1.0f * bc / (A.column_count * A.row_count);
	}
	
	/**
	 * Given two clusterings in the form of their co-cluster matrices, 
	 * returns a Rand index between the two.
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	public static float rand(BitSymmetricMatrix A,
			BitSymmetricMatrix B, boolean includeGB/*TODO*/) {
		// calculate number of 1-1
		assert(A.N == B.N);
		int i,j;
		long sum;
		int a = 0, d = 0;
		// calculate number of 1-1
		for (i = 0; i < A.bitVectors.length; i++) {
			sum = A.bitVectors[i] & B.bitVectors[i];
			for (j = 0; j < 63; j++) {
				a += ((sum >>> j) & 1);
			}
		}
		
		// calculate number of 0-0
		for (i = 0; i < A.bitVectors.length; i++) {
			sum = A.bitVectors[i] & B.bitVectors[i];
			sum = ~sum;	// want to invert all 1s to 0s and 0s to 1s, then count all 1s (former 0s)
			for (j = 0; j < 63; j++) {
				d += ((sum >>> j) & 1);
			}
		}
		return (a + d) * 1.0f / (A.column_count * B.column_count);
	}
	
	/**
	 * Given two clusterings in the form of their co-cluster matrices, 
	 * returns a Folkes and Mallows (FM) metric between the two.
	 * 
	 * FM = SQRT( a/(a+b) * a/(a+c) )
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	public static float fm(BitSymmetricMatrix A, BitSymmetricMatrix B, boolean includeGB/*TODO*/) {
		// calculate number of 1-1
		assert(A.N == B.N);
		int i,j;
		long sum;
		int a = 0, b = 0, c = 0;
		// calculate number of 1-1
		for (i = 0; i < A.bitVectors.length; i++) {
			sum = A.bitVectors[i] & B.bitVectors[i];
			for (j = 0; j < 63; j++) {
				a += ((sum >>> j) & 1);
			}
		}
		
		// calculate number of 1-0
		for (i = 0; i < A.bitVectors.length; i++) {
			sum = A.bitVectors[i];
			for (j = 0; j < 63; j++) {
				b += ((sum >>> j) & 1);
			}
		}
		b /= 2;// TODO: why divide?
		
		// calculate number of 0-1
		for (i = 0; i < A.bitVectors.length; i++) {
			sum = A.bitVectors[i];
			for (j = 0; j < 63; j++) {
				c += ((sum >>> j) & 1);
			}
		}
		c /= 2;	
		
		//System.out.println(a + " " + bc);
		return (float)Math.sqrt(a * a * 1.0f / (a + b) / ( a + c) );
	}

	/**
	 * 
	 * @param base
	 * @param toFilter
	 * @return
	 * @throws Exception 
	 */
	public static BitSymmetricMatrix filter(BitSymmetricMatrix base,
			BitSymmetricMatrix toFilter)  {		
		// Matrices should be of the same size
		// columns should be ordered in teh same way
		BitSymmetricMatrix filtered = new BitSymmetricMatrix(toFilter.column_count, toFilter.columnNames);
		
		int i;
		for (i = 0; i < toFilter.bitVectors.length; i++) {
			filtered.bitVectors[i] = base.bitVectors[i] & toFilter.bitVectors[i];
		}
		return filtered;
	}

	/*
	public static Matrix addMatrices(Collection<BitSymmetricMatrix> set) {
		int len = set.size(), i, j;
		Iterator<BitSymmetricMatrix> iter = set.iterator();
		BitSymmetricMatrix M;
		
		M = iter.next();
		int n = M.bitVectors.length;
		long [] mask = new long[n]; // all zeros since we'll be using the complement
		for (i = 0; i < n; i++)
			mask[i] = -1;
		iter = set.iterator();
		long [] oldMask;
		
		for (i = len; i > 0; i++) {
			
			// and them all!!!
			while (iter.hasNext()) {
				M = iter.next();
				for (j = 0; j < M.bitVectors.length; j++)
					mask[j] = mask[j] & M.bitVectors[j];
			}
			// now count # ones
			
			oldMask = mask;
			mask = new long[n];
			for (j = 0; j < n; j++)
				mask[j] = -1;
		}
		
		
		return null;
	}
	*/
//	
//	/**
//	 * Returns a sum of matrices in the set ANDed with filter. The only entries
//	 * in the sum are non-zero that were non-zero after summing up and were non-
//	 * zero in the filter.
//	 * 
//	 * @param set
//	 * @param filter
//	 * @return
//	 */
//	public static Matrix addAndFilterMatrices(Collection<BitSymmetricMatrix> set, BitSymmetricMatrix filter) {
//		int len = set.size(), i, j;
//		Iterator<BitSymmetricMatrix> iter = set.iterator();
//		BitSymmetricMatrix M;
//		
//		M = iter.next();
//		int n = M.bitVectors.length;
//		long [] mask = new long[n]; // all zeros since we'll be using the complement
//		for (i = 0; i < n; i++)
//			mask[i] = -1;
//		iter = set.iterator();
//		long [] oldMask;
//		
//		
//			
//			// and them all!!!
//			while (iter.hasNext()) {
//				M = iter.next();
//				for (j = 0; j < M.bitVectors.length; j++)
//					mask[j] = mask[j] & M.bitVectors[j];
//			}
//			// now count # ones
//			
//			oldMask = mask;
//			mask = new long[n];
//			for (j = 0; j < n; j++)
//				mask[j] = -1;
//		
//		
//		return null;
//	}
}
