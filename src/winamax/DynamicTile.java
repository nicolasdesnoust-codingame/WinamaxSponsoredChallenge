package winamax;

import java.util.HashMap;
import java.util.Map;

public enum DynamicTile {
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
	LEFT('>') {
		@Override
		public boolean canPassTroughIt() {
			return false;
		}

		@Override
		public boolean canStopOnIt() {
			return false;
		}
	},
	RIGHT('<') {
		@Override
		public boolean canPassTroughIt() {
			return false;
		}

		@Override
		public boolean canStopOnIt() {
			return false;
		}
	},
	DOWN('v') {
		@Override
		public boolean canPassTroughIt() {
			return false;
		}

		@Override
		public boolean canStopOnIt() {
			return false;
		}
	},
	UP('^') {
		@Override
		public boolean canPassTroughIt() {
			return false;
		}

		@Override
		public boolean canStopOnIt() {
			return false;
		}
	},
	BALL('B') {
		@Override
		public boolean canPassTroughIt() {
			return false;
		}

		@Override
		public boolean canStopOnIt() {
			return false;
		}
	};

	private static final Map<Character, DynamicTile> BY_CHARACTER = new HashMap<>();

	static {
		for (DynamicTile e : values()) {
			BY_CHARACTER.put(e.character, e);
		}
	}

	public final char character;

	private DynamicTile(char character) {
		this.character = character;
	}

	public static DynamicTile valueOfChar(char character) {
		return BY_CHARACTER.get(character);
	}

	@Override
	public String toString() {
		return String.valueOf(character);
	}

	public abstract boolean canPassTroughIt();

	public abstract boolean canStopOnIt();
}