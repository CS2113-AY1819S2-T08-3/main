package planmysem.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import planmysem.Main;
import planmysem.logic.LogicManager;

/**
 * The GUI of the App
 */
public class UiManager implements Ui {

    public static final int INITIAL_WINDOW_WIDTH = 1100;
    public static final int INITIAL_WINDOW_HEIGHT = 600;
    private final LogicManager logicManager;

    private MainWindow mainWindow;
    private String version;

    public UiManager(LogicManager logicManager, String version) {
        this.logicManager = logicManager;
        this.version = version;
    }

    @Override
    public void start(Stage stage, Stoppable mainApp) throws IOException {
        mainWindow = createMainWindowP(stage, mainApp);
        mainWindow.displayWelcomeMessage(version, logicManager.getStorageFilePath());
    }

    /**
     * TODO: Add Javadoc comment.
     */
    private MainWindow createMainWindowP(Stage stage, Stoppable mainApp) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("ui/mainwindow.fxml"));

        stage.setTitle(version);
        stage.setScene(new Scene(loader.load(), INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT));
        stage.show();
        mainWindow = loader.getController();
        mainWindow.setLogicManager(logicManager);
        mainWindow.setMainApp(mainApp);
        return mainWindow;
    }
}
