package winamax;

public class Position {
	int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position position) {
		this.x = position.x;
		this.y = position.y;
	}
	
	public Direction computeDirectionTo(Position position) {
		Debug.println("comparing " + this + " to " + position);
		if (position.y > y && position.x == x) {
			return Direction.DOWN;
		} else if (position.x < x && position.y == y) {
			return Direction.LEFT;
		} else if (position.x > x && position.y == y) {
			return Direction.RIGHT;
		} else if (position.y < y && position.x == x) {
			return Direction.UP;
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + "]";
	}
}