package V;


import V.GridNode.NodeType;


public class Grid {

	GridNode nodes[];
	SparseMatrix A;
	SparseVector v, f, residual;
	
	SparseMatrix Interpolation, Restriction, A2h;
	
	public Grid(SparseMatrix A){
		nodes = new GridNode[A.size()];
		this.A = A;
		for(int i=0;i<A.size(); i++)
			nodes[i] = new GridNode(i, A.get(i, i));
	}
	
	public IndexMaxPQ<GridNode> nodesInSet(){
		IndexMaxPQ<GridNode> pq = new IndexMaxPQ<>(nodes.length);
		for(GridNode gn : nodes)
			pq.insert(gn.id,gn);		
		
		return pq;
	}
	
	public IndexMaxPQ<GridNode> getFNodes(){
		IndexMaxPQ<GridNode> pq = new IndexMaxPQ<>(nodes.length);
		for(GridNode gn : nodes)
			if(gn.type == NodeType.F)
				pq.insert(gn.id,gn);		
		
		return pq;
	}
	
	public SparseVector restrict(SparseVector v){
		return Restriction.times(v);
	}
	
	public SparseVector interpolate(SparseVector v){
		return Interpolation.times(v);
	}
	
	public void print(){

		int root = (int)Math.sqrt(nodes.length);
		for(int i=0; i<nodes.length; i++){
			GridNode gp = nodes[i];
			if(i % root == 0)
				System.out.println();
			System.out.print(gp.type.toString()+ " ");
		}
		System.out.println();
	}
}
