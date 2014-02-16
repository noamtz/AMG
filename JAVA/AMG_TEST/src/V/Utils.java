package V;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;



public class Utils {

	static boolean printTypedGrid = false;

	public static double norm(SparseVector vector){

		double max = 0;
		Iterator<Integer> itr = vector.Iterator();
		while(itr.hasNext()){
			int i = itr.next();
			if(max < Math.abs(vector.get(i)))
				max = Math.abs(vector.get(i));
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

//	public static void printMatrix(double[][] M){
//		System.out.println();
//		System.out.print("[");
//		for(int i=0; i<M.length;i++){
//			for(int j=0;j<M[0].length;j++){
//				System.out.print((int)M[i][j] + " ");
//			}
//			System.out.println( (i<M.length-1) ?";" : "]");
//			System.out.print(" ");
//		}
//		System.out.println();
//	}
	
	public static void printMatrix(double[][] M){
		 try {
			BufferedWriter out = new BufferedWriter(new FileWriter("MATRIX.txt"));
			
			 out.newLine();
			 out.newLine();
			 out.append("************************************************************************");
			 out.append("			 PRINTING MATRIX			");
			 out.append("************************************************************************");
			 out.newLine();
			 out.newLine();
			 
			for(int i=0; i<M.length;i++){
				for(int j=0;j<M[0].length;j++){
					double target = 0;
					String s = (M[i][j] < 1) ? M[i][j] +" ": ((int)M[i][j]) + " " ;
					out.append(s);
				}
				out.append((i<M.length-1) ?";" : "]");
			    out.newLine();
			    out.append(" ");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	//	public static void plot(SparseVector v , String name){
	//		ArrayList<Double> list = new ArrayList<Double>(); 
	//		double factor = (norm(v) < 0.01) ? 1000 : (norm(v) > 100) ? 1.0/1000 : 6;
	//		for(int i=0; i<v.length;i++){
	//			list.add(v[i]*factor);
	//		}
	//		DrawGraph mainPanel = new DrawGraph(list, norm(v));
	//
	//		JFrame frame = new JFrame(name);
	//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//		frame.getContentPane().add(mainPanel);
	//		frame.pack();
	//		frame.setLocationByPlatform(true);
	//		frame.setVisible(true);
	//	}

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
					A[rows][j] = Integer.parseInt(digits[j]);
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

	//	public static void main(String[] args){
	//		double[][] G = getGraphFromSTPFile("r-graph.stp");//getGraphFromFile("Clustering.txt");
	//		graphToMmatrix(G);
	//		printMatrix(G);
	//	}

	public static SparseMatrix toSparse(double[][] A){
		SparseMatrix M = new SparseMatrix(A.length, A[0].length,true);
		for(int i=0; i<A.length; i++)
			for(int j=0; j<A.length; j++)
				if(A[i][j] != 0)
					M.put(i, j, A[i][j]);
		return M;
	}

	public static void plot(SparseVector v , String name){
		ArrayList<Double> list = new ArrayList<Double>(); 
		double factor = (norm(v) < 0.01) ? 1000 : (norm(v) > 100) ? 1.0/1000 : 6;
		double[] varr = v.toArray();
		for(int i=0; i<varr.length;i++){
			list.add(varr[i]*factor);
		}
		DrawGraph mainPanel = new DrawGraph(list, norm(v));

		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	public static boolean hasZeroRows(SparseMatrix A){
		for(int i=0; i<A.dimensions()[0]; i++)
			if(A.getRow(i).nnz() == 0){
				System.err.println("HAS ZERO ROW AT: " + i);
				return true;
			}
		return false;
	}

	//IMAGE GRAPH

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
static boolean a =false;
	public static double[][] imageToGraph(String path){
		double[][] I = FromImage(path);
		int size = I.length*I[0].length;
		double[][] graph = new double[size][size];
		for(int i=0; i< I.length; i++){
			for(int j=0; j< I[0].length; j++){

				int row = i*I[0].length +j;
				a = i==0&&j==0;
				if(I[i][j] == 0){
					I[i][j] = 1;
				}
				
				projection(graph,I,row,i,j-1);//left
				projection(graph,I,row,i,j+1);//right
				projection(graph,I,row,i-1,j);//up				
				projection(graph,I,row,i+1,j);//down	
				
				projection(graph,I,row,i-1,j-1); //up-left
				projection(graph,I,row,i-1,j+1);//up-right			
				projection(graph,I,row,i+1,j-1); //down-left
				projection(graph,I,row,i+1,j+1);//down-right
				

				//System.out.println(String.format("%d*%d + %d=%d", i,I.length,j,row));
			}
		}

		for(int i=0; i< graph.length; i++){
			double count = 0;
			for(int j=0; j< graph[0].length; j++){
				count += Math.abs(graph[i][j]);
				if(graph[i][j] != 0){
					graph[i][j] = -1*graph[i][j];
				}
			
			}

			graph[i][i] = count;
			//System.out.println(String.format("(%d,%d)=%f", i,i,count));
			
		}
		for(int i=0;i<graph.length;i++){
			for(int j=0;j<graph[0].length;j++){
				if(i!=j && graph[i][j] != 0)
					if(graph[j][i] == 0){
						graph[j][i] = 0.5*graph[i][j];
						graph[i][j] = 0.5*graph[i][j];
					}
				if(graph[i][j] != graph[j][i] ){
					graph[i][j] = graph[j][i] ;
					graph[j][i] = graph[i][j];
					//System.out.println(String.format("(%d,%d)=diff", i,j));
				}
			}
		}
		for(int i=0;i<graph.length;i++){
			for(int j=0;j<graph[0].length;j++){
				if(graph[i][j] != graph[j][i] ){
					System.out.println("adasd");
					System.exit(1);
				}
			}
		}
		return graph;
	}

	public static int[] toCoordinate(double[][] I, int indGraph){
		int i = indGraph/I.length;
		int j = indGraph % I.length;
		return new int[] {i,j};
	}

	private static void projection(double[][] graph, double[][] I,int row , int i,int j){
		int gind = i*I[0].length +j;
		
		if(gind >= 0 && gind < graph.length && j>=0 && i>=0 && j<I[0].length && i<I.length){
//			if(a)
//				System.out.println(String.format("allowed(%d,%d)", i,j));
			graph[row][gind] = 1;//I[i][j] == 0 ? 1 : I[i][j];
		} else {
//			if(a)
//				System.out.println(String.format("(%d,%d)", i,j));
		}
	}
}
