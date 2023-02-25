package puzzles.jam.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import puzzles.common.Coordinate;
import puzzles.common.Observer;
import puzzles.jam.model.Car;
import puzzles.jam.model.JamModel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * GUI for Jam.
 *
 * @author Ethan Hartman (theeman05)
 */
public class JamGUI extends Application  implements Observer<JamModel, String>  {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "src/puzzles/jam/gui/resources/";

    /** The extension for horizontal cars */
    private static final String HORIZONTAL_CAR_EXTENSION = "-hori.png";

    /** The extension for vertical cars */
    private static final String VERTICAL_CAR_EXTENSION = "-vert.png";

    /** Style to remove padding from a button */
    private static final String NO_PAD_STYLE = "-fx-padding: 0;";

    /** The file directory for jam games */
    private static final File JAM_DATA_DIR = new File(System.getProperty("user.dir") + "/data/jam");

    /** The font of the main UI */
    private static final Font MAIN_UI_FONT = Font.font("SourceSans", FontWeight.BOLD, 13);

    /** The adjustment applied to selected cars */
    private static final ColorAdjust SELECTED_CAR_EFFECT = new ColorAdjust();

    /** Size of icons */
    private final static int ICON_SIZE = 75;

    /** Our jam model object */
    private JamModel model;

    /** The label which will display messages */
    private final Label messageLabel = new Label();

    /** The pane which will hold our cars */
    private final Pane carPane = new Pane();

    /** The button of the last selected car */
    private Button lastSelectedCar;

    /** The current Stage */
    private Stage stage;

    /** The last file loaded */
    private String lastFileLoaded;

    /** Map of the cars we have created so far identified by their resource name */
    private HashMap<String, Button> visualCars;

    public void init() {
        visualCars = new HashMap<>();
        lastFileLoaded = getParameters().getRaw().get(0);
        SELECTED_CAR_EFFECT.setSaturation(1);
        model = new JamModel();
        model.addObserver(this);
        model.loadFromFile(lastFileLoaded);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane topPane = new StackPane();
        BorderPane mainBPain = new BorderPane();
        FlowPane bottomPane = new FlowPane();
        this.stage = stage;

        messageLabel.setFont(MAIN_UI_FONT);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        topPane.getChildren().add(messageLabel);

        mainBPain.setTop(topPane);

        mainBPain.setCenter(carPane);

        Button loadBtn = new Button("Load");
        loadBtn.setFont(MAIN_UI_FONT);
        loadBtn.setOnAction(event -> loadBoardWithChooser(stage));

        Button resetBtn = new Button("Reset");
        resetBtn.setFont(MAIN_UI_FONT);
        resetBtn.setOnAction(event -> model.loadFromFile(lastFileLoaded));

        Button hintBtn = new Button("Hint");
        hintBtn.setFont(MAIN_UI_FONT);
        hintBtn.setOnAction(event -> model.announceHint());

        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.getChildren().addAll(loadBtn, resetBtn, hintBtn);

        mainBPain.setBottom(bottomPane);

        resizeStage();

        stage.setTitle("Jam GUI");
        stage.setScene(new Scene(mainBPain));
        stage.show();
    }

    @Override
    public void update(JamModel jamModel, String message) {
        messageLabel.setText(message);
        if (message.equals(JamModel.HINT_PREFIX) || message.equals(JamModel.LOADED) || message.contains(JamModel.SHIFT_PREFIX)) {
            if (message.equals(JamModel.LOADED))
                refreshBoardVisuals();
            else
                addCarBtn(jamModel.getLastMoved());
        }
    }

    /**
     * Loads a graphic from the given resource name.
     * @param resourceName name of the resource in resources to make an image of.
     * @return an image view of the graphic or null, resource not found.
     */
    private ImageView loadGraphicFromResources(String resourceName){
        ImageView view = null;

        try(InputStream is = new FileInputStream(RESOURCES_DIR + resourceName)) {
            view = new ImageView(new Image(is));
        }catch (IOException e){
            System.out.println("File could not be found");
        }
        return view;
    }

    /**
     * Creates a button with an image inside based on the given resource name.
     * @param resourceName name of the resource in resources to make an image of.
     * @return a button with an image inside
     */
    private Button buttonFromResources(String resourceName){
        Button previouslyMade = visualCars.get(resourceName);
        if (previouslyMade == null) {
            Button btn = new Button();
            btn.setStyle(NO_PAD_STYLE);
            btn.setGraphic(loadGraphicFromResources(resourceName));
            return btn;
        }
        carPane.getChildren().remove(previouslyMade);
        return previouslyMade;
    }

    /**
     * Creates and adds an active car button to the GUI.
     * @param car to add to the GUI.
     */
    private void addCarBtn(Car car) {
        String resourceName = car.getID() + (car.isHorizontal() ? HORIZONTAL_CAR_EXTENSION : VERTICAL_CAR_EXTENSION);
        Button carBtn = buttonFromResources(resourceName);

        carBtn.setLayoutX(car.getBackCoordinate().getColumn() * ICON_SIZE);
        carBtn.setLayoutY(car.getBackCoordinate().getRow() * ICON_SIZE);
        carBtn.setOnAction(event-> {
            if (lastSelectedCar != null)
                lastSelectedCar.setEffect(null);
            if (model.selectCarAt(car.getBackCoordinate())) {
                carBtn.setEffect(SELECTED_CAR_EFFECT);
                lastSelectedCar = carBtn;
            }
        });
        visualCars.put(resourceName, carBtn);
        carPane.getChildren().add(carBtn);
    }

    /**
     * Creates and adds an active empty button to the GUI.
     * @param at location to add button.
     */
    private void addEmptyButton(Coordinate at){
        Button emptyBtn = new Button();
        emptyBtn.setPrefSize(ICON_SIZE, ICON_SIZE);
        emptyBtn.setLayoutX(at.getColumn() * ICON_SIZE);
        emptyBtn.setLayoutY(at.getRow() * ICON_SIZE);
        emptyBtn.setOnAction(event-> model.moveSelected(at));
        carPane.getChildren().add(emptyBtn);
    }

    /** Resizes the stage so it fits the board */
    private void resizeStage(){
        // These values are somewhat random. The stage did not resize correctly for some reason
        if (stage != null) {
            stage.setMinWidth(ICON_SIZE * model.getDimensions().getColumn() + 15);
            stage.setMinHeight(ICON_SIZE * model.getDimensions().getRow() + 85);
            stage.setMaxWidth(stage.getMinWidth());
            stage.setMaxHeight(stage.getMinHeight());
        }
    }

    /** Refresh the visuals on our board completely */
    private void refreshBoardVisuals(){
        Coordinate dimensions = model.getDimensions();
        HashSet<String> createdCars = new HashSet<>();
        carPane.getChildren().clear();
        if (lastSelectedCar != null) lastSelectedCar.setEffect(null);

        // Add our empty buttons
        for (int row = 0; row < dimensions.getRow(); row++)
            for(int col = 0; col < dimensions.getColumn(); col++)
                addEmptyButton(new Coordinate(row, col));

        for (int row = 0; row < dimensions.getRow(); row++)
            for(int col = 0; col < dimensions.getColumn(); col++){
                Car car = model.getCarAt(new Coordinate(row, col));
                if (car != null && createdCars.add(car.getID()))
                    addCarBtn(car);
            }
        resizeStage();
    }

    /**
     * Prompts the file chooser so the user can load a new board
     * @param currentStage stage in use for the UI.
     */
    public void loadBoardWithChooser(Stage currentStage){
        //create a new FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a game board.");
        // Set initial directory to the boards folder
        fileChooser.setInitialDirectory(JAM_DATA_DIR);
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        //open up a window for the user to interact with and load the file.
        File chosen = fileChooser.showOpenDialog(currentStage);
        if (chosen != null)
            model.loadFromFile(lastFileLoaded = chosen.getAbsolutePath());
    }

    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Usage: java JamGUI filename");
        else
            Application.launch(args);
    }
}
