package V;

import java.util.ArrayList;
import java.util.Iterator;


public class TEST {
	static int N = 49;
	static ArrayList<Grid> grids;
	static AMG amg;
	public static void main(String[] args){
		amg  = new AMG();
//		double[][] A = Utils.getGraphFromFile("Clustering.txt");
//		Utils.graphToMmatrix(A);
//		SparseMatrix  M = toSparse(A);
//		Grid grid = new Grid(M);
//		//		IndexMaxPQ<GridNode> impq = grid.nodesInSet();
//		//		System.out.println(grid.nodes[impq.delMax()]);
//		//		System.out.println(grid.nodes[impq.delMax()]);
//		//		System.out.println(grid.nodes[impq.delMax()]);
//		//		System.out.println(M);
//		amg.classifyGrid(grid);
		TEST t = new TEST();
		t.init(null);
		
		System.out.println("Before: " + Utils.norm(grids.get(0).v));
		Grid g = grids.get(0);
//		Utils.plot(g.v, "Start");
		for(int i=0;i<1;i++) {
			t.vcycle();
			grids.clear();
			grids.add(g);
		}
//		Utils.printVector(g.v);

//		Grid grid = grids.get(0);
//		System.out.println(grids.size());
//		Grid amgS = grid.amgGrid;
//		for(GridPoint gp : amgS.nodes) {
//			if(gp.type == PointType.C_POINT)
//				System.out.println(gp);
//		}
		System.out.println("After: " + Utils.norm(grids.get(0).v));

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
	
	public void vcycle(){

		for(int i=0; grids.get(i).A.size() > 2; i++) {
			Grid mGrid = grids.get(i);
			amg.start(mGrid);

			relax(mGrid, 1); //relaxation
			System.out.println("size a: " + mGrid.A.size() + " size v: " + mGrid.v.size());
			mGrid.residual = mGrid.f.minus(mGrid.A.times(mGrid.v));//compute residual
			
			Grid mGrid2 = new Grid(mGrid.A2h);
			mGrid2.f = mGrid2.restrict(mGrid.residual);//restrict;
			mGrid2.v = new SparseVector(mGrid2.f.size());
			
			grids.add(mGrid2);
		}
		
		for(int i=grids.size()-2; i>=0; i--) {
			Grid mGrid = grids.get(i);
			mGrid.v = mGrid.v.plus(mGrid.interpolate(grids.get(i+1).v));//correction
			relax(mGrid, 1); //relaxation
		}
		
	}
		public void init(double[][] A){
			grids = new ArrayList<>();
	
			double[][] M= { {2,-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{-1,2,-1,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{0,-1,2,-1,0,0,0,0,0,0,0,0,0,0,0,0},
					{0,0,-1,2,-1,0,0,0,0,0,0,0,0,0,0,0},
					{0,0,0,-1,2,-1,0,0,0,0,0,0,0,0,0,0},
					{0,0,0,0,-1,2,-1,0,0,0,0,0,0,0,0,0},
					{0,0,0,0,0,-1,2,-1,0,0,0,0,0,0,0,0},
					{0,0,0,0,0,0,-1,2,-1,0,0,0,0,0,0,0},
					{0,0,0,0,0,0,0,-1,2,-1,0,0,0,0,0,0},
					{0,0,0,0,0,0,0,0,-1,2,-1,0,0,0,0,0},
					{0,0,0,0,0,0,0,0,0,-1,2,-1,0,0,0,0},
					{0,0,0,0,0,0,0,0,0,0,-1,2,-1,0,0,0},
					{0,0,0,0,0,0,0,0,0,0,0,-1,2,-1,0,0},
					{0,0,0,0,0,0,0,0,0,0,0,0,-1,2,-1,0},
					{0,0,0,0,0,0,0,0,0,0,0,0,0,-1,2,-1},
					{0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,2}};
			if(A == null)
				A = M;
	
			SparseMatrix S = toSparse(A);
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

	public static SparseMatrix toSparse(double[][] A){
		SparseMatrix M = new SparseMatrix(A.length);
		for(int i=0; i<A.length; i++)
			for(int j=0; j<A.length; j++)
				if(A[i][j] != 0)
					M.put(i, j, A[i][j]);
		return M;
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
