

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GridPoint implements Comparable<GridPoint>{

	int id, order;
	double lamda;
	PointType type;
	
	Map<Integer, GridPoint> Ni, Ci , Dis, Diw;
	
	
	
	public GridPoint(int id, double value){
		this.id = id;
		this.lamda = value;
		this.type = PointType.UNASSIGN;
		
		this.order = -1;
		
		Ci = new HashMap<Integer, GridPoint>();
		Dis = new HashMap<Integer, GridPoint>();
		Diw = new HashMap<Integer, GridPoint>();
	}

	public void addToNi(GridPoint gp){
		Ni.put(gp.id, gp);
	}
	
	public Set<Integer> getNeighbors(){
		return Ni.keySet();
	}
	
	public void toCi(int gpId){
		GridPoint cpoint = Ni.remove(gpId);
		Ci.put(cpoint.id, cpoint);
	}

	@Override
	public int compareTo(GridPoint other) {
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
			sb.append(" " + i);
		}
		sb.append("\n");
		
		sb.append("Diw: ");
		for(int i: Diw.keySet()){
			sb.append(" " + i);
		}
		sb.append("\n\n\n\n");
		return sb.toString();
	}

}
