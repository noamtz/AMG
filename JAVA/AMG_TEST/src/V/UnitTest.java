package V;

public class UnitTest {

	public static void main(String[] args) {
		UnitTest ut = new UnitTest();
		System.out.println("UnitTest");
		ut.classifyGrid();
	}

	public void classifyGrid() {
		AMG amg = new AMG();
		GridNode[] nodes = new GridNode[4];
		nodes[0] = new GridNode(0, 1);
		nodes[1] = new GridNode(1, 1);
		nodes[2] = new GridNode(2, 2);
		nodes[3] = new GridNode(3, 1);

		double[] Ni = {-1,-0.5,2,-0.1};

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
		
		Grid grid = new Grid(Utils.toSparse(M));
		int numOfC= amg.classifyGrid(grid);

		for(int i=0; i<grid.nodes.length; i++) {
			System.out.print(grid.nodes[i].type + " ");
			if((i+1) % 4 == 0)
				System.out.println();
		}
		System.out.println();
		System.out.println("Num of C's: " + numOfC);
	}

	public void Assert(boolean condition, String failedMessage){
		if(!condition)
			System.err.println(failedMessage);
	}
}
