
public class Amg {

	
	/**
	 * Classify for each grid point.
	 * @param Ni
	 * @param nodes
	 */
	public void classify(double[][] A, GridPoint[] nodes){
		for(GridPoint gp : nodes)
			classify(gp, A[gp.id], nodes);
	}
	
	/**
	 * Classify for each grid point.
	 * @param Ni
	 * @param nodes
	 */
	public void classify(double[] Ni, GridPoint[] nodes){
		for(GridPoint gp : nodes)
			classify(gp, Ni, nodes);
	}

	/**
	 * Classify for each point the grid its neighbors for
	 *  
	 * 		Ci - Coarse interpolarity set ( only c points ) 
	 * 		Dis - Strong dependent set ( only f points )
	 * 		Diw - Weak dependent set ( c & f points)
	 * 
	 * @param gp
	 * @param Ni
	 * @param nodes
	 */
	public void classify(GridPoint gp, double[] Ni, GridPoint[] nodes){
		//maximum
		double max = Double.MIN_VALUE;
		for(int i=0;i<Ni.length; i++){
			if(i != gp.id)
				if(max < -Ni[i])
					max = -Ni[i]; 
		}
		
		for(int i=0;i<Ni.length; i++){
			if(i != gp.id && Ni[i] != 0)
				if(-Ni[i] >= 0.2*max){
					if(nodes[i].type == PointType.C_POINT)
						gp.Ci.put(i, nodes[i]);
					else{
						gp.Dis.put(i, nodes[i]);
						gp.Dependence.put(i,-Ni[i]);
					}
				}
				else
					gp.Diw.put(i, nodes[i]);
		}
	}

	public double computeWeight(GridPoint gp, double[][] A){
		double sum = 0;
		for(int j : gp.Ci.keySet())
			sum += computeWeight2pt(gp, j, A);

		return sum;
	}

	public double computeWeight2pt(GridPoint gp, int j, double[][] A){
		double aii = A[gp.id][gp.id];
		double Dw = 0;
		for(int n : gp.Diw.keySet())
			Dw += A[gp.id][n];
		double denominator = aii + Dw;
		double Ds = 0;

		for(int m : gp.Dis.keySet()){
			double Dsnomirator = A[gp.id][m]*A[m][j];
			double Dsdenomirator = 0;
			int aaa = -1;
			for(int k : gp.Ci.keySet()){
				Dsdenomirator += A[m][k];
				aaa = k;
			}
			if(Dsdenomirator == 0){
				Dsdenomirator = 1;
				
//				if(aaa != -1)
//				System.out.println(String.format("A[%d][%d] = %f", m,aaa,A[m][aaa]));
//				//System.out.println("ID: " + gp.id + " , TYPE: " + gp.type + " Ci size:" + gp.Ci.keySet().size() + ", Dsdenomirator: " + Dsdenomirator);
//				//Utils.printVector(A[m]);
//				System.out.println(gp);
			}
			Ds += Dsnomirator/Dsdenomirator;
		}

		double numerator = A[gp.id][j] + Ds;
		return (numerator / denominator) * -1;
	}

	public double[][] buildInterpolation(GridPoint[] nodes, double[][] A, int Nc){
		double[][] I = new double[A.length][Nc];
		for(GridPoint gp : nodes){
			if(gp.type == PointType.C_POINT)
				I[gp.id][gp.order] = 1;
			else
				for(int j : gp.Ci.keySet())
					I[gp.id][nodes[j].order] = computeWeight2pt(gp, j, A);
		}
		return I;
	}

	public double[][] buildRestriction(double[][] Inter){ 
		double[][] I = new double[Inter[0].length][Inter.length];
		for(int i=0; i<I.length; i++)
			for(int j=0; j<I[0].length; j++)
				I[i][j] = Inter[j][i];
		return I;
	}

	public double[][] buildA2h(double[][] Rest, double[][] A, double[][] Inter){
		return multiply(multiply(Rest, A), Inter);
	}

	/**
	 * Multiply 2 Matrices O(N^3)
	 * @param A
	 * @param B
	 * @return
	 */
	
	public double[][] multiply(double[][] A, double[][] B){
		double[][] res = new double[A.length][B[0].length];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < B[0].length; j++) {
				for (int k = 0; k < A[0].length; k++) { 
					res[i][j] += A[i][k] * B[k][j];
				}
			}
		}
		return res;
	}

	
	/**
	 * Classify the grid to C_Point or F_Point
	 * @param grid
	 * @return The number of C_Point in the grid
	 */
	public int classifyGrid(Grid grid){

		GridPoint[] nodes = grid.nodes;
		double[][] A = grid.A;
		
		for(int i=0;i<A.length; i++)
			nodes[i] = new GridPoint(i, A[i][i]);

		GridPoint gp = null;
		int numOfC = 0;
		while((gp = getMax(nodes)) != null && gp.type == PointType.UNASSIGN){
			gp.type = PointType.C_POINT;
			grid.print();

			gp.order = numOfC++;
			gp.lamda = -1;

			for(int i=0;i<nodes.length;i++)
				if(gp.id != i && A[gp.id][i] != 0) {
					nodes[i].type = PointType.F_POINT;
					nodes[i].lamda = -1;
				}

			for(int i=0;i<nodes.length;i++)
				if(gp.id != i && A[gp.id][i] != 0)
					for(int n=0;n<nodes.length;n++)
						if(i != n && A[i][n] != 0 && nodes[n].type == PointType.UNASSIGN)
							nodes[n].lamda++;
		}
		return numOfC;
	}
	
	/**
	 * Perform 2 grid schema
	 * @param A
	 * @return
	 */
	public Grid start(double[][] A){
		Grid grid = new Grid(A);
		int numOfC = classifyGrid(grid);
		classify(A, grid.nodes);
		
		grid.Interpolation = buildInterpolation(grid.nodes, grid.A, numOfC);
		grid.Restriction = buildRestriction(grid.Interpolation);
		grid.A2h = buildA2h(grid.Restriction, A, grid.Interpolation);
		
		return grid;
	}
	
	private static GridPoint getMax(GridPoint[] nodes) {
		double max = Double.MIN_VALUE;
		GridPoint p = null;
		for(int i=0; i<nodes.length; i++)
			if(nodes[i].lamda > max){
				max = nodes[i].lamda;
				p = nodes[i];
			}
		return p;
	}

	
	public static void main(String[] args){
		
	}
}
