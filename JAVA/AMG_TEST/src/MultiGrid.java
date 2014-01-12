
public class MultiGrid {
	
	double[][] A;
	double[] v;
	double[] f;
	double[] residual;
	
	Grid amgGrid;

	public double getNormResidual(){
		return norm(residual);
	}

	public int size(){
		return A.length;
	}
	
	public static double norm(double[] vector){

		double max = 0;
		for(int i=0; i<vector.length; i++){
			if(max < Math.abs(vector[i]))
				max = Math.abs(vector[i]);
		}
		return max;
	}

}
