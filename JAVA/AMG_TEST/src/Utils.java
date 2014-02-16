import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;



public class Utils {

	static boolean printTypedGrid = true;

	public static double norm(double[] vector){

		double max = 0;
		for(int i=0; i<vector.length; i++){
			if(max < Math.abs(vector[i]))
				max = Math.abs(vector[i]);
		}
		return max;
	}

	public static double[] subtract(double[] a, double[] b){
		double[] res = new double[a.length];
		for(int i=0; i<res.length; i++)
			res[i] = a[i] - b[i];
		return res;
	}

	public static void add(double[] a, double[] b){
		for(int i=0; i<b.length; i++)
			a[i] += b[i];
	}

	public static double[] applyOperator(double[][] A, double[] v){
		double[] res = new double[v.length];
		for(int i=0; i<res.length; i++)
			for(int j=0; j<res.length; j++){
				//System.out.println(String.format("(%d,%d) : %f : %f", i, j, A[i-1][j-1],v[i]));
				res[i] += A[i][j] * v[j];
			}
		return res;
	}

	public static void printMatrix(double[][] M){
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

	public static void printVector(double[] v){
		System.out.println();
		System.out.print("[");
		for(int i=0;i<v.length;i++){
			System.out.print(v[i] + " ");
		}
		System.out.print("]");
		System.out.println();
	}

	public static void plot(double v[] , String name){
		ArrayList<Double> list = new ArrayList<Double>(); 
		double factor = (norm(v) < 0.01) ? 1000 : (norm(v) > 100) ? 1.0/1000 : 6;
		for(int i=0; i<v.length;i++){
			list.add(v[i]*factor);
		}
		DrawGraph mainPanel = new DrawGraph(list, norm(v));

		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	public static double[][] getGraphFromFile(String path){
		double A[][] = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = null;
			int rows = 0;
			while ((line = br.readLine()) != null) {
				String[] digits = line.trim().split(" ");
				if(A == null)
					A = new double[digits.length][digits.length];
				for(int j=0; j< A.length; j++)
					A[rows][j] = Double.parseDouble(digits[j]);
				rows++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return A;
	}

	public static double[][] getGraphFromSTPFile(String path){
		double A[][] = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = null;
			boolean end = false;
			while (!end && (line = br.readLine()) != null) {

				String[] split = line.trim().split(" ");
				if(split[0].contentEquals("SECTION") && split[1].contentEquals("Graph")){

					int numOfnodes = Integer.parseInt(br.readLine().trim().split(" ")[1]);
					A = new double[numOfnodes][numOfnodes];

					int numOfEdges = Integer.parseInt(br.readLine().trim().split(" ")[1]);
					while (!end && (line = br.readLine()) != null) {
						if(line.contains("END"))
							end = true;
						else{
							split = line.trim().split(" ");
							int src = Integer.parseInt(split[1]) - 1 ;
							int target = Integer.parseInt(split[2]) - 1;
							double weight = Double.parseDouble(split[3]);

							A[src][target] = weight;
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return A;
	}

	public static void graphToMmatrix(double[][] G){
		for(int i=0; i<G.length; i++){
			int numOfN = 0;
			for(int k=0; k<G.length; k++)
				if(i != k)
					numOfN = (G[i][k] != 0) ? numOfN+1 : numOfN;

			for(int j=0; j<G.length; j++){
				if(i!=j){
					if(G[i][j] != 0)
						G[i][j] = -1*G[i][j];
				}
				else
					G[i][i] = numOfN;	
			}
		}

	}



	public static boolean isZero(double[] v){
		boolean isZero = true;
		for(int i=0;i<v.length;i++)
			isZero &= v[i] == 0;
		return isZero;
	}

	public static boolean hasZeroRows(double[][] A){
		int counter = 0;
		for(int i=0;i<A.length;i++){
			if(isZero(A[i]))
				counter++;
		}
		//System.err.println("Num Of Zero Rows: " + counter);
		return counter>0;
	}

	public static double[][] FromImage(String path){
		ArrayList<double[]> A = new ArrayList<>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = br.readLine()) != null) {
				String split[] = line.split(",");
				double[] row = new  double[split.length];
				for(int i=0; i<row.length; i++)
					row[i] = Double.parseDouble(split[i]);
				A.add(row);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		double[][] I = null;
		if(A.size()>0){
			I = new double[A.size()][A.get(0).length];
			for(int i=0; i<A.size(); i++){
				I[i] = A.get(0);
			}
		}
		return I;
	}
	
	public static double[][] imageToGraph(String path){
		double[][] I = FromImage(path);
		double[][] graph = new double[I.length*I[0].length][I.length*I[0].length];
		System.out.println(graph.length);
		for(int i=0; i< I.length; i++){
			for(int j=0; j< I[0].length; j++){
				construct(graph, I, i,j);
			}
		}
		for(int i=0; i< graph.length; i++){
			double count = 0;
			for(int j=0; j< graph.length; j++){
				count += Math.abs(graph[i][j]);
			}
			
			graph[i][i] = count == 0 ? 1 : count;
		}
		return graph;
	}

	public static int[] toCoordinate(double[][] I, int indGraph){
		int i = indGraph/I.length;
		int j = indGraph % I.length;
		return new int[] {i,j};
	}
	
	private static void construct(double[][] graph, double[][] I, int i,int j){
		int rowSize =I.length;
		int ind = i*I.length + j; 

		boolean Iright = j+1 < I[0].length;
		boolean Ileft = j-1 >= 0;
		boolean Idown = i+1 < I.length;
		boolean Iup = i-1 >= 0;

		boolean graphLeft = ind-1 >=0;
		boolean graphRight = ind+1 < graph.length;
		boolean graphUp = (i-1)*rowSize >= 0 ;
		boolean graphDown= (i+1)*rowSize < graph.length;


		graph[ind][ind] = -I[i][j];
		if(graphLeft && Ileft)
			graph[ind][ind-1] = -I[i][j-1];//left
		if(graphRight && Iright)
			graph[ind][ind+1] = -I[i][j+1];//right	
		if(graphUp && Iup)
			graph[(i-1)*rowSize][ind] = -I[i-1][j];//up
		if(graphDown & Idown)
			graph[(i+1)*rowSize][ind] = -I[i+1][j];//down
		if(graphUp && graphRight && Idown && Iright)
			graph[(i-1)*rowSize][ind+1] = -I[i-1][j+1];//triangle up-right
		if(graphUp && graphLeft && Iup && Ileft)
			graph[(i-1)*rowSize][ind-1] = -I[i-1][j-1];//triangle up-left
		if(graphDown && graphRight && Idown && Iright)
			graph[(i+1)*rowSize][ind+1] = -I[i+1][j+1];//triangle down-right
		if(graphDown && graphLeft & Idown && Ileft)
			graph[(i+1)*rowSize][ind-1] = -I[i+1][j-1];//triangle down-left
	}

	public static void main(String[] args){
		//double[][] G = getGraphFromSTPFile("r-graph.stp");//getGraphFromFile("Clustering.txt");
		//graphToMmatrix(G);
		//printMatrix(G);
		double[][] image = imageToGraph("output.txt");
		//printMatrix(image);
	}
}
