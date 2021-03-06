import java.util.ArrayList;

public class Main {

	static int N;
	static int numOfCycles = 3;
	static double PI = Math.PI;
	static Amg amg;
	static ArrayList<MultiGrid> grids;
	public static void main(String[] args){
		Main m = new Main();
		amg = new Amg();
		double[][] A = m.generateMatrix();

//		double[][] A = Utils.imageToGraph("output.txt"); //Utils.getGraphFromFile("Clustering.txt");//
//		for(int i=0;i<A.length;i++){
//
//			for(int j=0;j<A[0].length;j++)
//				if(i!=j && A[i][j] != 0)
//					if(A[j][i] == 0){
//						A[j][i] = 1;//0.5;
//						A[i][j] = 1;//0.5;
//					}
//
//		}
		N = A.length;
		//Utils.graphToMmatrix(A);
		m.init(A);

		System.out.println("Before: " + Utils.norm(grids.get(0).v));
		MultiGrid g = grids.get(0);
		//		Utils.plot(g.v, "Start");
		for(int i=0;i<numOfCycles;i++) {
			m.vcycle();
						grids.clear();
						grids.add(g);
		}
		//System.out.println(grids.size());
		//grids.get(1).amgGrid.Interpolation));

		//	Utils.printVector(g.v);

		//		MultiGrid grid = grids.get(0);
		//		System.out.println(grids.size());
		//		Grid amgS = grid.amgGrid;
		//		for(GridPoint gp : amgS.nodes) {
		//			if(gp.type == PointType.C_POINT){
		//				for(Integer i : gp.Dis.keySet()){
		//					for(GridPoint gp2 : amgS.nodes) {
		//						if(gp2.type == PointType.C_POINT && gp2.id != gp.id){
		//							for(Integer j : gp2.Dis.keySet()){
		//								if(i == j){ 
		//									if(gp.Dependence.get(i) < gp2.Dependence.get(j)){
		//										gp.Dependence.put(i,0.0);
		//									}
		//									else if(gp.Dependence.get(i) > gp2.Dependence.get(j)){
		//										gp2.Dependence.put(j,0.0);
		//									}
		//								}
		//							}
		//						}
		//					}
		//				}
		//			}
		//		}
		//		for(GridPoint gp : amgS.nodes) {
		//			if(gp.type == PointType.C_POINT){
		//				System.out.println(gp);
		//			}
		//		}
				System.out.println("After: " + Utils.norm(grids.get(0).v));
	}

	public void cluster(){
		MultiGrid mg = grids.get(0);
		Grid amg = mg.amgGrid;
		for(GridPoint gp : amg.nodes) {
			if(gp.type == PointType.C_POINT){
				for(Integer i : gp.Dis.keySet()){
					for(GridPoint gp2 : amg.nodes) {
						if(gp2.type == PointType.C_POINT && gp2.id != gp.id){
							for(Integer j : gp2.Dis.keySet()){
								if(i == j){ 
									if(gp.Dependence.get(i) < gp2.Dependence.get(j)){
										gp.Dependence.put(i,0.0);
									}
									else if(gp.Dependence.get(i) > gp2.Dependence.get(j)){
										gp2.Dependence.put(j,0.0);
									}
								}
							}
						}
					}
				}
			}
		}
		double[][] I = Utils.FromImage("output.txt");
		for(GridPoint gp : amg.nodes) {
			if(gp.type == PointType.C_POINT){

				for(Integer i : gp.Dis.keySet()){
					if(gp.Dependence.get(i) != 0){}	

				}
			}
		}


	}

	public void relax(MultiGrid grid ,int swipes){
		for(int swipe=0; swipe<swipes; swipe++)
			for(int i=0; i<grid.v.length; i++){
				double sum = 0;
				for(int j=0; j<grid.v.length; j++)
					if(i != j)
						sum += grid.v[j] * grid.A[i][j];
				grid.v[i] = (grid.f[i] - sum) / grid.A[i][i];
			}
	}

	public void vcycle(){
		for(int i=0; grids.get(i).A.length > 10; i++) {
			MultiGrid mGrid = grids.get(i);
			mGrid.amgGrid = amg.start(mGrid.A);
			if(Utils.hasZeroRows(mGrid.A)){
				System.err.println("Grid:"+mGrid.A.length+" Has Zero Rows");
				System.exit(1);
			}
			relax(mGrid, 1); //relaxation
			mGrid.residual = Utils.subtract(mGrid.f, Utils.applyOperator(mGrid.A, mGrid.v));//compute residual

			MultiGrid mGrid2 = new MultiGrid();
			mGrid2.A =  mGrid.amgGrid.A2h;
			mGrid2.f = mGrid.amgGrid.restrict(mGrid.residual);//restrict;
			mGrid2.v = new double[mGrid2.f.length];

			grids.add(mGrid2);
		}

		for(int i=grids.size()-2; i>=0; i--) {
			MultiGrid mGrid = grids.get(i);
			Utils.add(mGrid.v, mGrid.amgGrid.interpolate(grids.get(i+1).v));//correction
			relax(mGrid, 1); //relaxation
		}

	}

	public double[][] generateMatrix() {
		N=512;
		double[][] A = new double[N][N];

		double h2= Math.pow(N , 2) ;

		A[0][0] = h2*2;
		A[0][1] = h2*-1;
		for(int i=1; i<N-1; i++){
			A[i][i-1] = h2*-1;
			A[i][i] = h2*2;
			A[i][i+1] = h2*-1;
		}
		A[N-1][N-2] = h2*-1;
		A[N-1][N-1] = h2*2;
		return A;
	}

	public void init(double[][] A){
		grids = new ArrayList<>();

		MultiGrid grid = new MultiGrid();

		grid.v = new double[N];
		grid.f = new double[N];

		for(int i=1; i<=N; i++){
			grid.v[i-1] = (Math.sin(i*32*PI/N) + Math.sin(i*6*PI/N) + Math.sin(i*PI/N)) * 1.0/3;
			grid.f[i-1] = 0;
		}
		//		//Boundary conditions
		//		grid.v[0] = 0;
		//		grid.v[N] = 0;


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

		grid.A = A;
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
