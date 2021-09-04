package winamax;

import java.util.HashMap;
import java.util.Map;

public enum StaticTile {
	EMPTY('.') {
		@Override
		public boolean canPassTroughIt() {
			return true;
		}

		@Override
		public boolean canStopOnIt() {
			return true;
		}
	},
	OBSTACLE('X') {
		@Override
		public boolean canPassTroughIt() {
			return true;
		}

		@Override
		public boolean canStopOnIt() {
			return false;
		}
	},
	HOLE('H') {
		@Override
		public boolean canPassTroughIt() {
			return false;
		}

		@Override
		public boolean canStopOnIt() {
			return true;
		}
	};

	private static final Map<Character, StaticTile> BY_CHARACTER = new HashMap<>();

	static {
		for (StaticTile e : values()) {
			BY_CHARACTER.put(e.character, e);
		}
	}

	public final char character;

	private StaticTile(char character) {
		this.character = character;
	}

	public static StaticTile valueOfChar(char character) {
		return BY_CHARACTER.get(character);
	}

	@Override
	public String toString() {
		return String.valueOf(character);
	}

	public abstract boolean canPassTroughIt();

	public abstract boolean canStopOnIt();
}