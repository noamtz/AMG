
public class Grid {
	GridPoint[] nodes;
	double[][] A;

	double[][] Interpolation, Restriction , A2h;

	public Grid(double[][] A){
		this.A = A;
		nodes = new GridPoint[A.length];
	}
	
	public double[] restrict(double[] v){
		double[] res = new double[Restriction.length];
		for(int i=0; i<Restriction.length; i++)
			for(int j=0; j<Restriction[0].length; j++){
				res[i] += Restriction[i][j] * v[j];
			}
		return res;
	}
	
	public double[] interpolate(double[] v){

		double[] res = new double[Interpolation.length];
		for(int i=0; i<Interpolation.length; i++)
			for(int j=0; j<Interpolation[0].length; j++)
				res[i] += Interpolation[i][j] * v[j];
		return res;
	}
	
	public void print(){
		if(!Utils.printTypedGrid)
			return;
		int root = (int)Math.sqrt(nodes.length);
		for(int i=0; i<nodes.length; i++){
			GridPoint gp = nodes[i];
			if(i % root == 0)
				System.out.println();
			System.out.print(gp.type.toString()+ " ");
		}
		System.out.println();
	}
	
}