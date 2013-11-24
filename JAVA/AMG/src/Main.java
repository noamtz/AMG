import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;




public class Main {

	private static final int N = 512;
	private static final double PI = Math.PI;

	public static void main(String args[]) throws Exception{

		Main m = new Main();
		m.start();
	}
	
	public Grid[] init(){
		Grid[] grids = new Grid[log2(N)];
		for(int i=0; i<grids.length; i++){
			grids[i] = new Grid();
		}

		grids[0].v = new double[N+1];
		grids[0].f = new double[N+1];

		for(int i=0; i<=N; i++){
			grids[0].v[i] = (Math.sin(i*32*PI/N) + Math.sin(i*6*PI/N) + Math.sin(i*PI/N)) * 1.0/3;
			grids[0].f[i] = 0;
		}
		//Boundary conditions
		grids[0].v[0] = -1;
		grids[0].v[N] = 1;

		return grids;
	}

	public void start() throws Exception{

		Grid[] grids = init();

		long totalTime = 0;
		int numOfCycles = 10;

		plot(grids[0].v, "V: Vcycle-0");

		double prevNorm = norm(subtract(grids[0].f, apply_operator(grids[0].v)));;
		double initialNorm = prevNorm;

		System.out.println("Start residual Norm: "+ prevNorm);
		
		for(int i=0; i<numOfCycles; i++){
			totalTime += vcycle(grids);
			double currentNorm = grids[0].getNormResidual();
			System.out.print("Vcycle: " + (i+1) + " current residual Norm: "+ currentNorm + " ");
			System.out.println(" Norm Rate: " + (int)(currentNorm/prevNorm * 100) + " %");
			prevNorm = currentNorm;
		}

		//plot(grids[0].residual, "Residual: Vcycle-" + numOfCycles);
		plot(grids[0].v, "V: Vcycle-" + numOfCycles);

		System.out.println();
		System.out.println("Summary: ");
		System.out.println("Start residual norm: " + initialNorm);
		System.out.println("End residual norm: " + norm(grids[0].residual));
		System.out.println("Boundries: u[0]=" + grids[0].v[0] + " , u["+N+"]=" + grids[0].v[N]);
		System.out.println("Elapsed time for " + numOfCycles + " V-cycles: " + totalTime/1000.0 + " sec. for: " + N + " points");
		

		vectorToMatlab(grids[0].v);
	}


	public static long vcycle(Grid[] grids) throws Exception{
		long start = System.currentTimeMillis();
		int coarsest = grids.length-1;
		for(int i=0; i< coarsest; i++){
			relax(grids[i].v, grids[i].f , 2);
			grids[i].residual = subtract(grids[i].f, apply_operator(grids[i].v));
			grids[i+1].f = restrict(grids[i].residual);
			grids[i+1].v = new double[gridSize(i+1)+1];
		}

		relax(grids[coarsest].v, grids[coarsest].f , 2);

		for(int i=coarsest-1; i>=0; i--){
			add(grids[i].v , interpolate(grids[i+1].v));
			relax(grids[i].v, grids[i].f, 2);
		}
		return System.currentTimeMillis() - start;
	}


	public static void relax(double[] v, double[] f , int numSweeps) throws Exception{
		double h = Math.pow(1.0/(v.length-1), 2);
		for(int j = 0; j<numSweeps; j++){
			for(int i=1; i<v.length-1; i++){
				v[i] = 0.5*(v[i-1] + v[i+1] + h*f[i]);
			}
		}

	}

	public static double[] apply_operator(double[] v){
		double[] Av = new double[v.length];
		double h = Math.pow((v.length-1), 2); // domain is 64 not 65

		for(int i=1; i<v.length-1; i++){
			Av[i] = (2*v[i] - v[i-1] - v[i+1] )*h; 
		}
		return Av;
	}


	public static double[] restrict(double[] v){
		double[] restricted = new double[v.length/2+1];
		for(int i=1; i<=v.length/2-1; i++){  
			restricted[i] = 0.25 * (2*v[2*i] + v[2*i-1] + v[2*i+1]);
		}
		return restricted;
	}

	public static double[] interpolate(double[] v){
		double[] interpolated = new double[v.length*2+1];
		for(int i=0; i<interpolated.length/2-1; i++){
			interpolated[2*i] = v[i]; 
			interpolated[2*i+1] = 0.5*(v[i] + v[i+1]);
		}
		return interpolated;
	}



	public static int gridSize(int level){
		return (int)Math.pow(2, log2(N)-level);
	}

	public static double[] subtract(double[] a, double[] b){
		double[] res = new double[a.length];
		for(int i=0; i<res.length; i++)
			res[i] = a[i] - b[i];
		return res;
	}

	public static void add(double[] a, double[] b){
		for(int i=0; i<a.length; i++)
			a[i] += b[i];
	}

	private class Grid{
		double[] v;
		double[] f;
		double[] residual;

		public double getNormResidual(){
			return norm(residual);
		}
	}


	static int log2(int x)
	{
		return (int) (Math.log(x) / Math.log(2));
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



	public static double norm(double[] vector){

		double max = 0;
		for(int i=0; i<vector.length; i++){
			if(max < Math.abs(vector[i]))
				max = Math.abs(vector[i]);
		}
		return max;
	}

	public static void vectorToMatlab(double[] v){
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for(int i = 0;i<v.length;i++){
			sb.append(Double.toString(v[i]) + " ");
		}
		sb.append("]");
		writeToFile(sb.toString());
	}

	public static void writeToFile(String content){
		File file = new File("out.txt");
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) 
				file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
