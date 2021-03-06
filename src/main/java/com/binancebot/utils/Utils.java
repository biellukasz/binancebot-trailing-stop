package com.binancebot.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

@Component
public class Utils {

    public void addLogLine(TextArea logbox, String newLine){
        logbox.setText(logbox.getText()+"\n"+newLine);
        logbox.appendText("");

    }
    public void updateLabelText(Label label,String text){
        Platform.runLater(() -> label.setText(text));
    }
    public void clear(TextArea logbox){
        logbox.clear();
    }
}
