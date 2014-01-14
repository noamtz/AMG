package V;

import java.util.Iterator;

import V.GridNode.NodeType;

public class AMG {


	/**
	 * Classify the grid to C or F NODE
	 * @param grid
	 * @return The number of C NODES in the grid
	 */
	public int classifyGrid(Grid grid){

		IndexMaxPQ<GridNode> pq = grid.nodesInSet();
		GridNode[] nodes = grid.nodes;
		SparseMatrix A = grid.A;
		GridNode gn = null;
		int numOfC = 0;
		while(!pq.isEmpty()){
			int del = pq.delMax();
			gn = nodes[del];
			System.out.println("Prime deleted: " + del);

			gn.type = NodeType.C;
			grid.print();

			gn.order = numOfC++;


			SparseVector neighbors = A.getRow(gn.id);
			Iterator<Integer> itr = neighbors.Iterator();
			while(itr.hasNext()){
				Integer i = itr.next();
				if(i != gn.id){
					nodes[i].type = NodeType.F;

					if(pq.contains(i)){
						System.out.println("Secondery deleted: " + i);
						pq.delete(nodes[i].id);
					}

				}
			}
			itr = neighbors.Iterator();

			while(itr.hasNext()){
				Integer i = itr.next();
				if(i != gn.id){
					SparseVector nn = A.getRow(i);
					Iterator<Integer> itr2 = neighbors.Iterator();
					while(itr2.hasNext()){
						Integer n = itr2.next();
						if(i != n && nodes[n].type == NodeType.UNASSIGN)
							nodes[n].lamda++;
					}
				}
			}

		}
		return numOfC;
	}

	/**
	 * Classify for each grid point.
	 * @param Ni
	 * @param nodes
	 */
	public void classify(SparseMatrix A, GridNode[] nodes){
		for(GridNode gn : nodes)
			classify(gn, A.getRow(gn.id), nodes);
	}

	/**
	 * Classify for each grid point.
	 * @param Ni
	 * @param nodes
	 */
	public void classify(SparseVector Ni, GridNode[] nodes){
		for(GridNode gn : nodes)
			classify(gn, Ni, nodes);
	}

	/**
	 * Classify for each point the grid its neighbors for
	 *  
	 * 		Ci - Coarse interpolarity set ( only c points ) 
	 * 		Dis - Strong dependent set ( only f points )
	 * 		Diw - Weak dependent set ( c & f points)
	 * 
	 * @param gn
	 * @param Ni
	 * @param nodes
	 */
	public void classify(GridNode gn, SparseVector Ni, GridNode[] nodes){
		//maximum
		double max = Double.MIN_VALUE;
		Iterator<Integer> itr = Ni.Iterator();
		while(itr.hasNext()){
			int i = itr.next();
			if(i != gn.id)
				if(max < -Ni.get(i))
					max = -Ni.get(i); 
		}
		itr = Ni.Iterator();
		while(itr.hasNext()){
			int i = itr.next();
			if(i != gn.id)
				if(-Ni.get(i) >= 0.2*max){
					if(nodes[i].type == NodeType.C)
						gn.Ci.put(i, nodes[i]);
					else{
						gn.Dis.put(i, nodes[i]);
						gn.Dependence.put(i,-Ni.get(i));
					}
				}
				else
					gn.Diw.put(i, nodes[i]);
		}
	}

	public double computeWeight(GridNode gn, SparseMatrix A){
		double sum = 0;
		for(int j : gn.Ci.keySet())
			sum += computeWeight2pt(gn, j, A);

		return sum;
	}

	public double computeWeight2pt(GridNode gn, int j, SparseMatrix A){
		double aii = A.get(gn.id, gn.id);
		double Dw = 0;
		for(int n : gn.Diw.keySet())
			Dw += A.get(gn.id,n);
		double denominator = aii + Dw;
		double Ds = 0;

		for(int m : gn.Dis.keySet()){
			double Dsnomirator = A.get(gn.id,m)*A.get(m,j);
			double Dsdenomirator = 0;
			int aaa = -1;
			for(int k : gn.Ci.keySet()){
				Dsdenomirator += A.get(m,k);
				aaa = k;
			}
			if(Dsdenomirator == 0){
				Dsdenomirator = 1;

				//				if(aaa != -1)
				//				System.out.println(String.format("A[%d][%d] = %f", m,aaa,A[m][aaa]));
				//				//System.out.println("ID: " + gp.id + " , TYPE: " + gp.type + " Ci size:" + gp.Ci.keySet().size() + ", Dsdenomirator: " + Dsdenomirator);
				//				//Utils.printVector(A[m]);
				//				System.out.println(gp);
			}
			Ds += Dsnomirator/Dsdenomirator;
		}

		double numerator = A.get(gn.id,j) + Ds;
		return (numerator / denominator) * -1;
	}

	public SparseMatrix buildInterpolation(GridNode[] nodes, SparseMatrix A, int Nc){
		//		double[][] I = new double[A.length][Nc];
		SparseMatrix I = new SparseMatrix(A.size());
		for(GridNode gn : nodes){
			if(gn.type == NodeType.C)
				I.put(gn.id,gn.order, 1);
			else
				for(int j : gn.Ci.keySet())
					I.put(gn.id,nodes[j].order ,computeWeight2pt(gn, j, A));
		}
		return I;
	}

	public SparseMatrix buildRestriction(SparseMatrix Inter , int Nc){ 
		SparseMatrix I = new SparseMatrix(Inter.size());

		for(int i=0; i< Inter.size(); i++){
			SparseVector row = Inter.getRow(i);
			Iterator<Integer> itr = row.Iterator();
			while(itr.hasNext()){
				int j = itr.next();
				I.put(j, i, Inter.get(i, j));
			}

		}

		return I;
	}

	public SparseMatrix buildA2h(SparseMatrix Rest, SparseMatrix A, SparseMatrix Inter){
		return multiply(multiply(Rest, A), Inter);
	}

	/**
	 * Multiply 2 Sparse Matrices
	 * @param A
	 * @param B
	 * @return
	 */

	public SparseMatrix multiply(SparseMatrix A, SparseMatrix B){
		SparseMatrix res = new SparseMatrix(A.size());
		B.computeCols();
		for(int k = 0; k < B.size(); k++){
			for (int i = 0; i < A.size(); i++) {
				SparseVector row = A.getRow(i);
				SparseVector col = B.getCols(k);
				res.put(k, i, row.dot(col));
			}
		}
		return res;
	}
	
	/**
	 * Perform 2 grid schema
	 * @param A
	 * @return
	 */
	public void start(Grid g){
		int numOfC = classifyGrid(g);
		classify(g.A, g.nodes);
		
		g.Interpolation = buildInterpolation(g.nodes, g.A, numOfC);
		g.Restriction = buildRestriction(g.Interpolation,numOfC);
		g.A2h = buildA2h(g.Restriction, g.A, g.Interpolation);
	}
}
