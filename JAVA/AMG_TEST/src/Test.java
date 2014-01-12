
public class Test {

	public static void main(String[] args)  {
		Test testAmg = new Test();
		
		testAmg.classify();
		//testAmg.computeWeight();
		//testAmg.buildA2h();
		//testAmg.buildInterpolation();
	}

	public void classify() {
		Amg amg = new Amg();
		GridPoint[] nodes = new GridPoint[4];
		nodes[0] = new GridPoint(0, 1);
		nodes[1] = new GridPoint(1, 1);
		nodes[2] = new GridPoint(2, 2);
		nodes[3] = new GridPoint(3, 1);
		
		nodes[0].type = PointType.C_POINT;
		nodes[1].type = PointType.F_POINT;
		nodes[2].type = PointType.F_POINT;
		nodes[3].type = PointType.F_POINT;
		double[] Ni = {-1,-0.5,2,-0.1};
		
		amg.classify(nodes[2], Ni, nodes);

		Assert(nodes[2].Ci.get(0) != null ,"Classify: Failed to add to Ci");
		Assert(nodes[2].Dis.get(1) != null , "Classify: Failed to add to Dis");
		Assert(nodes[2].Diw.get(3) != null , "Classify: Failed to add to Dis");
		System.out.println("<classify> excecute successfully");
	}
	
	
	public void computeWeight(){
		Amg amg = new Amg();
		GridPoint[] nodes = new GridPoint[4];
		nodes[0] = new GridPoint(0, 1);
		nodes[1] = new GridPoint(1, 1);
		nodes[2] = new GridPoint(2, 2);
		nodes[3] = new GridPoint(3, 1);
		
		nodes[0].type = PointType.C_POINT;
		nodes[1].type = PointType.F_POINT;
		nodes[2].type = PointType.F_POINT;
		nodes[3].type = PointType.F_POINT;
		//				  0      1    2    3
		double[][] A = {{  2,  -0.2, -1,    0},  //0
						{-0.2,   2,  -0.5,  0}, //1
						{ -1,  -0.5,  3,  -0.1}, //2
						{  0,    0,   0.1,  1}};//3
		
		amg.classify(A[2], nodes);
		double weight = amg.computeWeight(nodes[2],  A);
		
		Assert(weight == 0.5172413793103449, "ComputeWeight: Failed , Expected: 0.5172413793103449, Actual: " + weight);
		System.out.println("<computeWeight> excecute successfully");
	}
	
	public void buildInterpolation(){
		Amg amg = new Amg();
		GridPoint[] nodes = new GridPoint[4];
//		nodes[0] = new GridPoint(0, 2);
//		nodes[1] = new GridPoint(1, 3);
//		nodes[2] = new GridPoint(2, 3);
//		nodes[3] = new GridPoint(3, 2.4);
		nodes[0] = new GridPoint(0, 2);
		nodes[1] = new GridPoint(1, 2);
		nodes[2] = new GridPoint(2, 2);
		nodes[3] = new GridPoint(3, 2);
		
		nodes[0].type = PointType.F_POINT;
		nodes[1].type = PointType.C_POINT;
		nodes[2].type = PointType.F_POINT;
		nodes[3].type = PointType.C_POINT;
		
		nodes[1].order = 0;
		nodes[3].order = 1;
		
		//				  0       1     2      3 
//		double[][] A = {{  2 ,  -0.2 , -0.1  ,   0  },  //0
//						{ -0.2,    2  ,  -1  ,  -0.2},  //1
//						{ -0.1 ,    -1  ,  2  ,  -0.5},  //2
//						{  0 , -0.2  , -0.5,   2  }}; //3
////		
//		double[][] A = {{ 2 ,  -0.2 , -1.8  ,   0  },      //0
//						{ -0.2,    3  ,  -0.8  ,  -2},     //1
//						{ -1.8 ,    -0.8  ,  3  ,  -0.4},  //2
//						{  0 , -2  , -0.4,      2.4 }};    //3
		double[][] A = {{ 2 ,  -1 , 0  ,   0  },      //0
						{ -1  ,  2  ,  -1  ,  0},     //1
						{ 0,    -1  ,  2  ,  -1},  //2
						{  0 , 0  , -1,      2 }};    //3

		
		amg.classify(A, nodes);
		
		double[][] Inter = amg.buildInterpolation(nodes, A, 2);
		
		printMatrix(Inter, "Interpolation: ");
		
		for(int i=0;i<Inter.length; i++){
			double sum = 0;
			for(int j=0;j<Inter[0].length; j++){
				sum += Inter[i][j];
			}
			Assert(Math.abs((1.0 - sum)) < 0.000000001, String.format("Interpolation failed at:%d with sum-diff: %f", i, Math.abs(1.0 - sum )));
		}
		System.out.println("<buildInterpolation> excecute successfully");		
		
//		for(GridPoint gp: nodes)
//			System.out.println(gp);
	}
	
	public void  buildA2h(){
		Amg amg = new Amg();
		boolean succeeded = true;
		
		GridPoint[] nodes = new GridPoint[4];
		nodes[0] = new GridPoint(0, 2);
		nodes[1] = new GridPoint(1, 3);
		nodes[2] = new GridPoint(2, 3);
		nodes[3] = new GridPoint(3, 2.4);
		
		nodes[0].type = PointType.C_POINT;
		nodes[1].type = PointType.F_POINT;
		nodes[2].type = PointType.F_POINT;
		nodes[3].type = PointType.C_POINT;
		
		nodes[0].order = 0;
		nodes[3].order = 1;
		
						//		  0      1    2    3
		double[][] A = {{ 2 ,  -0.2 , -1.8  ,   0  },      //0
				{ -0.2,    3  ,  -0.8  ,  -2},     //1
				{ -1.8 ,    -0.8  ,  3  ,  -0.4},  //2
				{  0 , -2  , -0.4,      2.4 }};    //3
		
		amg.classify(A, nodes);
		
		double[][] Inter = amg.buildInterpolation(nodes, A, 2);

		double[][] Rest = amg.buildRestriction(Inter);
		
		
		printMatrix(Inter, "Interpolation Matrix: ");
		printMatrix(Rest, "Restriction Matrix: ");
		
		double[][] expectedResult = {{ 0.922 ,-0.922},
									 { 0.922 ,-0.922}};
		
		
	
		double[][] actualResult = amg.buildA2h(Rest, A, Inter);
		
		if(expectedResult.length != actualResult.length ||
		   expectedResult[0].length != actualResult[0].length){
			System.err.println("Failed: buildA2h - dimension: excpeted: 2x2 , actual: " + actualResult.length + "x" + actualResult[0].length);
			return;
		}	
		for(int i=0; i<actualResult.length; i++)
			for(int j=0; j<actualResult.length; j++){
				if(actualResult[i][j] != expectedResult[i][j]){
					if(succeeded)
						System.err.println("<buildA2h> excecute failed");
					//System.out.println("Failed: buildA2h - matrices dont match at: " + String.format("(%s,%s)", i,j));
					succeeded = false;
				}
			}
		if(succeeded)
			System.out.println("<buildA2h> excecute successfully");
		else{
			printMatrix(expectedResult, "Expected Matrix: ");
			printMatrix(actualResult, "Actual Matrix: ");
			System.err.println("</buildA2h> excecute failed");
		}
	}
	
	public static void printMatrix(double[][] M , String title){
		System.err.println();
		System.err.println(title);
		for(int i=0; i<M.length; i++){
			System.err.println();
			for(int j=0; j<M[0].length; j++){
				System.err.print(M[i][j] + " ");
			}
		}
		System.err.println();

		
	}
	
	public void Assert(boolean condition, String failedMessage){
		if(!condition)
			System.err.println(failedMessage);
	}
}
