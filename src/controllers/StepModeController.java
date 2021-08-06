package controllers;

import functional.Condition;
import functional.Statistic;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class StepModeController {
    
    @FXML
    public Scene stepModePage;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public AnchorPane diagramPane;
    @FXML
    public Button finishButton;
    @FXML
    public Button nextButton;
    
    private List<Condition> conditions;
    private int lastConditionIndex = -1;
    private static final int LINE_DISTANCE = 40;
    private static final int LETTER_POS = 12;
    private static final int TACT_HEIGHT = (int) (LINE_DISTANCE * 0.75);
    private static final int TIME_TO_DISTANCE_FACTOR = 100;
    private static final int WIDTH = 900;
    
    private double prevTime;
    private double rightTime;
    
    
    public void sendStatistic(Statistic statistic) {
        this.conditions = new ArrayList<>(statistic.fillConditions());
        signDiagram();
        nextButtonClick();
    }
    
    private void signDiagram() {
        for (int i = 0; i < Condition.getSourceCount(); i++) {
            sign(0, i * LINE_DISTANCE + LETTER_POS, "И" + i);
        }
        for (int i = 0; i < Condition.getBufferSize(); i++) {
            sign(0, (i + Condition.getSourceCount()) * LINE_DISTANCE + LETTER_POS, "Б" + i);
        }
        for (int i = 0; i < Condition.getDeviceCount(); i++) {
            sign(0, (i + Condition.getSourceCount() + Condition.getBufferSize()) * LINE_DISTANCE + LETTER_POS, "П" + i);
        }
        sign(0, (Condition.getSourceCount() + Condition.getDeviceCount() + Condition.getBufferSize()) * LINE_DISTANCE + LETTER_POS, "ОТК");
    }
    
    private void drawLines() {
        int linesCount = Condition.getSourceCount() + Condition.getDeviceCount() + Condition.getBufferSize() + 1;
        if (prevTime == 0 || rightTime * TIME_TO_DISTANCE_FACTOR > WIDTH) {
            Line nextLine = new Line(prevTime * TIME_TO_DISTANCE_FACTOR, LINE_DISTANCE, Math.max(WIDTH, rightTime * TIME_TO_DISTANCE_FACTOR), LINE_DISTANCE);
            for (int i = 0; i < linesCount; i++) {
                diagramPane.getChildren().add(nextLine);
                nextLine = new Line(nextLine.getStartX(), nextLine.getStartY() + LINE_DISTANCE,
                        nextLine.getEndX(), nextLine.getEndY() + LINE_DISTANCE);
            }
        }
    }
    
    @FXML
    public void nextButtonClick() {
        Condition lastCondition = conditions.get(++lastConditionIndex);
        prevTime = rightTime;
        rightTime = lastCondition.getTime();
        drawLines();
        drawCondition(lastCondition);
        scrollPane.setHvalue(1);
        if (lastConditionIndex == conditions.size() - 1) {
            nextButton.setDisable(true);
            finishButton.setDisable(true);
        }
    }
    
    private void drawCondition(Condition condition) {
        boolean[] bufferConditions = condition.getBuffersCondition();
        boolean[] deviceConditions = condition.getDevicesCondition();
        int timeToDistance = (int) (TIME_TO_DISTANCE_FACTOR * condition.getTime());
        
        if (condition.getSrcId() != null) {
            Line line = new Line(timeToDistance, (condition.getSrcId() + 1) * LINE_DISTANCE,
                    timeToDistance, (condition.getSrcId() + 1) * LINE_DISTANCE - TACT_HEIGHT);
            diagramPane.getChildren().add(line);
            int y1 = (condition.getSrcId() + 1) * LINE_DISTANCE;
            sign(timeToDistance, y1 - 15, condition.getReqUniqueNumber());
            if (condition.getBufferId() != null) {
                drawDashLineAndSign(timeToDistance, y1,
                        (Condition.getSourceCount() + condition.getBufferId() + 1) * LINE_DISTANCE,
                        condition.getReqUniqueNumber());
            } else if (condition.getDeviceId() != null) {
                drawDashLineAndSign(timeToDistance, y1,
                        (Condition.getSourceCount() + Condition.getBufferSize() + condition.getDeviceId() + 1) * LINE_DISTANCE,
                        condition.getReqUniqueNumber());
            } else if (condition.isFailure()) {
                drawDashLineAndSign(timeToDistance, y1,
                        (Condition.getSourceCount() + Condition.getBufferSize() + Condition.getDeviceCount() + 1) * LINE_DISTANCE,
                        condition.getReqUniqueNumber());
            }
        }
        
        
        int bufferShift = Condition.getSourceCount() + 1;
        for (int i = 0; i < Condition.getBufferSize(); i++) {
            if (bufferConditions[i] || condition.getBufferId() != null) {
                if (condition.getBufferId() != null && condition.getBufferId() == i) {
                    Line line = new Line(timeToDistance, (bufferShift + i) * LINE_DISTANCE,
                            timeToDistance, (bufferShift + i) * LINE_DISTANCE - TACT_HEIGHT);
                    diagramPane.getChildren().add(line);
                    if (condition.getDeviceId() != null) {
                        drawDashLineAndSign(timeToDistance, (Condition.getSourceCount() + condition.getBufferId() + 1) * LINE_DISTANCE,
                                (Condition.getSourceCount() + Condition.getBufferSize() + condition.getDeviceId() + 1) * LINE_DISTANCE,
                                condition.getReqUniqueNumber());
                    }
                }
                if (lastConditionIndex != 0) {
                    Condition prevCondition = conditions.get(lastConditionIndex - 1);
                    if (prevCondition.getBuffersCondition()[i]) {
                        int prevTimeToDistance = (int) (TIME_TO_DISTANCE_FACTOR * prevCondition.getTime());
                        Line line = new Line(prevTimeToDistance, (bufferShift + i) * LINE_DISTANCE - TACT_HEIGHT,
                                timeToDistance, (bufferShift + i) * LINE_DISTANCE - TACT_HEIGHT);
                        diagramPane.getChildren().add(line);
                    }
                }
            }
        }
        
        int deviceShift = bufferShift + Condition.getBufferSize();
        for (int i = 0; i < Condition.getDeviceCount(); i++) {
            if (deviceConditions[i] || condition.getDeviceId() != null) {
                if (condition.getDeviceId() != null && condition.getDeviceId() == i) {
                    Line line = new Line(timeToDistance, (deviceShift + i) * LINE_DISTANCE,
                            timeToDistance, (deviceShift + i) * LINE_DISTANCE - TACT_HEIGHT);
                    diagramPane.getChildren().add(line);
                }
                if (lastConditionIndex != 0) {
                    Condition prevCondition = conditions.get(lastConditionIndex - 1);
                    if (prevCondition.getDevicesCondition()[i]) {
                        int prevTimeToDistance = (int) (TIME_TO_DISTANCE_FACTOR * prevCondition.getTime());
                        Line line = new Line(prevTimeToDistance, (deviceShift + i) * LINE_DISTANCE - TACT_HEIGHT,
                                timeToDistance, (deviceShift + i) * LINE_DISTANCE - TACT_HEIGHT);
                        diagramPane.getChildren().add(line);
                    }
                }
            }
        }
        int failureShift = deviceShift + 1;
        if (condition.isFailure()) {
            Line line = new Line(timeToDistance, (failureShift + 1) * LINE_DISTANCE,
                    timeToDistance, (failureShift + 1) * LINE_DISTANCE - TACT_HEIGHT);
            diagramPane.getChildren().add(line);
        }
        
        Stage stage = (Stage) stepModePage.getWindow();
        stage.show();
    }
    
    private void drawDashLineAndSign(int x, int y1, int y2, String reqNumber) {
        Line dashLine = new Line(x, y1, x, y2);
        dashLine.getStrokeDashArray().addAll(2d, 2d);
        dashLine.setStroke(Color.INDIANRED);
        diagramPane.getChildren().add(dashLine);
        sign(x, y2 - 15, reqNumber);
    }
    
    private void sign(int x, int y, String signStr) {
        Label label = new Label(signStr);
        label.setLayoutX(x);
        label.setLayoutY(y);
        diagramPane.getChildren().add(label);
    }
    
    @FXML
    public void finishButtonClick() {
        while (lastConditionIndex != conditions.size() - 1) {
            nextButtonClick();
        }
        scrollPane.setHvalue(1);
    }
}

