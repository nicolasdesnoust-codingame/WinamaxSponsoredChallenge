package winamax;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
	DOWN('v') {
		@Override
		public int xMove() {
			return 0;
		}

		@Override
		public int yMove() {
			return 1;
		}
	},
	RIGHT('>') {
		@Override
		public int xMove() {
			return 1;
		}

		@Override
		public int yMove() {
			return 0;
		}
	},
	UP('^') {
		@Override
		public int xMove() {
			return 0;
		}

		@Override
		public int yMove() {
			return -1;
		}
	},
	LEFT('<') {
		@Override
		public int xMove() {
			return -1;
		}

		@Override
		public int yMove() {
			return 0;
		}
	};

	private static final Map<Character, Direction> BY_CHARACTER = new HashMap<>();

	static {
		for (Direction e : values()) {
			BY_CHARACTER.put(e.character, e);
		}
	}

	public final char character;

	private Direction(char character) {
		this.character = character;
	}

	public static Direction valueOfChar(char character) {
		return BY_CHARACTER.get(character);
	}

	@Override
	public String toString() {
		return String.valueOf(character);
	}

	public abstract int xMove();

	public abstract int yMove();
}