package winamax;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BallMove {
	public int ballIndex;
	public int numberOfMoves;
	public List<DirectionHolePair> availableDirections;

	public BallMove(int ballIndex, int numberOfMoves) {
		this.ballIndex = ballIndex;
		this.numberOfMoves = numberOfMoves;
		this.availableDirections = new ArrayList<>();
	}

	public BallMove(BallMove ballMove) {
		this.ballIndex = ballMove.ballIndex;
		this.numberOfMoves = ballMove.numberOfMoves;
		this.availableDirections = ballMove.availableDirections.stream()
				.map((DirectionHolePair ad) -> new DirectionHolePair(ad.direction, ad.hole))
				.collect(Collectors.toList());
	}
	
	public int getAvailableDirectionCount() {
		return availableDirections.size();
	}

	public long getHoleCount() {
		return availableDirections.stream()
				.filter(dir -> dir.hole)
				.count();
	}

	public int getBallIndex() {
		return ballIndex;
	}

	public int getNumberOfMoves() {
		return numberOfMoves;
	}

	public List<DirectionHolePair> getAvailableDirections() {
		return availableDirections;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ballIndex;
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
		BallMove other = (BallMove) obj;
		if (ballIndex != other.ballIndex)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BallStatistics [ballIndex=" + ballIndex + ", numberOfMoves=" + numberOfMoves
				+ ", availableDirections=" + availableDirections + "]";
	}
}