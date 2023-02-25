package puzzles.tilt.solver;
import puzzles.tilt.model.*;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.IOException;
import java.util.List;

/**
 * Test class for tilt puzzle.
 * The rules for tilt:
 * Tilt is a sliding logic maze puzzle.
 * The board gets populated with gray blockers that do not move and green and blue sliders.
 * You can only move the sliders by tilting the board up, down, left or right.
 * When you do that, all the sliders will move all the way across the board in that direction until they reach the end of the board or a blocker.
 * Moving them just half way is not allowed. The goal is to get all the green sliders to fall through the hole while
 * all the blue ones remain on the board. This video gives an explanation of how the puzzle is played.
 *
 * @author Yaroslav Khalitov
 */
public class Tilt {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Tilt filename");
        }else {
            try {
                TiltConfig myConfig = new TiltConfig(args[0]);
                Solver solver = new Solver();
                List<Configuration> path = solver.buildPathBFS(myConfig);

                System.out.println(path.get(0).toString());
                System.out.println("Total configs: " + solver.getLastTotalConfigs());
                System.out.println("Unique configs: " + solver.getLastUniqueConfigs());

                if (path != null) {
                    for (int i = 0; i < path.size(); i++)
                        System.out.println("Step " + i + ": \n" + path.get(i).toString());
                } else System.out.println("No solution");

            } catch (IOException e) {
                System.out.println("Could not read thy file");
                throw new RuntimeException(e);
            }
        }




    }
}
