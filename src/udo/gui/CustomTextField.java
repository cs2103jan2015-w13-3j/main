package udo.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class CustomTextField {
    
    private static final Logger logger = 
            Logger.getLogger(CustomTextField.class.getName());
    
    private TextField textField;
    private HomeController controller_;
    
    public CustomTextField(TextField field, HomeController controller) {
        textField = field;
        controller_ = controller;
        logger.setLevel(Level.INFO);
    }
    
    public void setText(String str) {
        textField.setText(str);
    }
    
    public String getText() {
        return textField.getText();
    }
    
    public void bindKeys(EventHandler<KeyEvent> keyHandlers) {
        textField.setOnKeyPressed(keyHandlers);
    }
    
    public void handleReturnKey() {
        String text = getText();
    
        if (controller_.getCommand(text) == true) {
            clear();
        }
    }

    public void handleTabKey(KeyEvent event) {
        String userInput = getText();
        String completedStr = controller_.getAutocompleted(userInput);
        setText(completedStr);
        textField.end();
        event.consume();
    }

    public void handleDirectionKey(KeyEvent event, String direction) {
        String command = controller_.getCmdHistory(direction);
        logger.finer(command); 
        setText(command);
        textField.end();
        event.consume();
    }

    public void clear() {
        textField.clear();
    }
}
