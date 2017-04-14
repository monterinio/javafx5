package pl.pwr.measurement.controller;

import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import pl.pwr.measurement.data.ConnectionData;
import pl.pwr.measurement.data.Data;
import pl.pwr.measurement.data.Strings;
import pl.pwr.measurement.util.ConnectionUtil;
import pl.pwr.measurement.util.SaveLoadUtil;
import pl.pwr.measurement.util.WindowUtil;

public class MainController implements Initializable {

    private Data data;
    private ConnectionData connectionData;

    @FXML
    private MenuItem connectionSettingsItem;
    @FXML
    private MenuItem closeItem;
    @FXML
    private MenuItem aboutItem;
    @FXML
    private Button startMeasurement;
    @FXML
    private Button stopMeasurement;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Slider speedSlider;
    @FXML
    private Slider clampSlider;
    @FXML
    private Label speedMonitor;
    @FXML
    private Label clampMonitor;

    public MainController() {
        data = new Data();
        connectionData = SaveLoadUtil.loadApplicationState(Strings.FILE_NAME);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSliders();
        initializeMonitors();
        configureSliders();
        configureMenuItems();
        initializeButtons();
        startMeasurementButton();
        stopMeasurementButton();
    }

    private void initializeSliders() {
        speedSlider.setDisable(true);
        clampSlider.setDisable(true);
    }

    private void resetSliders() {
        speedSlider.setValue(0);
        clampSlider.setValue(0);
    }

    private void configureSliders() {
        speedSlider.valueProperty().addListener((o, oldVal, newVal) -> {
            data.setSpeed(newVal.doubleValue());
            data.setTime(LocalTime.now());
            ConnectionUtil.run(connectionData, data);
        });

        clampSlider.valueProperty().addListener((o, oldVal, newVal) -> {
            data.setClamp(newVal.doubleValue());
            data.setTime(LocalTime.now());
            ConnectionUtil.run(connectionData, data);
        });
    }

    private void initializeMonitors() {
        speedMonitor.setDisable(true);
        speedMonitor.textProperty().bind(speedSlider.valueProperty().asString());
        clampMonitor.textProperty().bind(clampSlider.valueProperty().asString());
    }

    private void configureMenuItems() {
        connectionSettingsItem.setOnAction(x -> WindowUtil.loadWindowAndSendData(Strings.CONNECTION_LAYOUT_NAME,
                Strings.CONNECTION_SETTINGS_ITEM_NAME, connectionData));
        aboutItem.setOnAction(x -> WindowUtil.loadWindow(Strings.ABOUT_LAYOUT_NAME, Strings.ABOUT_ITEM_NAME));
        closeItem.setOnAction(x -> WindowUtil.loadWindow(Strings.EXIT_LAYOUT_NAME, Strings.EXIT_ITEM_NAME));
    }

    private void initializeButtons() {
        startMeasurementButton();
        stopMeasurementButton();
    }

    private void startMeasurementButton() {
        startMeasurement.setOnAction(x -> {
            progressBar.progressProperty().bind(startMeasurementDevicesService.progressProperty());
            progressIndicator.progressProperty().bind(startMeasurementDevicesService.progressProperty());
            resetSliders();
            startMeasurementDevicesService.start();

            startMeasurementDevicesService.setOnSucceeded(e -> {
                speedSlider.setDisable(startMeasurementDevicesService.getValue());
                clampSlider.setDisable(startMeasurementDevicesService.getValue());
            });
        });
    }

    private void stopMeasurementButton() {
        stopMeasurement.setOnAction(x-> {
            startMeasurementDevicesService.cancel();
            startMeasurementDevicesService.reset();

            speedSlider.setDisable(true);
            clampSlider.setDisable(true);
        });
    }

    //klasa anonimowa
    Service<Boolean> startMeasurementDevicesService = new Service<Boolean>() {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    double max = 100;
                    for (int i = 0; i <= max; i++) {
                        if(isCancelled()) {
                            break;
                        }
                        updateProgress(i, max);
                        Thread.sleep(1);
                    }
                    return false;
                }
            };
        }
    };

    public ConnectionData getConnectionData() {
        return connectionData;
    }
}
