package javaasp;


public class LinearAssignmentSolver {

	public LinearAssignmentSolver() {
	}

	/**
	 * Array - matrix col_mate - outputs order of columns after reordering
	 * row_mate - outputs order of rows
	 */
	public void solve(int size, double[][] cost, int[] col_mate, int[] row_mate) {
		// IMPORTANT! The values of Array[][] don't exist after calling asp()
		// any more
		int i;
		/*
		 * for (i=0;i<size;++i) { int j; for (j=0;j<size;++j)
		 * Result[i][j]=false; }
		 */
		int m = size, n = size;
		int k;
		int l;
		int j;
		double s;
		// int*col_mate;
		// int*row_mate;
		int[] parent_row = null;
		int[] unchosen_row = null;
		int t;
		int q;
		double[] row_dec = null;
		double[] col_inc = null;
		double[] slack = null;
		int[] slack_row = null;
		int unmatched;

		for (i = 0; i < n; ++i)
			col_mate[i] = 0;
		for (i = 0; i < n; ++i)
			row_mate[i] = 0;
		parent_row = new int[n];
		for (i = 0; i < n; ++i)
			parent_row[i] = 0;
		unchosen_row = new int[m];
		for (i = 0; i < m; ++i)
			unchosen_row[i] = 0;
		// col_mate= new int[m];
		for (i = 0; i < m; ++i)
			col_mate[i] = 0;
		// row_mate= new int[n];
		for (i = 0; i < n; ++i)
			row_mate[i] = 0;
		row_dec = new double[m];
		for (i = 0; i < m; ++i)
			row_dec[i] = 0;
		col_inc = new double[n];
		for (i = 0; i < n; ++i)
			col_inc[i] = 0;
		slack = new double[n];
		for (i = 0; i < n; ++i)
			slack[i] = 0;
		slack_row = new int[n];
		for (i = 0; i < n; ++i)
			slack_row[i] = 0;

		// Do heuristic
		for (l = 0; l < n; l++) {
			s = cost[0][l];
			for (k = 1; k < n; k++)
				if (cost[k][l] < s)
					s = cost[k][l];
			if (s != 0)
				for (k = 0; k < n; k++)
					cost[k][l] -= s;
		}

		t = 0;
		for (l = 0; l < n; l++) {
			row_mate[l] = -1;
			parent_row[l] = -1;
			col_inc[l] = 0;
			slack[l] = 10000000;
		}

		for (k = 0; k < m; k++) {
			try {
				s = cost[k][0];
				for (l = 1; l < n; l++)
					if (cost[k][l] < s)
						s = cost[k][l];
				row_dec[k] = s;
				for (l = 0; l < n; l++)
					if ((s == cost[k][l]) && (row_mate[l] < 0)) {
						col_mate[k] = l;
						row_mate[l] = k;
						throw new RowDoneException();
						// break;
						// goto row_done;
					}
				col_mate[k] = -1;
				unchosen_row[t++] = k;

			} catch (RowDoneException r) {
			}
			// row_done:;
		}

		try {
			if (t == 0)
				throw new DoneException(); // goto done;
			unmatched = t;
			while (true) {
				q = 0;
				try {
					while (true) {

						while (q < t) {
							{
								k = unchosen_row[q];
								s = row_dec[k];
								for (l = 0; l < n; l++)
									if (slack[l] != 0) {
										double del;
										del = cost[k][l] - s + col_inc[l];
										if (del < slack[l]) {
											if (del == 0) {
												if (row_mate[l] < 0)
													throw new BreakthruException();
												slack[l] = 0;
												parent_row[l] = k;
												unchosen_row[t++] = row_mate[l];
											} else {
												slack[l] = del;
												slack_row[l] = k;
											}
										}
									}
							}
							q++;
						}

						s = 10000000;
						for (l = 0; l < n; l++)
							if ((slack[l] != 0) && (slack[l] < s))
								s = slack[l];
						for (q = 0; q < t; q++)
							row_dec[unchosen_row[q]] += s;
						for (l = 0; l < n; l++)
							if (slack[l] != 0) {
								slack[l] -= s;
								if (slack[l] == 0) {
									k = slack_row[l];
									if (row_mate[l] < 0) {
										for (j = l + 1; j < n; j++)
											if (slack[j] == 0)
												col_inc[j] += s;
										throw new BreakthruException();
									} else {
										parent_row[l] = k;
										unchosen_row[t++] = row_mate[l];
									}
								}
							} else
								col_inc[l] += s;
					}
				} catch (BreakthruException b) {
				}
				// breakthru:
				while (true) {
					j = col_mate[k];
					col_mate[k] = l;
					row_mate[l] = k;
					if (j < 0)
						break;
					k = parent_row[j];
					l = j;
				}
				if (--unmatched == 0)
					throw new DoneException();
				t = 0;
				for (l = 0; l < n; l++) {
					parent_row[l] = -1;
					slack[l] = 10000000;
				}
				for (k = 0; k < m; k++)
					if (col_mate[k] < 0) {
						unchosen_row[t++] = k;
					}
			}
		} catch (DoneException d) {
			// done: // Just test whether everything went right
		}
		/*
		 * for(k= 0;k<m;k++) for(l= 0;l<n;l++)
		 * if(Array[k][l]<row_dec[k]-col_inc[l]){ ASSERT(NULL); } for(k=
		 * 0;k<m;k++){ l= col_mate[k];
		 * if(l<0||Array[k][l]!=row_dec[k]-col_inc[l]){ ASSERT(NULL); } } k= 0;
		 * for(l= 0;l<n;l++) if(col_inc[l])k++; if(k>m){ ASSERT(NULL); }
		 */
		/*
		 * for (i=0;i<size;++i) { Result[i][col_mate[i]]=true; }
		 */
		for (k = 0; k < m; ++k) {
			for (l = 0; l < n; ++l) {
				/* TRACE("%d ",Array[k][l]-row_dec[k]+col_inc[l]); */
				cost[k][l] = (double) (cost[k][l] - row_dec[k] + col_inc[l]);
			}
			/* TRACE("\n"); */
		}
	}
}
