package winamax;

public class Ball {
	int x, y;
	boolean toUpdate;
	boolean inAHole;
	int numberOfMoves;

	public Ball(int x, int y, int numberOfMoves) {
		this.x = x;
		this.y = y;
		this.numberOfMoves = numberOfMoves;
		this.toUpdate = true;
		this.inAHole = false;
	}

	public Ball(Ball ball) {
		this.x = ball.x;
		this.y = ball.y;
		this.numberOfMoves = ball.numberOfMoves;
		this.toUpdate = ball.toUpdate;
		this.inAHole = ball.inAHole;
	}

	public Position computeNextPosition(Direction direction) {
		return new Position(
				x + direction.xMove() * numberOfMoves,
				y + direction.yMove() * numberOfMoves);
	}

	public int computeNextPositionX(Direction direction) {
		return x + direction.xMove() * numberOfMoves;
	}

	public int computeNextPositionY(Direction direction) {
		return y + direction.yMove() * numberOfMoves;
	}

	@Override
	public String toString() {
		return "Ball [x=" + x + ", y=" + y + ", toUpdate=" + toUpdate + ", inAHole=" + inAHole + ", numberOfMoves="
				+ numberOfMoves + "]";
	}
}