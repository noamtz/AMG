
public enum PointType {
	UNASSIGN(0),
	C_POINT(1),
	F_POINT(2);
	private int value;
	private PointType (int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}