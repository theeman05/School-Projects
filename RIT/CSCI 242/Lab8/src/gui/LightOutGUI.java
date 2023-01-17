package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LightsOutModel;
import model.Observer;

import java.io.File;

/**
 * The GUI view portion of our lights out game.
 *
 * @author Ethan Hartman (theeman05)
 */
public class LightOutGUI extends Application implements Observer<LightsOutModel, String> {
    /**
     * The css style for when the light is on
     */
    private static final String LIGHT_ON_STYLE = "-fx-background-color: white; -fx-border-color: grey";

    /**
     * The css style for when the light is off
     */
    private static final String LIGHT_OFF_STYLE = "-fx-background-color: #1c1c1c; -fx-border-color: grey";

    /**
     * The css style for when the light is the hint
     */
    private static final String LIGHT_HINT_STYLE = "-fx-background-color: #ffb752; -fx-border-color: grey";

    /**
     * The size of a light button
     */
    private static final int LIGHT_BUTTON_DIMENSION = 100;

    /**
     * The beginning index the x starts at within a hint.
     */
    private static final int HINT_X_START_INDEX = 6;

    /**
     * Our LightsOutModel for the GUI
     */
    private LightsOutModel model;

    /**
     *  The changeable "view" on the top which displays how many moves the user has made, or a message to the user.
     */
    private Label movesDisplay, messageDisplay;

    /**
     *  Our visible array of light buttons
     */
    private Button [][] lightButtons;

    /**
     * Allows the user to toggle lights
     */
    private boolean gameOn;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        model = new LightsOutModel();
        model.addObserver(this);
        lightButtons = new Button[model.getDimension()][model.getDimension()];
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create our layouts
        BorderPane mainBPain = new BorderPane();
        GridPane lightsGrid = new GridPane();
        FlowPane topPane = new FlowPane();
        FlowPane bottomPane = new FlowPane();

        // Set preferred width of our BorderPane
        mainBPain.setPrefWidth(model.getDimension() * LIGHT_BUTTON_DIMENSION);

        // Create top messages and view components
        Label movesLabel = new Label("Moves: ");
        movesDisplay = new Label("0");
        Label messageLabel = new Label(" Message: ");
        messageDisplay = new Label("Start a game first.");
        topPane.getChildren().addAll(movesLabel, movesDisplay, messageLabel, messageDisplay);
        mainBPain.setTop(topPane);

        // Create bottom buttons
        Button newGameBtn = new Button("New Game");
        Button loadGameBtn = new Button("Load Game");
        Button hintBtn = new Button("Hint");

        newGameBtn.setOnAction(event -> {
            model.generateRandomBoard();
            gameOn = true;
        });
        loadGameBtn.setOnAction(event -> loadBoardWithChooser(stage));
        hintBtn.setOnAction(event -> model.getHint());

        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.getChildren().addAll(newGameBtn, loadGameBtn, hintBtn);
        mainBPain.setBottom(bottomPane);

        // Create center light grid buttons
        for (int x = 0; x < model.getDimension(); x++)
            for (int y = 0; y < model.getDimension(); y++){
                Button lightBtn = new Button();

                int finalX = x, finalY = y;
                lightBtn.setOnAction(event -> {
                    if (gameOn) model.toggleTile(finalX,finalY);
                });

                lightBtn.setStyle(LIGHT_ON_STYLE);
                lightBtn.setPrefSize(LIGHT_BUTTON_DIMENSION, LIGHT_BUTTON_DIMENSION);
                lightsGrid.add(lightBtn, x, y);
                lightButtons[y][x] = lightBtn;
            }

        lightsGrid.setAlignment(Pos.CENTER);
        mainBPain.setCenter(lightsGrid);
        stage.setTitle("Lights Out");
        stage.setScene(new Scene(mainBPain));
        stage.show();
    }

    /**
     * The observed subject calls this method on each observer that has
     * previously registered with it. This version of the design pattern
     * follows the "push model" in that the subject can provide
     * ClientData to inform the observer about what exactly has happened.
     * But this convention is not required. It may still be necessary for
     * the observer to also query the subject to find out more about what has
     * happened. If this is a simple signal with no data attached,
     * or if it can safely be assumed that the observer already has a
     * reference to the subject, even the subject parameter may be null.
     * But as always this would have to be agreed to by designers of both sides.
     *
     * @param model  the object that wishes to inform this object
     *           about something that has happened.
     * @param msg optional data the server.model can send to the observer
     * @see <a href="https://sourcemaking.com/design_patterns/observer">the
     * Observer design pattern</a>
     */
    @Override
    public void update(LightsOutModel model, String msg) {
        if (msg.equals(LightsOutModel.LOADED)){ // game is loaded successfully
            messageDisplay.setText("Game Loaded");
            refreshBoardVisuals();
            return;
        }else if (msg.equals(LightsOutModel.LOAD_FAILED)){ //Game failed to load
            messageDisplay.setText("Error Loading Game");
            return;
        } else if (msg.startsWith(LightsOutModel.HINT_PREFIX)) { //Model is reporting a  hint
            String[] splitHint = msg.split(", ");
            messageDisplay.setText(msg);
            lightButtons[Integer.parseInt(splitHint[1])][Integer.parseInt(splitHint[0].substring(HINT_X_START_INDEX))]
                    .setStyle(LIGHT_HINT_STYLE);
            //don't display board
            return;
        }

        if (model.gameOver()) { //checks if game is over.
            refreshBoardVisuals();
            messageDisplay.setText("You win. Good for you.");
            gameOn = false;
            return;
        }
        refreshBoardVisuals(); // renders the board
        messageDisplay.setText(msg);
    }

    /**
     * Refresh the board by re-rendering the colors and updating the number of moves
     */
    public void refreshBoardVisuals(){
        movesDisplay.setText(Integer.toString(model.getMoves()));
        for (int x = 0; x < model.getDimension(); x++)
            for (int y = 0; y < model.getDimension(); y++)
                lightButtons[y][x].setStyle(model.getTile(x,y).isOn() ? LIGHT_ON_STYLE : LIGHT_OFF_STYLE);
    }

    /**
     * Prompts the file chooser so the user may load a board.
     */
    public void loadBoardWithChooser(Stage currentStage){
        //create a new FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a game board.");
        // Set initial directory to the boards folder
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"/boards"));
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Text Files", "*.lob"));
        //open up a window for the user to interact with and load the file.
        File chosen = fileChooser.showOpenDialog(currentStage);
        if (chosen != null) {
            model.loadBoardFromFile(chosen);
            gameOn = true;
        }
    }
}
