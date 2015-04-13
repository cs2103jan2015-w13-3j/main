package udo.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
        logger.setLevel(Level.INFO);
        
        _textField = field;
        _controller = controller;
    }
    
    public void setText(String str) {
        assert(str != null);
        _textField.setText(str);
    }
    
    public String getText() {
        return _textField.getText();
    }
    
    public String getChangedText(KeyCode code) {        
        String input = getText();
        
        if (code.isLetterKey()) {
            return concatLetter(input, code);
        } else if (code.equals(KeyCode.BACK_SPACE)) {
            return removeLetter(input);
        } else {
            return GuiUtil.STRING_EMPTY;
        }
    }
    
    private String concatLetter(String input, KeyCode code) {
        return input + retrieveLetter(code);
    }

    private String retrieveLetter(KeyCode code) {
        return code.getName().toLowerCase();
    }

    private String removeLetter(String input) {
        assert(input != null);
        
        if(input.length() > 0) {
            return input.substring(0, input.length() - 1 );
        } else {
            return GuiUtil.STRING_EMPTY;
        }
    }

    public void bindKeys(EventHandler<KeyEvent> keyHandlers) {
        _textField.setOnKeyPressed(keyHandlers);
    }
    
    public void handleReturnKey() {
        String text = getText();
    
        if (_controller.getCommand(text) == true) {
            clear();
        }
        logger.finest(text);
    }

    public void handleTabKey(KeyEvent event) {
        String userInput = getText();
        String completedStr = _controller.getAutocompleted(userInput);
        setText(completedStr);
        _textField.end();
        event.consume();
        
        logger.finest(completedStr);
    }

    public void handleDirectionKey(KeyEvent event, String direction) {
        String command = _controller.getCmdHistory(direction);
        logger.finer(command); 
        setText(command);
        _textField.end();
        event.consume();
        
        logger.finest(command);
    }

    public void clear() {
        _textField.clear();
    }
}
