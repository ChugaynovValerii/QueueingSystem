package controllers;

import functional.DeviceInfo;
import functional.SourceInfo;
import functional.Statistic;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class AutoModeController {
    @FXML
    public Scene autoModePage;
    
    @FXML
    public TableColumn<DeviceInfo, Integer> deviceId;
    @FXML
    public TableColumn<DeviceInfo, Double> deviceLoadFactor;
    @FXML
    public TableView<DeviceInfo> deviceTable;
    
    @FXML
    public TableView<SourceInfo> srcTable;
    @FXML
    public TableColumn<SourceInfo, Integer> srcId;
    @FXML
    public TableColumn<SourceInfo, Integer> srcCount;
    @FXML
    public TableColumn<SourceInfo, Double> srcFailProb;
    @FXML
    public TableColumn<SourceInfo, Double> srcAverageTimeInSystem;
    @FXML
    public TableColumn<SourceInfo, Double> srcAverageTimeInBuffer;
    @FXML
    public TableColumn<SourceInfo, Double> srcAverageTimeInDevice;
    @FXML
    public TableColumn<SourceInfo, Double> srcDispersionBufferTime;
    @FXML
    public TableColumn<SourceInfo, Double> srcDispersionServiceTime;
    
    private final ObservableList<SourceInfo> sourcesInfo = FXCollections.observableArrayList();
    private final ObservableList<DeviceInfo> devicesInfo = FXCollections.observableArrayList();
    
    private static final double DEFAULT_DOUBLE_PLACES = 4;
    
    private static double round(double value) {
        long factor = (long) Math.pow(10, DEFAULT_DOUBLE_PLACES);
        value *= factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    public void sendStatistic(Statistic statistic) {
        sourcesInfo.addAll(statistic.getSourcesStat());
        devicesInfo.addAll(statistic.getDeviceStat());
        
        srcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        srcCount.setCellValueFactory(new PropertyValueFactory<>("count"));
//        srcFailProb.setCellValueFactory(new PropertyValueFactory<>("failureProb"));
//        srcAverageTimeInSystem.setCellValueFactory(new PropertyValueFactory<>("averageTimeInSystem"));
//        srcAverageTimeInBuffer.setCellValueFactory(new PropertyValueFactory<>("averageTimeInBuffer"));
//        srcAverageTimeInDevice.setCellValueFactory(new PropertyValueFactory<>("averageTimeInDevice"));
//        srcDispersionServiceTime.setCellValueFactory(new PropertyValueFactory<>("dispersionServiceTime"));
//        srcDispersionBufferTime.setCellValueFactory(new PropertyValueFactory<>("dispersionBufferTime"));
        srcFailProb.setCellValueFactory(dataCell
                -> new SimpleDoubleProperty(round(dataCell.getValue().getFailureProb())).asObject());
        srcAverageTimeInSystem.setCellValueFactory(dataCell
                -> new SimpleDoubleProperty(round(dataCell.getValue().getAverageTimeInSystem())).asObject());
        srcAverageTimeInBuffer.setCellValueFactory(dataCell
                -> new SimpleDoubleProperty(round(dataCell.getValue().getAverageTimeInBuffer())).asObject());
        srcAverageTimeInDevice.setCellValueFactory(dataCell
                -> new SimpleDoubleProperty(round(dataCell.getValue().getAverageTimeInDevice())).asObject());
        srcDispersionServiceTime.setCellValueFactory(dataCell
                -> new SimpleDoubleProperty(round(dataCell.getValue().getDispersionServiceTime())).asObject());
        srcDispersionBufferTime.setCellValueFactory(dataCell
                -> new SimpleDoubleProperty(round(dataCell.getValue().getDispersionBufferTime())).asObject());
        
        deviceId.setCellValueFactory(new PropertyValueFactory<>("id"));
        deviceLoadFactor.setCellValueFactory(dataCell
                -> new SimpleDoubleProperty(round(dataCell.getValue().getLoadFactor())).asObject());
        
        srcTable.setItems(sourcesInfo);
        deviceTable.setItems(devicesInfo);
    }
}
