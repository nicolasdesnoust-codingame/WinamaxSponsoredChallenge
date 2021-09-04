package winamax;

public class DirectionHolePair {
	Direction direction;
	boolean hole;

	public DirectionHolePair(Direction direction, boolean hole) {
		this.direction = direction;
		this.hole = hole;
	}

	@Override
	public String toString() {
		return "BallStatisticsDirection [direction=" + direction + ", hole=" + hole + "]";
	}
}