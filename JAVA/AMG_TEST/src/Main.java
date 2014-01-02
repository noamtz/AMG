import java.util.ArrayList;


public class Main {

	static Amg amg;
	static ArrayList<Grid> grids;
	public static void main(String[] args){
		Main m = new Main();
		amg = new Amg();
		m.start();
	}

	public void init(){
		grids = new ArrayList<>();

		Grid grid = new Grid();

		grid.v = new double[N+1];
		grid.f = new double[N+1];

		for(int i=0; i<=N; i++){
			grid.v[i] = (Math.sin(i*32*PI/N) + Math.sin(i*6*PI/N) + Math.sin(i*PI/N)) * 1.0/3;
			grid.f[i] = 0;
		}
		//Boundary conditions
		grid.v[0] = -1;
		grid.v[N] = 1;

		grids.add(grid);
	}

	public void start() throws Exception{

		long totalTime = 0;
		int numOfCycles = 10;

		Grid grid = grids.get(0);

		plot(grid.v, "V: Vcycle-0");

		double prevNorm = norm(subtract(grid.f, apply_operator(grid.v)));;
		double initialNorm = prevNorm;

		System.out.println("Start residual Norm: "+ prevNorm);

		for(int i=0; i<numOfCycles; i++){
			totalTime += vcycle();
			double currentNorm = grid.getNormResidual();
			System.out.print("Vcycle: " + (i+1) + " current residual Norm: "+ currentNorm + " ");
			System.out.println(" Norm Rate: " + (int)(currentNorm/prevNorm * 100) + " %");
			prevNorm = currentNorm;
		}

		//plot(grids[0].residual, "Residual: Vcycle-" + numOfCycles);
		plot(grid.v, "V: Vcycle-" + numOfCycles);

		System.out.println();
		System.out.println("Summary: ");
		System.out.println("Start residual norm: " + initialNorm);
		System.out.println("End residual norm: " + norm(grid.residual));
		System.out.println("Boundries: u[0]=" + grid.v[0] + " , u["+N+"]=" + grid.v[N]);
		System.out.println("Elapsed time for " + numOfCycles + " V-cycles: " + totalTime/1000.0 + " sec. for: " + N + " points");


		vectorToMatlab(grids[0].v);
	}

	public long vcycle() throws Exception{
		long start = System.currentTimeMillis();
		int coarsest = grids.size()-1;
		for(int i=0; i< coarsest; i++){
			relax(grids.get(i), 2);
			grids.get(i).residual = subtract(grids.get(i).f, apply_operator(grids.get(i).v));
			grids.get(i+1).f = restrict(grids.get(i).residual);
			grids.get(i+1).v = new double[gridSize(i+1)+1];
		}

		relax(grids.get(coarsest).v, grids.get(coarsest).f , 2);

		for(int i=coarsest-1; i>=0; i--){
			add(grids.get(i).v , interpolate(grids.get(i+1).v));
			relax(grids.get(i), 2);
		}
		return System.currentTimeMillis() - start;
	}

	public void relax(Grid grid ,int swipes){
		double[] v = grid.v;
		double[] f = grid.f;
		double[][] A = grid.A;
		for(int k=0; k< swipes; k++){
			for(int i=0; i< grid.size(); i++){
				for(int j=0; j< grid.size(); j++){
					v[i] = (f[i] - A[i][j]*v[i]);
				}
			}
		}

	}
}
