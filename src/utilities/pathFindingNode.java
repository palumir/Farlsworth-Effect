package utilities;

public class pathFindingNode {
	public boolean blocked = false;
	public int i;
	public int j;
	public int x;
	public int y;
	public int g = Integer.MAX_VALUE;
	public int h = Integer.MAX_VALUE;
	public int f = Integer.MAX_VALUE;
	public pathFindingNode parent;
	public pathFindingNode[] children = new pathFindingNode[8];
	
	public pathFindingNode(int x, int y) {
		this.x = x;
		this.y = y;
	}
}