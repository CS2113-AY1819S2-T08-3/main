package planmysem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import planmysem.logic.LogicManager;
import planmysem.storage.Storage;
import planmysem.storage.StorageFile;
import planmysem.ui.Stoppable;
import planmysem.ui.UiManager;

/**
 * Main entry point to the application.
 */
public class Main extends Application implements Stoppable {

    /**
     * Version info of the program.
     */
    public static final String VERSION = "PlanMySem - Version 1.3";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Storage storageFile = new StorageFile();
        UiManager uiManager = new UiManager(new LogicManager(storageFile), VERSION);
        uiManager.start(primaryStage, this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }
}


