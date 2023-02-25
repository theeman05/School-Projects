package puzzles.tilt.gui;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.tilt.model.TiltModel;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * This is a GUI implementation for the game called tilt.
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
public class TiltGUI extends Application implements Observer<TiltModel, String> {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    //directional and model components
    /** a blocker cell */
    private final static char BLOCKER = '*';
    /** a green slider */
    private final static char GREEN = 'G';
    /** a blue slider */
    private final static char BLUE = 'B';
    /** the goal */
    private final static char GOAL = 'O';

    private final static String UP = "n";
    private final static String RIGHT = "e";
    private final static String DOWN = "s";
    private final static String LEFT = "w";

    //different sizes for GUI components
    private final static int UP_DOWN_BUTTON_SIZE = 550;
    private final static int LEFT_RIGHT_BUTTON_SIZE = 500;
    private final static int CENTER_GRID_SIZE = 500;
    private final static int GAME_WIDTH = 600;
    private final static int GAME_HEIGHT = 650;
    private final static int SPACE_BUFFER_TOP_PANE = 15;
    private final static int TOO_MUCH_TEXT_LENGTH = 40;

    //current file
    private static String currentFile;

    //main model
    private TiltModel model;

    //pane components
    private final GridPane centerPane = new GridPane();
    private final Text observerText = new Text("Welcome!");

    //images
    private Image greenDisk;
    private Image blueDisk;
    private Image wall;
    private Image goal;

    /**
     * Adds LightsOutModel as the model for this GUI and sets initial parameters.
     *
     * @exception IOException if there is an error creating the model.
     */
    public void init() throws IOException {
        //get the initial filename
        String filename = getParameters().getRaw().get(0);
        currentFile = filename;

        //set up model
        model = new TiltModel(filename);
        model.addObserver(this);

        //set up initial font for "Welcome!" observerText
        observerText.setFont(Font.font("Verdana", 15));
    }

    /**
     * The start() method defines the GUI containing the game Tilt
     *
     * @param stage container in which the UI will be rendered.
     * @exception Exception if there is an error starting the game.
     * @see Application#start
     */
    @Override
    public void start(Stage stage) throws Exception {

        //initialize panes
        BorderPane mainPane = new BorderPane();
        VBox topPane = new VBox();
        HBox centerBox = new HBox();
        FlowPane rightPane = new FlowPane();
        FlowPane leftPane = new FlowPane();
        FlowPane bottomPane = new FlowPane();

        //align panes and set sizes and spaces
        stage.setHeight(GAME_HEIGHT);
        stage.setWidth(GAME_WIDTH);
        stage.setResizable(false);
        topPane.setAlignment(Pos.CENTER);
        topPane.setSpacing(SPACE_BUFFER_TOP_PANE);
        rightPane.setAlignment(Pos.CENTER);
        bottomPane.setAlignment(Pos.CENTER);
        leftPane.setAlignment(Pos.CENTER);

        //movement buttons
        Button up = new Button("^");
        Button right = new Button(">");
        Button down = new Button("⌄");
        Button left = new Button("<");
        up.setMinWidth(UP_DOWN_BUTTON_SIZE);
        right.setMinHeight(LEFT_RIGHT_BUTTON_SIZE);
        down.setMinWidth(UP_DOWN_BUTTON_SIZE);
        left.setMinHeight(LEFT_RIGHT_BUTTON_SIZE);
        up.setOnMouseClicked(event -> makeMove(UP));
        right.setOnMouseClicked(event -> makeMove(RIGHT));
        down.setOnMouseClicked(event -> makeMove(DOWN));
        left.setOnMouseClicked(event -> makeMove(LEFT));

        //bottom button components
        Button reset = new Button("Reset");
        Button loadGame = new Button("Load Game");
        Button hint = new Button("Hint");
        reset.setOnMouseClicked(event -> {
            try {
                reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        loadGame.setOnMouseClicked(event -> {
            try {
                loadGame(stage);
                refreshBoard();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        hint.setOnMouseClicked(event -> getHint());

        //add configurations to panes
        topPane.getChildren().addAll(observerText, up);
        bottomPane.getChildren().addAll(down, reset, loadGame, hint);
        rightPane.getChildren().addAll(right);
        leftPane.getChildren().addAll(left);
        centerBox.getChildren().addAll(leftPane, centerPane, rightPane);

        //set up the main pane
        mainPane.setTop(topPane);
        mainPane.setBottom(bottomPane);
        mainPane.setCenter(centerBox);

        //set stage and launch
        refreshBoard();
        stage.setScene(new Scene(mainPane));
        stage.setTitle("Tilt");
        stage.show();
    }

    /**
     * Updates game model with the correct next move.
     */
    private void getHint(){
        model.getHint();
        refreshBoard();
    }

    /**
     * Reloads current game file to the beginning.
     *
     * @exception IOException if the file could not be loaded.
     */
    private void reset() throws IOException {
        model.loadFromFile(currentFile);
        refreshBoard();
    }

    /**
     * Updates game model with chosen next move.
     */
    private void makeMove(String direction){
        model.makeMove(direction);
        refreshBoard();
    }

    /**
     * Refreshes the center grid pane with new information by reading in the models
     * current board. Sets image size to the needed size based on board size.
     */
    private void refreshBoard(){
        int gridComponentSize = CENTER_GRID_SIZE/model.getSize();
        setImageSize(gridComponentSize);

        //center components
        for (int row = 0; row < model.getSize(); row++) {
            for (int col = 0; col < model.getSize(); col++) {
                Button button = new Button();
                button.setMinHeight(gridComponentSize);
                button.setMaxHeight(gridComponentSize);
                button.setMinWidth(gridComponentSize);
                button.setMaxWidth(gridComponentSize);

                //pick what image to put based on model
                switch (model.getVal(row, col)) {
                    case BLOCKER -> button.setGraphic(new ImageView(wall));
                    case GREEN -> button.setGraphic(new ImageView(greenDisk));
                    case BLUE -> button.setGraphic(new ImageView(blueDisk));
                    case GOAL -> button.setGraphic(new ImageView(goal));
                }
                centerPane.add(button, col, row);
            }
        }

    }

    /**
     * Sets images to a specified square size.
     *
     * @param size the size to the set the images to.
     */
    private void setImageSize(int size){
        greenDisk = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "green.png")), size, size, false, false);
        blueDisk = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "blue.png")), size, size, false, false);
        wall = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "block.png")), size, size, false, false);
        goal = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "hole.png")), size, size, false, false);
    }

    /**
     * Load a game from your computer files onto the GUI.
     *
     * @param stage the stage where the GUI is
     * @exception IOException if the game could not be loaded.
     */
    public void loadGame(Stage stage) throws IOException {
        //find and choose file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a game board.");
        File selectedFile = fileChooser.showOpenDialog(stage);

        //ensure user selects file
        if (selectedFile != null) {
            String file = selectedFile.toString();
            currentFile = file;
            model.loadFromFile(file);

            //clear current pane in case of different size board
            centerPane.getChildren().clear();
            refreshBoard();
        }
    }

    /**
     * Every time something happens update observerText. If text is too long reduce the font.
     *
     * @param tiltModel model for the GUI.
     * @param message message returned from model.
     */
    @Override
    public void update(TiltModel tiltModel, String message) {
        //reduce size of text if file path too long to fit
        if(message.length() > TOO_MUCH_TEXT_LENGTH){
            observerText.setFont(Font.font("Verdana", 8));
        }else{

            observerText.setFont(Font.font("Verdana", 15));
        }
        observerText.setText(message);
    }

    /**
     * The main runs the JavaFX application.
     *
     * @param args ignored
     * @see Application#launch
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltGUI filename");
        } else {
            Application.launch(args);
        }
    }
}
