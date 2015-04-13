package udo.gui;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;
import udo.storage.Task;
import udo.storage.Task.TaskType;

//@author A0114906J
public class ReminderDialog {
    
    private static final Logger logger = 
            Logger.getLogger(ReminderDialog.class.getName());
    
    private static final String _TITLE = "Reminders";

    private static final double _POS_X = 1050;
    private static final double _POS_Y = 10;
    private static final double _POS_WIDTH = 300;
        
    private static final int _ARR_SIZE = 4;
    
    private static final int _INDEX_TASK_TYPE = 0;
    private static final int _INDEX_TITLE = 1;
    private static final int _INDEX_START = 2;
    private static final int _INDEX_END = 3;
    
    private static final String _BUTTON_TEXT = "Open";

    private static final String _MESSAGE_START = "You have ";
    private static final String _MESSAGE_EVENT_PART_1 = " starting at ";
    private static final String _MESSAGE_EVENT_PART_2 = " until ";
    private static final String _MESSAGE_DEADLINE = " due ";
    
    private Alert _alert;
    private TextFlow _textBox;
    private DialogPane _dialogPane;
    
    /**
     * Message displayed will have the following format
     * Event - you have [content] starting at [xx:xx] until [xx:xx]
     * Deadline - you have [content] due on [xx:xx]
     */
    
    public ReminderDialog(Task task) {
        logger.setLevel(Level.INFO);
        
        _alert = new Alert(AlertType.INFORMATION);
        _dialogPane = _alert.getDialogPane();
        
        customizeDialog(task);
    }

    private void customizeDialog(Task task) {
        removeDefaults();
        setDisplay(task);
        setButton();
    }

    private void removeDefaults(){
        _alert.setGraphic(null);
        _alert.setHeaderText(null);
        _alert.initStyle(StageStyle.UNDECORATED);
        _alert.getButtonTypes().clear();
    }

    private void setDisplay(Task task) {
        _alert.setTitle(_TITLE);
        setLayout();
        String[] taskInfo = getInformation(task);
        setContent(taskInfo);
    }
    
    private void setLayout() {
        _alert.setX(_POS_X);
        _alert.setY(_POS_Y);
        
        _dialogPane.setMaxWidth(_POS_WIDTH);
    }

    private void setContent(String[] taskInfo) {
        _textBox = completeMessage(taskInfo);
        
        _dialogPane.setStyle(GuiUtil.COLOUR_BACKGROUND);              
        _dialogPane.setContent(_textBox);       
    }
    
    private TextFlow completeMessage(String[] taskInfo) {
        String taskType = taskInfo[_INDEX_TASK_TYPE];
        
        if(taskType == TaskType.EVENT.toString()) {
            return completeMessageEvent(taskInfo);
        } else {
            return completeMessageDeadline(taskInfo);
        }
    }
    
    private TextFlow completeMessageEvent(String[] taskInfo) {
        ArrayList<Text> textArr = new ArrayList<>();
        
        textArr.add(new Text(_MESSAGE_START));
        textArr.add(new Text(taskInfo[_INDEX_TITLE]));
        
        textArr.add(new Text(_MESSAGE_EVENT_PART_1));
        textArr.add(new Text(taskInfo[_INDEX_START]));
        
        textArr.add(new Text(_MESSAGE_EVENT_PART_2));
        textArr.add(new Text(taskInfo[_INDEX_END]));  
        
        setTextStyles(textArr);
        return createTextBox(textArr, taskInfo[_INDEX_TASK_TYPE]);
    }
    
    private TextFlow completeMessageDeadline(String[] taskInfo) {
        ArrayList<Text> textArr = new ArrayList<>();
        
        textArr.add(new Text(_MESSAGE_START));
        textArr.add(new Text(taskInfo[_INDEX_TITLE]));
        
        textArr.add(new Text(_MESSAGE_DEADLINE));
        textArr.add(new Text(taskInfo[_INDEX_START]));
        
        setTextStyles(textArr);
        return createTextBox(textArr, taskInfo[_INDEX_TASK_TYPE]);       
    }
    
    private TextFlow createTextBox(ArrayList<Text> textArr, 
                                   String taskType) {
        if(taskType.equals(TaskType.EVENT.toString())) {
            return new TextFlow(textArr.get(0), textArr.get(1),
                                textArr.get(2), textArr.get(3),
                                textArr.get(4), textArr.get(5));
        } else { 
            return new TextFlow(textArr.get(0), textArr.get(1),
                                textArr.get(2), textArr.get(3));
        }
    }

    /**
     * Set styles to individual blocks of text inside the array 
     * 
     * @param textArr
     */
    private void setTextStyles(ArrayList<Text> textArr) {      
        for(int i = 0; i < textArr.size(); i++) {
            if(i%2 == 0) {
                setNormalStyle(textArr.get(i));
            } else {
                setEmphasisedStyle(textArr.get(i));
            }
        }
    }

    private void setNormalStyle(Text title) {
        title.setStyle(GuiUtil.STYLE_FONT + GuiUtil.STYLE_SIZE + 
                       GuiUtil.COLOUR_WHITE);
    }
    
    private void setEmphasisedStyle(Text title) {
        title.setStyle(GuiUtil.STYLE_FONT + GuiUtil.STYLE_SIZE + 
                       GuiUtil.COLOUR_GREEN);
    }
    
    /**
     * 
     * @param task
     * @return An array of string, Array[0] = Task Type
     *                             Array[1] = Task Content
     *                             Array[2] = Task Start Time or Deadline
     *                             Array[3] = Task End Time or empty string
     */
    private String[] getInformation(Task task){
        
        switch(task.getTaskType()) {
            case EVENT :
                return getEventInformation(task);
            case DEADLINE :
                return getDeadlineInformation(task);
            case TODO :
                return getTodoInformation(task);
            default:
                return null;
        }
    }
    
    private String[] getDeadlineInformation(Task task) {
        String[] taskInfo = new String[_ARR_SIZE];
        taskInfo[_INDEX_TASK_TYPE] = task.getTaskType().toString();
        taskInfo[_INDEX_TITLE] = task.getContent();
        taskInfo[_INDEX_START] = GuiUtil.guiTimeFormat(task.getDeadline());
        taskInfo[_INDEX_END] = GuiUtil.STRING_EMPTY;
        
        return taskInfo;
    }

    private String[] getEventInformation(Task task) {
        String[] taskInfo = new String[_ARR_SIZE];
        
        taskInfo[_INDEX_TASK_TYPE] = task.getTaskType().toString();
        taskInfo[_INDEX_TITLE] = task.getContent();
        taskInfo[_INDEX_START] = GuiUtil.guiDateTimeFormat(task.getStart());
        taskInfo[_INDEX_END] = GuiUtil.guiDateTimeFormat(task.getEnd());
        
        return taskInfo;
    }

    private String[] getTodoInformation(Task task) {
        String[] taskInfo = new String[_ARR_SIZE];
        
        taskInfo[_INDEX_TASK_TYPE] = task.getTaskType().toString();
        taskInfo[_INDEX_TITLE] = task.getContent();
        taskInfo[_INDEX_START] = GuiUtil.STRING_EMPTY;
        taskInfo[_INDEX_END] = GuiUtil.STRING_EMPTY;
        
        return taskInfo;
    }
    
    private void setButton() {
        ButtonType openButton = new ButtonType(_BUTTON_TEXT, ButtonData.OK_DONE);
        _alert.getDialogPane().getButtonTypes().addAll(openButton);
        
        final Button button = (Button) _dialogPane.lookupButton(openButton); 
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               
            }
        });
    }
    
    public void appear(){
        _alert.showAndWait();
        logger.fine(String.format(GuiUtil.LOG_INITIATE, "ReminderDialog"));
    }
}
