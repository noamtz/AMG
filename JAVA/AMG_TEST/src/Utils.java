import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;



public class Utils {
	
	static boolean printTypedGrid = false;

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
				System.out.print((int)M[i][j] + " ");
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
	
	public static void main(String[] args){
		double[][] G = getGraphFromFile("Clustering.txt");
		graphToMmatrix(G);
		printMatrix(G);
	}
}
