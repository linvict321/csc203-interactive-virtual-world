import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PathingTest {

    @Test
    public void testSingleStepNoObstacles() {
        // Grid for testing --> 2D array
        boolean[][] grid = {
                { true, true, true },
                { true, true, true },
                { true, true, true }
        };

        PathingStrategy ps = new SingleStepPathingStrategy();
        List<Point> path = ps.computePath(
                new Point(0, 0), new Point(2, 2), // start, end
                (p) -> withinBounds(p, grid) && grid[p.y][p.x], // canPassThrough
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        // expected path => [(0, 1)] <=
        assertEquals(path, Arrays.asList(new Point(0, 1)));
    }

    // Write more tests including obstacles and other edge cases.
    @Test
    public void testSingleStepObstacles() {
        // Grid for testing --> 2D array
        boolean[][] grid = {
                { true, false, true },
                { true, true, true },
                { true, true, true }
        };

        PathingStrategy ps = new SingleStepPathingStrategy();
        List<Point> path = ps.computePath(
                new Point(0, 0), new Point(2, 0),
                (p) -> withinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertTrue(path.isEmpty(), "SingleStep should return empty when the direct axis step is blocked");

    }

    @Test
    public void testSingleStep_GoalStraightDown() {
        boolean[][] grid = {
                { true },
                { true },
                { true }
        };
        PathingStrategy ps = new SingleStepPathingStrategy();

        List<Point> path = ps.computePath(
                new Point(0, 0), new Point(0, 2),
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (a, b) -> a.adjacent(b),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(List.of(new Point(0, 1)), path);
    }

    @Test
    public void testSingleStep_DiagonalGoalNoDetour() {
        boolean[][] grid = {
                { true, true, true },
                { true, true, true },
                { true, true, true }
        };
        PathingStrategy ps = new SingleStepPathingStrategy();

        List<Point> path = ps.computePath(
                new Point(0, 0), new Point(2, 2),
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (a, b) -> a.adjacent(b),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(List.of(new Point(0, 1)), path);
    }


    /*
     * Properties of a correct a-star path
     *
     * 1. path length
     * 2. path starts at the start point and ends at the goal
     * 3. path actually contains contiguous nodes
     */

    @Test
    public void testAStarPathProperties() {
        boolean[][] grid = {
                { true, true, true, true },
                { true, true, true, true },
                { true, true, true, true }
        };

        PathingStrategy ps = new AStarPathingStrategy();
        Point start = new Point(0, 0);
        Point end = new Point(3, 2);

        List<Point> path = ps.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (a, b) -> a.adjacent(b),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertTrue(path.size() > 0, "Path should not be empty"); //valid-ish size

        assertTrue(path.get(0).adjacent(start), "First step must be adjacent to start"); //adjacency, checks it starts and ends near the start/end points
        assertTrue(path.get(path.size() - 1).adjacent(end), "Last step must be adjacent to end");

        for (int i = 1; i < path.size(); i++) { //contiguous nodes (says they are continous)
            Point prev = path.get(i - 1);
            Point curr = path.get(i);
            assertTrue(prev.adjacent(curr),
                    "isValidPath returned " + isValidPath(path, 4, new Point(0,0), new Point(3,2)));
        }
    }


    // property based testing
    private static boolean isValidPath(List<Point> path, int expectedLength, Point expectedStart, Point expectedEnd) {

        if (path.size() != expectedLength) {
            System.out.println("Length mismatch: expected " + expectedLength + " but got " + path.size());
            return false;
        }

        if (path.isEmpty()) {
            boolean ok = expectedStart.adjacent(expectedEnd);
            if (!ok) {
                System.out.println("Empty path but start is not adjacent to end.");
            }
            return ok;
        }

        if (!path.get(0).adjacent(expectedStart)) {
            System.out.println("First step " + path.get(0) + " is not adjacent to start " + expectedStart);
            return false;
        }

        if (!path.get(path.size() - 1).adjacent(expectedEnd)) {
            System.out.println("Last step " + path.get(path.size() - 1) + " is not adjacent to end " + expectedEnd);
            return false;
        }

        Point prev = null;
        for (Point x : path) {
            if (prev != null && !prev.adjacent(x)) {
                System.out.println("Path breaks contiguity between " + prev + " and " + x);
                return false;
            }
            prev = x;
        }
        return true;
    }

    private static boolean withinBounds(Point p, boolean[][] grid) {
        return p.y >= 0 && p.y < grid.length &&
                p.x >= 0 && p.x < grid[0].length;
    }
}
