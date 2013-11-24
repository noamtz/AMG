import java.util.ArrayList;

import javax.swing.JFrame;




public class Version1 {

	private static final int N = 64;
	private static final double PI = Math.PI;

	public static void main(String args[]) throws Exception{
		Version1 m = new Version1();
		m.start();
	}

	public void start() throws Exception{

		Grid[] grids = init();


		double prevNorm = 1;
		int size = 20;
		plot(grids[0].v, "V: Vcycle-0");
		System.out.println("Before: " + norm(grids[0].v));
		for(int i=0; i<size; i++){
			vcycle(grids);
			double currentNorm = grids[0].getNormResidual();
			if(i>0){
				System.out.print("Vcycle: " + i + " current Norm: "+ currentNorm + " ");
				System.out.println(" Norm Rate: " + (int)(currentNorm/prevNorm * 100) + " %");
			}
			prevNorm = currentNorm;
			//plot(grids[0].v, "Vcycle-" + (i+1));
		}
		plot(grids[0].residual, "Residual: Vcycle-" + size);
		System.out.println("After: " + norm(grids[0].v));

	}

	
	public void plot(double v[] , String name){
		ArrayList<Double> list = new ArrayList<Double>(); 
		for(int i=0; i<v.length;i++){
			list.add(v[i]*6);
		}
	      DrawGraph mainPanel = new DrawGraph(list,norm(v));

	      JFrame frame = new JFrame(name);
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      frame.getContentPane().add(mainPanel);
	      frame.pack();
	      frame.setLocationByPlatform(true);
	      frame.setVisible(true);
	}
	
	public Grid[] init(){
		Grid[] grids = new Grid[log2(N)];
		for(int i=0; i<grids.length; i++){
			grids[i] = new Grid();
		}
		
		grids[0].v = new double[N];
		grids[0].f = new double[N];
		
		for(int i=0; i<N; i++){
			grids[0].v[i] = (Math.sin(i*40*PI/N) + Math.sin(i*16*PI/N)) * 1.0/2; 
		}
		
		
		return grids;
	}
	
	public static double norm(double[] vector){
		double max = 0;
		for(int i=0; i<vector.length; i++){
			if(max < Math.abs(vector[i]))
				max = Math.abs(vector[i]);
		}
		return max;
	}

	public static void relax(double[] v, double[] f , int numSweeps) throws Exception{
		double h = Math.pow(1.0/v.length, 2);

		for(int j = 0; j<numSweeps; j++){
			for(int i=0; i<v.length; i++){
				double before = i>0 ? v[i-1] : 0;
				double after = i<v.length-1 ? v[i+1] : 0;
				v[i] = 0.5*(before + after + h*f[i]);
			}
		}
	}

	public static double[] apply_operator(double[] v){
		double[] Av = new double[v.length];
		double h = Math.pow(v.length, 2);
		
		for(int i=0; i<v.length; i++){
			double before = i>0 ? v[i-1] : 0;
			double after = i<v.length-1 ? v[i+1] : 0;
			Av[i] = (2*v[i] - before - after)*h; 
		}
		return Av;
	}


	public static double[] restrict(double[] v){
		double[] restricted = new double[v.length/2];
		for(int i=0; i<restricted.length; i++){
			double before = i>0 ? v[2*i-1] : 0;
			double after = i<restricted.length-1 ? v[2*i+1] : 0;   
			restricted[i] = 0.25 * (2*v[i] + before + after);
		}
		return restricted;
	}

	public static double[] interpolate(double[] v){
		double[] interpolated = new double[v.length*2];
		for(int i=0; i<v.length; i++){
			double after = i==v.length-1 ? 0 : v[i+1];
			interpolated[2*i] = v[i]; 
			interpolated[2*i + 1] = 0.5*(v[i] + after);
		}
		return interpolated;
	}

	public static void vcycle(Grid[] grids) throws Exception{
		for(int i=0; i<grids.length-1; i++){
			relax(grids[i].v, grids[i].f , 2);
			grids[i].residual = subtract(grids[i].f, apply_operator(grids[i].v));
			grids[i+1].f = restrict(grids[i].residual);
			grids[i+1].v = new double[gridSize(i+1)];
		}

		relax(grids[grids.length-1].v, grids[grids.length-1].f , 2);
		
		for(int i=grids.length-2; i>0; i--){
			add(grids[i].v , interpolate(grids[i+1].v)); 
			relax(grids[i].v, grids[i].f, 2);
		}
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
}
