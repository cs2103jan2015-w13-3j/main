package udo.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CustomTextField {
    
    private static final Logger logger = 
            Logger.getLogger(CustomTextField.class.getName());
    
    private TextField textField;
    private Gui gui_;
    
    public CustomTextField(TextField field) {
        textField = field;
        logger.setLevel(Level.INFO);
    }
    
    public void setField(Gui gui) {
        gui_ = gui;
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
    
    public void handleTabKey(KeyEvent event) {
        String userInput = getText();
        String completedStr = gui_.callAutocomplete(userInput);
        setText(completedStr);
        textField.end();
        event.consume();
    }

    public void handleDirectionKey(KeyEvent event, String direction) {
        String command = gui_.callCmdHistory(direction);
        logger.finer(command); //TODO change to finer
        setText(command);
        textField.end();
        event.consume();
        //gui_.displayAlert();
    }
    
    /*
     * Retrieves suggestions for any letter keys
     */
    public void handleOtherKeys(KeyEvent event, KeyCode code) {
        if(code.isLetterKey()) {
            String userInput = getText();
            gui_.callSuggestions(userInput);
            //displayStatus();
            event.consume();
            //logger.info("Suggestion: ");
        } else {
            return;
        }
    }
    
    @FXML
    private void handleReturnKey(ActionEvent event) {
        String text = getText();

        if (gui_.callLogicCommand(text) == true) {
            clear();
        }
    }
    
    public void clear() {
        textField.clear();
    }
}
