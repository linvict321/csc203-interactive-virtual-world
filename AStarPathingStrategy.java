
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{

    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        Map<Point, Integer> fScore = new HashMap<>();
        PriorityQueue<Point> open = new PriorityQueue<>(
                Comparator.<Point>comparingInt(p -> fScore.getOrDefault(p, Integer.MAX_VALUE))
                        .thenComparingInt(p -> hScore(p, end))
                        .thenComparingInt(p -> p.x)
                        .thenComparingInt(p -> p.y)
        ); //Initialize a priority queue (the to-do list). This is often called the "open list".
        Set<Point> closed = new HashSet<>(); //Initialize a set to keep track of visited nodes. This is often called the "closed set" or "closed list".


        Map<Point, Point> begin = new HashMap<>();
        Map<Point, Integer> gScore = new HashMap<>(); // number of steps taken to get from the start node to the current node
        //Add the start node to the priority queue, with a g-value of 0 and an h-value computed using the heuristic function, i.e., manhattan(start, goal).
        open.add(start);
        gScore.put(start, 0);
        fScore.put(start, hScore(start, end));

        Point curr = start;

        while(!open.isEmpty()){ //While the priority queue is not empty
            //Remove the node with the lowest f-value(g + h) from the priority queue. This is the current node.
            int minF = Integer.MAX_VALUE;
            for(Point fPoints: open){
                int f = fScore.getOrDefault(fPoints, Integer.MAX_VALUE);
                if(f < minF){
                    minF = f;
                    curr = fPoints;
                }
            }

            if(closed.contains(curr)){ continue;} //**

            //If the current node is the goal node, the search is done. Think about how you would reconstruct the path from start to goal at this point.
            if(withinReach.test(curr, end)){
                return reconstructPath(begin, curr, start, end);
            }

            //Mark the current node as visited (i.e., add it to the closed list).
            closed.add(curr);
            open.remove(curr);

            //For each unvisited neighbour of the current node:
            //Compute its g-value as current.g + 1.
            final Point finalCurr = curr;
            potentialNeighbors.apply(curr)
                    .filter(canPassThrough).filter(neighbor -> !closed.contains(neighbor))
                    .forEach(neighbor -> {
                        int tentativeG = gScore.getOrDefault(finalCurr, Integer.MAX_VALUE) + 1;
                        int best = gScore.getOrDefault(neighbor, Integer.MAX_VALUE);
                        if(tentativeG < best){
                            begin.put(neighbor, finalCurr);
                            gScore.put(neighbor, tentativeG);
                            fScore.put(neighbor, tentativeG + hScore(neighbor, end));

                            open.remove(neighbor);
                            open.add(neighbor);
                        }
                    }); //filters canpass and if closed contains neighbor
            //Compute its h-value using the heuristic function, i.e., manhattan(neighbour, goal).
            //Check if the neighbour is already in the priority queue. (This can happen if you reach a neighbour multiple times via different paths.)
            //If the neighbour is not already in the priority queue, add it with its computed g and h values.
            //If the neighbour is already in the priority queue, but the new g-value is lower than its existing g-value, update its g and h values in the priority queue. (In practice, this will require removing and re-adding the node to the priority queue to maintain the correct ordering.)
        }
        return Collections.emptyList(); //no path
    }

    private static List<Point> reconstructPath(Map<Point,Point> begin, Point goal, Point start, Point end) {
        LinkedList<Point> path = new LinkedList<>();
        while(begin.containsKey(goal)){ //if we have curr point
            path.addFirst(goal); //want to stack it ish
            goal = begin.get(goal);
        }

        // exclude start if present
        if (!path.isEmpty() && path.getFirst().equals(start)) {
            path.removeFirst();
        }
        // exclude end if present
        if (!path.isEmpty() && path.getLast().equals(end)) {
            path.removeLast();
        }
        return path;
    }


    private static int hScore(Point a, Point b) {
        // Manhattan distance
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
