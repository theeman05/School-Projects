package puzzles.strings;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.List;

/**
 * Main class for the strings puzzle.
 *
 * @author Ethan Hartman (theeman05)
 */
public class Strings {
    /**
     * Run an instance of the strings puzzle.
     *
     * @param args [0]: the starting string;
     *             [1]: the finish string.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            Solver solver = new Solver();
            List<Configuration> path = solver.buildPathBFS(new StringsConfig(args[0], args[1]));
            System.out.println("Start: "+ args[0] + ", End: " + args[1]);
            System.out.println("Total configs: " + solver.getLastTotalConfigs());
            System.out.println("Unique configs: " + solver.getLastUniqueConfigs());

            if (path != null){
                for (int i = 0; i< path.size(); i++)
                    System.out.println("Step " + i + ": " + ((StringsConfig) path.get(i)).getStart());
            }else System.out.println("No solution");
        }
    }
}
