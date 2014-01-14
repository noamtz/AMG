package V;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GridNode implements Comparable<GridNode>{
	int id, order;
	double lamda;
	NodeType type;
	
	Map<Integer, GridNode> Ni, Ci , Dis, Diw ;
	Map<Integer, Double> Dependence;
	
	
	
	public GridNode(int id, double value){
		this.id = id;
		this.lamda = value;
		this.type = NodeType.UNASSIGN;
		
		this.order = -1;
		
		Ci = new HashMap<Integer, GridNode>();
		Dis = new HashMap<Integer, GridNode>();
		Diw = new HashMap<Integer, GridNode>();
		Dependence = new HashMap<Integer, Double>();
	}

	public void addToNi(GridNode gp){
		Ni.put(gp.id, gp);
	}
	
	public Set<Integer> getNeighbors(){
		return Ni.keySet();
	}
	
	public void toCi(int gpId){
		GridNode cpoint = Ni.remove(gpId);
		Ci.put(cpoint.id, cpoint);
	}

	@Override
	public int compareTo(GridNode other) {
		if(this.lamda > other.lamda) return 1;
		if(this.lamda < other.lamda) return -1;
		return 0;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("For Point " + id +  " " + type + ":\n");
		sb.append("Ci: ");
		for(int i: Ci.keySet()){
			sb.append(" " + i);
		}
		sb.append("\n");
		
		sb.append("Dis: ");
		for(int i: Dis.keySet()){
			sb.append(" " + i + " (" + Dependence.get(i) + ") ");
		}
		sb.append("\n");
		
		sb.append("Diw: ");
		for(int i: Diw.keySet()){
			sb.append(" " + i);
		}
		sb.append("\n\n\n\n");
		return sb.toString();
	}
	
	public enum NodeType{
		UNASSIGN(0),
		C(1),
		F(2);
		private int value;
		private NodeType (int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		
		public String toString(){
			if(value == 1)
				return "C";
			if(value == 2)
				return "F";
			return "U";
		}
	}
}
