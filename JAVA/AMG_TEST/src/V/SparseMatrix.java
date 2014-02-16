package V;

import java.util.Iterator;

/*************************************************************************
 *  Compilation:  javac SparseMatrix.java
 *  Execution:    java SparseMatrix
 *  
 *  A sparse, square matrix, implementing using two arrays of sparse
 *  vectors, one representation for the rows and one for the columns.
 *
 *  For matrix-matrix product, we might also want to store the
 *  column representation.
 *
 *************************************************************************/

public class SparseMatrix {
	private final int N,M;           // N-by-N matrix
	private SparseVector[] rows;   // the rows, each row is a sparse vector
	private SparseVector[] cols;   // the cols, each col is a sparse vector
	private boolean willMultiply;

	// initialize an N-by-N matrix of all 0s
	public SparseMatrix(int N, int M, boolean willMultiply) {
		this.N  = N;
		this.M = M;
		this.willMultiply = willMultiply;
		rows = new SparseVector[N];
		cols = new SparseVector[M];
		for (int i = 0; i < N; i++) rows[i] = new SparseVector(M);
		if(willMultiply)
			for (int i = 0; i < M; i++) cols[i] = new SparseVector(N);
	}

	public SparseMatrix(int[] size) {
		this.N  = size[0];
		this.M = size[1];
		rows = new SparseVector[N];
		for (int i = 0; i < N; i++) rows[i] = new SparseVector(M);
	}

	// put A[i][j] = value
	public void put(int i, int j, double value) {
		if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
		if (j < 0 || j >= M) throw new RuntimeException("Illegal index");
		rows[i].put(j, value);
		if(willMultiply)  cols[j].put(i, value);
	}

	// return A[i][j]
	public double get(int i, int j) {
		if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
		if (j < 0 || j >= M) throw new RuntimeException("Illegal index");
		return rows[i].get(j);
	}

	public SparseVector getRow(int i){
		if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
		return rows[i];
	}

	// return the number of nonzero entries (not the most efficient implementation)
	public int nnz() { 
		int sum = 0;
		for (int i = 0; i < N; i++)
			sum += rows[i].nnz();
		return sum;
	}

	// return the matrix-vector product b = Ax
	public SparseVector times(SparseVector x) {
		SparseMatrix A = this;
		if (M != x.size()) throw new RuntimeException("Dimensions disagree");
		SparseVector b = new SparseVector(N);
		for (int i = 0; i < N; i++)
			b.put(i, A.rows[i].dot(x));
		return b;
	}

	public SparseMatrix transpose(boolean willMultiply){
		SparseMatrix result = new SparseMatrix(M,N,willMultiply);
		for(int i=0; i< N; i++){
			SparseVector row = this.getRow(i);
			Iterator<Integer> itr = row.Iterator();
			while(itr.hasNext()){
				int j = itr.next();
				result.put(j, i, this.get(i, j));
			}

		}
		return result;
	}

	public SparseMatrix multiply(SparseMatrix B){
		if(M != B.N) throw new RuntimeException("Dimensions disagree");
		SparseMatrix target = new SparseMatrix(N,B.M,this.willMultiply || B.willMultiply);
		for(int j=0; j<B.cols.length; j++){
			for(int i=0; i<rows.length; i++){
				target.put(i, j, B.cols[j].dot(rows[i]));
			}
		}
		return target;
	}

	public SparseVector getCols(int j){
		return cols[j];
	}

	// return C = A + B
	public SparseMatrix plus(SparseMatrix B) {
		SparseMatrix A = this;
		if (A.N != B.N || A.M != B.M) throw new RuntimeException("Dimensions disagree");
		SparseMatrix C = new SparseMatrix(N,M,willMultiply);
		for (int i = 0; i < N; i++)
			C.rows[i] = A.rows[i].plus(B.rows[i]);
		return C;
	}

	public int[] dimensions(){
		return new int[] {N,M};
	}

	public int size(){
		if(N != M) throw new RuntimeException("Matrix is not symmetry");
		return N;
	}

	// return a string representation
	public String toString() {
		String s = "N = " + N + ", nonzeros = " + nnz() + "\n";
		for (int i = 0; i < N; i++) {
			s += i + ": " + rows[i] + "\n";
		}
		return s;
	}


	// test client
	public static void main(String[] args) {
		//        SparseMatrix A = new SparseMatrix(5);
		//        SparseVector x = new SparseVector(5);
		//        A.put(0, 0, 1.0);
		//        A.put(1, 1, 1.0);
		//        A.put(2, 2, 1.0);
		//        A.put(3, 3, 1.0);
		//        A.put(4, 4, 1.0);
		//        A.put(2, 4, 0.3);
		//        A.computeCols();
		//        System.out.println(A);
		//        System.out.println(A.getCols(4));
		//        x.put(0, 0.75);
		//        x.put(2, 0.11);
		//        System.out.println("x     : " + x);
		//        System.out.println("A     : " + A);
		//        System.out.println("Ax    : " + A.times(x));
		//        System.out.println("A + A : " + A.plus(A));
	}

}