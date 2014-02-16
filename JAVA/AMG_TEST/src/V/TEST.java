package V;

import java.util.ArrayList;
import java.util.Iterator;


public class TEST {
	static int N = 1024;
	static ArrayList<Grid> grids;
	static AMG amg;
	static boolean toPlot = true;
	static boolean vcycle = true;
	public static void main(String[] args){
		amg  = new AMG();
		TEST t = new TEST();
		t.init(null);
		
		long totalTime =0;
		int numOfCycles = 1;
		
		System.out.println("Before: " + Utils.norm(grids.get(0).v));
		if(toPlot) Utils.plot(grids.get(0).v, "Start");
		for(int i=0;i<numOfCycles;i++) {
			totalTime += t.vcycle();
			Grid temp = new Grid(grids.get(0).A);
			temp.v = grids.get(0).v;
			temp.f = new SparseVector(grids.get(0).v.size());
			grids.clear();
			grids.add(temp);
			//t.relax(g,1000);
		}
		if(toPlot) Utils.plot(grids.get(0).v, "After");
		System.out.println("After: " + Utils.norm(grids.get(0).v));
		System.out.println("Elapsed time for " + numOfCycles + " V-cycles: " + totalTime/1000.0 + " sec. for: " + N + " points");

	}
	
	public void relax(Grid grid ,int swipes){
		for(int swipe=0; swipe<swipes; swipe++){
			for(int i=0; i<grid.A.size(); i++){
				Iterator<Integer> itr = grid.A.getRow(i).Iterator();
				double sum = 0;
				while(itr.hasNext()){
					int j = itr.next();
					if(i != j)
						sum += grid.v.get(j) * grid.A.get(i, j);
				}
				grid.v.put(i, (grid.f.get(i) - sum) / grid.A.get(i, i));
			}
		}
	}
	
	public long vcycle(){
		long time = System.currentTimeMillis();
		for(int i=0; grids.get(i).A.size() > 2; i++) {
			Grid mGrid = grids.get(i);

			relax(mGrid, 2); //relaxation
			if(vcycle) if(Utils.hasZeroRows(mGrid.A)) {System.out.println("Has zero row"); System.exit(1);}
			amg.start(mGrid);
			if(vcycle) System.out.println("A: " + mGrid.A.size());
			
			mGrid.residual = mGrid.f.minus(mGrid.A.times(mGrid.v));//compute residual r = f - Av
			
			Grid mGrid2 = new Grid(mGrid.A2h);

			mGrid2.f = mGrid.restrict(mGrid.residual);//restrict;
			mGrid2.v = new SparseVector(mGrid2.f.size());
			
			grids.add(mGrid2);
		}
		for(int i=grids.size()-2; i>=0; i--) {
			Grid mGrid = grids.get(i);
			mGrid.v = mGrid.v.plus(mGrid.interpolate(grids.get(i+1).v));//correction
			relax(mGrid, 2); //relaxation
		}
		return System.currentTimeMillis() - time;
	}
	
	public void init(double[][] A){
			grids = new ArrayList<>();
	
			double[][] M = new double[N][N];
			double h = Math.pow(N, 2);
			M[0][0] = 2*h;
			M[0][1] = -1*h;
			M[N-1][N-1] = 2*h;
			M[N-1][N-2] = -1*h;
			for(int i=1;i<N-1; i++){
				M[i][i-1] = -1*h;
				M[i][i] = 2*h;
				M[i][i+1] = -1*h;
			}
			
			A = Utils.imageToGraph("output.txt");
			
			if(A == null)
				A = M;
	
			SparseMatrix S = Utils.toSparse(A);
			N = S.size();
			Grid grid = new Grid(S);
	
			grid.v = new SparseVector(N);
			grid.f = new SparseVector(N);
	
			for(int i=1; i<=N; i++){
				grid.v.put(i-1, (Math.sin(i*32*Math.PI/N) + Math.sin(i*6*Math.PI/N) + Math.sin(i*Math.PI/N)) * 1.0/3);
			}
	//		//Boundary conditions
	//		grid.v[0] = 0;
	//		grid.v[N] = 0;
	
	

			grids.add(grid);
		}

	//TESTING AMG MATRIX

	public static double[][] generateGraph(int N){
		if(!hasRoot(N)){
			System.err.println("Number has no root");
			return null;
		}

		int root = (int)Math.sqrt(N);

		double[][] graph = new double[N][N];
		for(int i=0; i<N;i++){
			for(int j=i; j<N; j++)
				if(j != i){
					if(isValid(i, j, root)){
						graph[i][j] = 1;
						graph[j][i] = 1;
					}
				}
		}
		return graph;
	}

	public static boolean isValid(int node, int j , int root){
		boolean up = j == (node-root);
		boolean down = j == (node+root);
		boolean left = j == (node-1) && (node % root != 0);
		boolean right = j == (node+1) && (node % root != root-1);

		//		boolean upLeft =  j == (node-root-1) && (node % root != 0);
		//		boolean upRight =  j == (node-root+1) && (node % root != root-1);
		//		
		//		boolean downLeft =  j == (node+root-1) && (node % root != 0);
		//		boolean downRight =  j == (node+root+1) && (node % root != root-1);

		return up || down || right || left ;//|| upLeft || upRight || downLeft || downRight;
	}

	public static boolean hasRoot(int x){
		double root = Math.sqrt(x);
		int rootInt = (int)Math.sqrt(x);
		return (rootInt != 0) && (rootInt/root == 1);
	}

	public static void print(){
		int root = (int) Math.sqrt(N);
		double[][] D = AMGmatrix();
		//printMatrix(D);
		System.out.println();
		for(int i=0; i<D.length;i++){
			if(i % root == 0)
				System.out.println();
			System.out.print(D[i][i]+ " ");

			//System.out.println("For point " + i + " influence on " + D[i][i] + " points");
		}
	}

	public static double[][] AMGmatrix(){
		double[][] D = generateGraph(N);
		for(int i=0; i<D.length;i++){
			for(int j=0; j<D[0].length; j++){
				if(i!=j)
					D[i][i] += D[i][j];
			}
		}
		return D;
	}
}
