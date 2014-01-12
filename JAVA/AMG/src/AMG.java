import java.util.ArrayList;


public class AMG {

	ArrayList<Integer> C = new ArrayList<>();
	ArrayList<Integer> F = new ArrayList<>();
	
	public static void main(String args[]){
		int N = 25;
		int root = (int) Math.sqrt(N);
		int[][] D = generateGraph(N);
		//printMatrix(D);
		AMGmatrix(D);
		System.out.println();
		for(int i=0; i<D.length;i++){
			if(i % root == 0)
				System.out.println();
			System.out.print(D[i][i]+ " ");

			//System.out.println("For point " + i + " influence on " + D[i][i] + " points");
		}
		
		//printMatrix(D);
		
		
	}

	public static void createCoarseGrid(int[][] D){
		int[][] temp = copyMatrix(D);
		int[] pt = selectCPt(D);
	}
	
	public static int[][] copyMatrix(int[][] source){
		int[][] target = new int[source.length][source[0].length];
		for(int i=0; i<source.length;i++)
			for(int j=0; j<source[0].length; j++)
				target[i][j] = source[i][j];
		return target;
			
	}
	
	public static int[] selectCPt(int[][] A){
		int max = Integer.MIN_VALUE;
		int ptx = 0, pty = 0;
		for(int i=0; i<A.length;i++){
			for(int j=0; j<A[0].length; j++){
				if(A[i][j] > max){
					max = A[i][j]; 
					ptx = i;
					pty = j;
				}
			}
		}
		return new int[]{ptx, pty};
	}
	
	public static void AMGmatrix(int[][] D){
		for(int i=0; i<D.length;i++){
			for(int j=0; j<D[0].length; j++){
				if(i!=j)
					D[i][i] += D[i][j];
			}
			for(int j=0; j<D[0].length; j++){
//				if(i!=j)
//					D[i][j] *= -1;
			}
		}
	}
	
	public static int[][] generateGraph(int N){
		if(!hasRoot(N)){
			System.err.println("Number has no root");
			return null;
		}

		int root = (int)Math.sqrt(N);

		int[][] graph = new int[N][N];
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
	
	public static void printMatrix(int[][] M){
		System.out.println();
		System.out.print("[");
		for(int i=0; i<M.length;i++){
			for(int j=0;j<M[0].length;j++){
				System.out.print(M[i][j] + " ");
			}
			System.out.println( (i<M.length-1) ?";" : "]");
			System.out.print(" ");
		}
		System.out.println();
	}
	
	public static boolean hasRoot(int x){
		double root = Math.sqrt(x);
		int rootInt = (int)Math.sqrt(x);
		return (rootInt != 0) && (rootInt/root == 1);
	}
}
