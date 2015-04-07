package udo.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

//@author A0114906J

/**
 * This class customises the input field of the graphical user interface
 * It handle events like up, down, tab keys.
 *
 */
public class CustomTextField {
    
    private static final Logger logger = 
            Logger.getLogger(CustomTextField.class.getName());
    
    private TextField _textField;
    private HomeController _controller;
    
    public CustomTextField(TextField field, HomeController controller) {
        _textField = field;
        _controller = controller;
        logger.setLevel(Level.INFO);
    }
    
    public void setText(String str) {
        _textField.setText(str);
    }
    
    public String getText() {
        return _textField.getText();
    }
    
    public void bindKeys(EventHandler<KeyEvent> keyHandlers) {
        _textField.setOnKeyPressed(keyHandlers);
    }
    
    public void handleReturnKey() {
        String text = getText();
    
        if (_controller.getCommand(text) == true) {
            clear();
        }
    }

    public void handleTabKey(KeyEvent event) {
        String userInput = getText();
        String completedStr = _controller.getAutocompleted(userInput);
        setText(completedStr);
        _textField.end();
        event.consume();
    }

    public void handleDirectionKey(KeyEvent event, String direction) {
        String command = _controller.getCmdHistory(direction);
        logger.finer(command); 
        setText(command);
        _textField.end();
        event.consume();
    }

    public void clear() {
        _textField.clear();
    }
}
