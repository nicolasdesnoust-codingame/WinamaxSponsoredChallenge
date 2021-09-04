package winamax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GolfCourse {
	private final int width;
	private final int height;
	private final DynamicTile[][] dynamicTiles;
	private final StaticTile[][] staticTiles;
	private final List<Ball> balls;

	private List<BallMove> ballMoves;
	private Map<Position, List<BallMove>> crossedPositions;

	private Comparator<BallMove> ballStatisticsComparator = Comparator
			.comparingInt(BallMove::getAvailableDirectionCount)
			.thenComparing(Comparator.comparingInt(BallMove::getNumberOfMoves))
			.thenComparing(Comparator.comparingLong(BallMove::getHoleCount).reversed());

	public GolfCourse(char[][] inputGolfCourse, int width, int height) {
		this.width = width;
		this.height = height;
		this.balls = new ArrayList<>();
		this.ballMoves = new ArrayList<>();
		this.crossedPositions = new HashMap<>();
		this.dynamicTiles = new DynamicTile[height][width];
		this.staticTiles = new StaticTile[height][width];

		for (int y = 0; y < height; y++) {
			Arrays.fill(this.dynamicTiles[y], DynamicTile.EMPTY);
			Arrays.fill(this.staticTiles[y], StaticTile.EMPTY);

			for (int x = 0; x < width; x++) {
				char currentCharacter = inputGolfCourse[y][x];
				if (Character.isDigit(currentCharacter)) {
					this.balls.add(new Ball(x, y, Character.getNumericValue(currentCharacter)));
					this.dynamicTiles[y][x] = DynamicTile.BALL;
				} else if (currentCharacter == 'X' || currentCharacter == 'H') {
					this.staticTiles[y][x] = StaticTile.valueOfChar(currentCharacter);
				}
			}
		}
	}

	public GolfCourse(GolfCourse golfCourse) {
		this.width = golfCourse.width;
		this.height = golfCourse.height;
		this.balls = golfCourse.balls.stream()
				.map(Ball::new)
				.collect(Collectors.toList());
		this.ballMoves = golfCourse.ballMoves.stream()
				.map(BallMove::new)
				.collect(Collectors.toList());
		this.crossedPositions = golfCourse.crossedPositions.entrySet().stream()
				.collect(Collectors.toMap(
						e -> new Position(e.getKey()),
						e -> e.getValue().stream().map(BallMove::new).collect(Collectors.toList())));
		this.dynamicTiles = Arrays.stream(golfCourse.dynamicTiles)
				.map(a -> (DynamicTile[]) a.clone())
				.toArray(DynamicTile[][]::new);
		this.staticTiles = golfCourse.staticTiles;
	}

	public boolean won() {
		return balls.stream().allMatch(ball -> ball.inAHole);
	}

	public List<GolfCourse> getNextGolfCourses() {
		findOrUpdateBallMoves();
		if (ballMoves.isEmpty()) {
			return List.of();
		}

		for (BallMove ballMove : ballMoves) {
			if (ballMove.numberOfMoves == 1 && ballMove.getHoleCount() == 0) {
				Debug.println("hole count 0 for " + ballMove + balls.get(ballMove.ballIndex));
				return List.of();
			}
		}

		sortBallMoves(ballMoves);

		Debug.println("Possible moves : ");
		ballMoves.forEach(e -> Debug.println(balls.get(e.ballIndex) + ": " + e.availableDirections));

		Optional<BallMove> mandatoryBallMove = findAMandatoryBallMove(ballMoves);
		if (mandatoryBallMove.isPresent()) {
			Debug.println("Found a mandatory move");
			move(mandatoryBallMove.get());
			return List.of(this);
		}

		GolfCourse nextGolfCourse = new GolfCourse(this);
		BallMove bestBallMove = ballMoves.get(0);
		nextGolfCourse.move(bestBallMove);
		Direction direction = bestBallMove.getAvailableDirections().remove(0).direction;
		removeBallMoveFromCrossedPositions(bestBallMove, direction);
		if (bestBallMove.getAvailableDirections().isEmpty()) {
			ballMoves.remove(0);
		}

		List<GolfCourse> nextGolfCourses = new ArrayList<>();
		nextGolfCourses.add(nextGolfCourse);
		nextGolfCourses.add(this);
		return nextGolfCourses;
	}

	private void findOrUpdateBallMoves() {
		for (int i = 0; i < balls.size(); i++) {
			Ball currentBall = balls.get(i);
			if (!currentBall.toUpdate || currentBall.inAHole) {
				continue;
			}
			currentBall.toUpdate = false;
			BallMove currentBallMove = new BallMove(i, currentBall.numberOfMoves);
			for (Direction direction : Direction.values()) {
				if (moveIsPossible(currentBall, direction)) {
					List<Position> ballCrossedPositions = getCrossedPositions(currentBall, direction);
					Position lastPosition = ballCrossedPositions.get(ballCrossedPositions.size() - 1);
					boolean hole = staticTiles[lastPosition.y][lastPosition.x].equals(StaticTile.HOLE);
					currentBallMove.availableDirections.add(new DirectionHolePair(direction, hole));
					saveCrossedPositions(ballCrossedPositions, currentBallMove);
				}
			}

			if (!currentBallMove.availableDirections.isEmpty()) {
				ballMoves.add(currentBallMove);
			}
			if (currentBallMove.numberOfMoves == 1 && currentBallMove.getHoleCount() == 0) {
				Debug.println("hole count 0 for " + currentBallMove + currentBall);
				ballMoves = List.of();
			}
		}
	}

	private List<Position> getCrossedPositions(Ball ball, Direction direction) {
		List<Position> ballCrossedPositions = new ArrayList<>();

		Position lastPosition = new Position(ball.computeNextPosition(direction));
		Position currentPosition = new Position(ball.x, ball.y);

		while (!currentPosition.equals(lastPosition)) {
			currentPosition.x += direction.xMove();
			currentPosition.y += direction.yMove();
			ballCrossedPositions.add(new Position(currentPosition));
		}

		return ballCrossedPositions;
	}

	private void saveCrossedPositions(List<Position> positions, BallMove ballMove) {
		for (Position position : positions) {
			List<BallMove> currentBallMoves = crossedPositions.getOrDefault(position, new ArrayList<>());
			currentBallMoves.add(ballMove);
			crossedPositions.put(position, currentBallMoves);
		}
	}

	private void sortBallMoves(List<BallMove> rawBallMoves) {
		rawBallMoves.sort(ballStatisticsComparator);
		rawBallMoves.forEach(ballMove -> ballMove.availableDirections.sort(
				(o1, o2) -> Boolean.compare(o2.hole, o1.hole)));
	}

	private Optional<BallMove> findAMandatoryBallMove(List<BallMove> rawBallMoves) {
		for (BallMove ballMove : rawBallMoves) {
			if (ballMove.availableDirections.size() == 1) {
				return Optional.of(ballMove);
			}

			if (ballMove.numberOfMoves == 1 && ballMove.getHoleCount() == 1) {
				return Optional.of(ballMove);
			}
		}

		return Optional.empty();
	}

	private void move(BallMove ballMove) {
		Ball ball = balls.get(ballMove.ballIndex);
		Direction direction = ballMove.availableDirections.get(0).direction;
		Position currentPosition = new Position(ball.x, ball.y);
		int numberOfMoves = ball.numberOfMoves;

		while (numberOfMoves > 0) {
			dynamicTiles[currentPosition.y][currentPosition.x] = DynamicTile.valueOfChar(direction.character);
			currentPosition.y += direction.yMove();
			currentPosition.x += direction.xMove();
			numberOfMoves--;

			removeBallMovesFromCrossedPositions(ballMove, currentPosition);
		}

		ballMoves.remove(ballMove);
		for (int i = 1; i < ballMove.availableDirections.size(); i++) {
			removeBallMoveFromCrossedPositions(ballMove, ballMove.availableDirections.get(i).direction);
		}

		ball.x = currentPosition.x;
		ball.y = currentPosition.y;
		ball.numberOfMoves--;
		dynamicTiles[currentPosition.y][currentPosition.x] = DynamicTile.BALL;
		ball.toUpdate = true;
		if (staticTiles[ball.y][ball.x].equals(StaticTile.HOLE)) {
			Debug.println(ball + " is in a hole");
			ball.inAHole = true;
		}
	}

	private void removeBallMovesFromCrossedPositions(BallMove ballMove, Position currentPosition) {
		Debug.println("Case " + currentPosition + " is taken now.");
		List<BallMove> crossedBallMoves = crossedPositions.remove(currentPosition);
		if (crossedBallMoves != null) {
			for (BallMove crossedBallMove : crossedBallMoves) {
				if (crossedBallMove.equals(ballMove) || !ballMoves.contains(crossedBallMove)) {
					continue;
				}

				BallMove ballMoveToUpdate = ballMoves.get(ballMoves.indexOf(crossedBallMove));
				Ball affectedBall = balls.get(crossedBallMove.ballIndex);
				final Direction affectedDirection = new Position(affectedBall.x, affectedBall.y)
						.computeDirectionTo(currentPosition);
				Debug.println("affected direction : " + affectedDirection);
				Debug.println(affectedBall + " crossed");

				removeBallMoveFromCrossedPositions(ballMoveToUpdate, affectedDirection);
				ballMoveToUpdate.availableDirections = crossedBallMove.availableDirections
						.stream().filter(bm -> !bm.direction.equals(affectedDirection))
						.collect(Collectors.toList());
				Debug.println("with new directions: " + ballMoveToUpdate.availableDirections);
				if (crossedBallMove.availableDirections.isEmpty()) {
					ballMoves.remove(crossedBallMove);
				}
			}
		}
	}

	private void removeBallMoveFromCrossedPositions(BallMove ballMove, Direction direction) {
		Ball ball = balls.get(ballMove.ballIndex);

		Position currentPosition = new Position(ball.x, ball.y);
		int numberOfMoves = ball.numberOfMoves;

		while (numberOfMoves > 0) {
			currentPosition.y += direction.yMove();
			currentPosition.x += direction.xMove();
			numberOfMoves--;
			List<BallMove> crossedBallMoves = crossedPositions.get(currentPosition);
			if (crossedBallMoves != null) {
				crossedBallMoves.remove(ballMove);
				if (crossedBallMoves.isEmpty()) {
					crossedPositions.remove(currentPosition);
				}
			}
		}
	}

	private boolean moveIsPossible(Ball ball, Direction direction) {
		if (ball.numberOfMoves == 0) {
			return false;
		}

		if (staticTiles[ball.y][ball.x] == StaticTile.HOLE) {
			return false;
		}

		int lastPositionX = ball.x + direction.xMove() * ball.numberOfMoves;
		if (lastPositionX < 0 || lastPositionX >= width) {
			return false;
		}

		int lastPositionY = ball.y + direction.yMove() * ball.numberOfMoves;
		if (lastPositionY < 0 || lastPositionY >= height) {
			return false;
		}

		int currentX = ball.x;
		int currentY = ball.y;
		int numberOfMoves = ball.numberOfMoves;
		while (numberOfMoves > 0) {
			currentY += direction.yMove();
			currentX += direction.xMove();
			numberOfMoves--;

			if (numberOfMoves > 0 && (!staticTiles[currentY][currentX].canPassTroughIt()
					|| !dynamicTiles[currentY][currentX].canPassTroughIt())) {
				return false;
			}
		}

		if (!staticTiles[currentY][currentX].canStopOnIt()
				|| !dynamicTiles[currentY][currentX].canStopOnIt()) {
			return false;
		}

		return true;
	}

	public String getStaticTilesAsString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				stringBuilder.append(staticTiles[y][x]);
			}
			stringBuilder.append('\n');
		}

		return stringBuilder.toString();
	}

	public String getDynamicTilesAsString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				stringBuilder.append(dynamicTiles[y][x]);
			}
			stringBuilder.append('\n');
		}

		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		return getDynamicTilesAsString()
				.replace(DynamicTile.BALL.character, DynamicTile.EMPTY.character);
	}
}