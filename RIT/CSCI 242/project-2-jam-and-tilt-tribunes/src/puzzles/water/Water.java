package puzzles.water;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.List;

/**
 * Main class for the water buckets puzzle.
 *
 * @author Ethan Hartman (theeman05)
 */
public class Water {
    /**
     * Run an instance of the water buckets puzzle.
     *
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Water amount bucket1 bucket2 ...");
        } else {
            List<Integer> buckets = new java.util.ArrayList<>();
            Solver solver = new Solver();
            for (int i = 1; i < args.length; i++) buckets.add(Integer.valueOf(args[i]));

            List<Configuration> path = solver.buildPathBFS(new WaterConfig(Integer.parseInt(args[0]), buckets));

            System.out.println("Amount: "+ args[0] + ", Buckets: " + buckets);
            System.out.println("Total configs: " + solver.getLastTotalConfigs());
            System.out.println("Unique configs: " + solver.getLastUniqueConfigs());

            if (path != null){
                for (int i = 0; i< path.size(); i++)
                    System.out.println("Step " + i + ": " + ((WaterConfig) path.get(i)).getBucketFills());
            }else System.out.println("No solution");
        }
    }
}
