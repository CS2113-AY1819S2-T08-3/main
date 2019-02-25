package planmysem.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import planmysem.Main;
import planmysem.logic.Logic;
import planmysem.logic.LogicP;

/**
 * The GUI of the App
 */
public class Gui {

    /**
     * Offset required to convert between 1-indexing and 0-indexing.
     */
    public static final int DISPLAYED_INDEX_OFFSET = 1;

    public static final int INITIAL_WINDOW_WIDTH = 800;
    public static final int INITIAL_WINDOW_HEIGHT = 600;
    private final Logic logic;
    private final LogicP logicP;

    private MainWindow mainWindow;
    private MainWindowP mainWindowP;
    private String version;

    public Gui(Logic logic, LogicP logicP, String version) {
        this.logicP = logicP;
        this.logic = logic;
        this.version = version;
    }

    /**
     * TODO: Add Javadoc comment.
     */
    public void start(Stage stage, Stoppable mainApp) throws IOException {
        mainWindow = createMainWindow(stage, mainApp);
        mainWindow.displayWelcomeMessage(version + " old Address Book", logic.getStorageFilePath());

        Stage stage2 = new Stage();
        mainWindowP = createMainWindowP(stage2, mainApp);
        mainWindowP.displayWelcomeMessage(version, logicP.getStorageFilePath());
    }

    /**
     * TODO: Add Javadoc comment.
     */
    private MainWindow createMainWindow(Stage stage, Stoppable mainApp) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        /* Note: When calling getResource(), use '/', instead of File.separator or '\\'
         * More info: http://docs.oracle.com/javase/8/docs/technotes/guides/lang/resources.html#res_name_context
         */
        loader.setLocation(Main.class.getResource("ui/mainwindow.fxml"));

        stage.setTitle(version);
        stage.setScene(new Scene(loader.load(), INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT));
        stage.show();
        MainWindow mainWindow = loader.getController();
        mainWindow.setLogic(logic);
        mainWindow.setMainApp(mainApp);
        return mainWindow;
    }

    /**
     * TODO: Add Javadoc comment.
     */
    private MainWindowP createMainWindowP(Stage stage, Stoppable mainApp) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("ui/mainwindowP.fxml"));

        stage.setTitle(version);
        stage.setScene(new Scene(loader.load(), INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT));
        stage.show();
        MainWindowP mainWindowP = null;
        mainWindowP = loader.getController();
        mainWindowP.setLogic(logicP);
        mainWindowP.setMainApp(mainApp);
        return mainWindowP;
    }
}
