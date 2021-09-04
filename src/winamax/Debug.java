package winamax;

public class Debug {
	public static boolean enabled;

	public static void println(String string) {
		if (!enabled)
			return;
		System.err.println(string);
	}

	public static void println() {
		if (!enabled)
			return;
		System.err.println();
	}

	public static void println(Object o) {
		if (!enabled)
			return;
		System.err.println(o.toString());
	}

	public static void println(char[] cs) {
		if (!enabled)
			return;
		for (char c : cs) {
			System.err.print(c);
		}
		System.err.println();
	}
}