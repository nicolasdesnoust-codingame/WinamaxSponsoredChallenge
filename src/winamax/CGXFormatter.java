package winamax;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.io.PrintWriter;

public class CGXFormatter {
    public void solve(int testNumber, Scanner in, PrintWriter out) {
        int width = in.nextInt();
        int height = in.nextInt();

        char[][] inputGolfCourse = new char[height][width];
        for (int y = 0; y < height; y++) {
            inputGolfCourse[y] = in.next().toCharArray();
            Debug.println(inputGolfCourse[y]);
        }

        GolfCourse golfCourse = new GolfCourse(inputGolfCourse, width, height);
        golfCourse = findBestPossibleMoves(golfCourse);
        out.println(golfCourse);
    }

    private static GolfCourse findBestPossibleMoves(GolfCourse initialGolfCourse) {
        Deque<GolfCourse> possibleMoves = new LinkedList<>();
        possibleMoves.push(initialGolfCourse);
        int compt = 0;

        while (!possibleMoves.isEmpty()) {
//			if (compt > 30) {
//				break;
//			}
            compt++;
            Debug.println("stack size: " + possibleMoves.size());
            GolfCourse currentGolfCourse = possibleMoves.pop();
            Debug.println("Trying:");
            Debug.println("---------------");
            Debug.println(currentGolfCourse.getDynamicTilesAsString());
            Debug.println("---------------");
            Debug.println();

            if (currentGolfCourse.won()) {
                Debug.println("won!");
                return currentGolfCourse;
            }

            List<GolfCourse> nextPossibleMoves = currentGolfCourse.getNextGolfCourses();
            if (nextPossibleMoves.isEmpty()) {
                Debug.println("Stuck, going back");
            }

            for (int i = nextPossibleMoves.size() - 1; i >= 0; i--) {
                possibleMoves.push(nextPossibleMoves.get(i));
            }
        }

        return null;
    }
}
