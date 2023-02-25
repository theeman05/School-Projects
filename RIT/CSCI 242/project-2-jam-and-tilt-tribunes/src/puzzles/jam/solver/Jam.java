package puzzles.jam.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.jam.model.JamConfig;

import java.io.IOException;
import java.util.List;

/**
 * Test Program to determine the steps to solve a Jam game.
 *
 * @author Ethan Hartman (theeman05)
 */
public class Jam {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Jam filename");
        }else{
            Solver solver = new Solver();
            JamConfig config;
            try {
                config = new JamConfig(args[0]);
                List<Configuration> path = solver.buildPathBFS(config);
                System.out.println(config);
                System.out.println("Total configs: " + solver.getLastTotalConfigs());
                System.out.println("Unique configs: " + solver.getLastUniqueConfigs());

                if (path != null){
                    for (int i = 0; i< path.size(); i++) {
                        if (i != 0)
                            System.out.println();
                        System.out.println("Step " + i + ":\n" + path.get(i));
                    }
                }else System.out.println("No solution");
            }catch (IOException e){
                System.out.println("There was an issue reading the file '"+args[0]+"'.");
            }
        }
    }
}