package udo.gui;

import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import udo.storage.Task;
import udo.storage.Task.TaskType;

public class ReminderDialog {
    
    private static final String TITLE = "Reminders";

    private static final double POS_X = 1050;
    private static final double POS_Y = 10;
    private static final double POS_WIDTH = 300;
    
    private static final String STYLE_COLOUR_BACKGROUND = "-fx-background-color: #2b3339; ";
    private static final String STYLE_COLOUR_WHITE = "-fx-fill: white; ";
    private static final String STYLE_COLOUR_GREEN = "-fx-fill: #1abc9c; ";
    private static final String STYLE_SIZE = "-fx-font-size: 12.5px; ";
    private static final String STYLE_FONT = "-fx-font-family: 'Open Sans', sans-serif; ";
    
    private static final int ARR_SIZE = 4;
    
    private static final int INDEX_TASK_TYPE = 0;
    private static final int INDEX_TITLE = 1;
    private static final int INDEX_START = 2;
    private static final int INDEX_END = 3;
    
    private static final String MESSAGE_START = "You have ";
    private static final String MESSAGE_EVENT_PART_1 = " starting at ";
    private static final String MESSAGE_EVENT_PART_2 = " until ";
    private static final String MESSAGE_DEADLINE = " due on ";
    
    /**
     * you have [content] starting at [abc] until [def]
     * you have [content] due on [abc]
     */
    private Alert alert;
    private TextFlow textBox;
    private DialogPane dialogPane;
    
    public ReminderDialog(Task task) {
        alert = new Alert(AlertType.INFORMATION);
        dialogPane = alert.getDialogPane();
        
        customizeDialog(task);
    }

    private void customizeDialog(Task task) {
        removeDefaults();
        setDisplay(task);
        setButton();
    }

    private void setDisplay(Task task) {
        alert.setTitle(TITLE);
        setLayout();
        String[] taskInfo = getInformation(task);
        setContent(taskInfo);
    }
    
    private void setLayout() {
        alert.setX(POS_X);
        alert.setY(POS_Y);
        dialogPane.setMaxWidth(POS_WIDTH);
    }

    private void setContent(String[] taskInfo) {
        textBox = completeMessage(taskInfo);       
        dialogPane.setStyle(STYLE_COLOUR_BACKGROUND);              
        dialogPane.setContent(textBox);       
    }
    
    //TODO settle todo
    private TextFlow completeMessage(String[] taskInfo) {
        String taskType = taskInfo[INDEX_TASK_TYPE];
        
        if(taskType == TaskType.EVENT.toString()) {
            return completeMessageEvent(taskInfo);
        } else {
            return completeMessageDeadline(taskInfo);
        }
    }
    
    private TextFlow completeMessageEvent(String[] taskInfo) {
        ArrayList<Text> textArr = new ArrayList<>();
        textArr.add(new Text(MESSAGE_START));
        textArr.add(new Text(taskInfo[INDEX_TITLE]));
        
        textArr.add(new Text(MESSAGE_EVENT_PART_1));
        textArr.add(new Text(taskInfo[INDEX_START]));
        
        textArr.add(new Text(MESSAGE_EVENT_PART_2));
        textArr.add(new Text(taskInfo[INDEX_END]));  
        
        createTextStyles(textArr);
        return createTextBox(textArr, taskInfo[INDEX_TASK_TYPE]);
    }
    
    private TextFlow completeMessageDeadline(String[] taskInfo) {
        ArrayList<Text> textArr = new ArrayList<>();
        textArr.add(new Text(MESSAGE_START));
        textArr.add(new Text(taskInfo[INDEX_TITLE]));
        
        textArr.add(new Text(MESSAGE_DEADLINE));
        textArr.add(new Text(taskInfo[INDEX_START]));
        
        createTextStyles(textArr);
        return createTextBox(textArr, taskInfo[INDEX_TASK_TYPE]);       
    }
    
    private TextFlow createTextBox(ArrayList<Text> textArr, String taskType) {
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
    private void createTextStyles(ArrayList<Text> textArr) {      
        for(int i = 0; i < textArr.size(); i++) {
            if(i%2 == 0) {
                setNormalStyle(textArr.get(i));
            } else {
                setEmphasisedStyle(textArr.get(i));
            }
        }
    }

    private void setNormalStyle(Text title) {
        title.setStyle(STYLE_FONT + STYLE_SIZE + STYLE_COLOUR_WHITE);
        return;
    }
    
    private void setEmphasisedStyle(Text title) {
        title.setStyle(STYLE_FONT + STYLE_SIZE + STYLE_COLOUR_GREEN);
        return;
    }
    
    private String[] getInformation(Task task){                
        switch(task.getTaskType()) {
            case EVENT :
                return getEventInformation(task);
            case DEADLINE :
                return getDeadlineInformation(task);
            case TODO :
                //Fallthrough //TODO
            default:
                return null;
        }
    }
    
    private String[] getDeadlineInformation(Task task) {
        String[] taskInfo = new String[ARR_SIZE];
        taskInfo[INDEX_TASK_TYPE] = task.getTaskType().toString();
        taskInfo[INDEX_TITLE] = task.getContent();
        taskInfo[INDEX_START] = GuiUtil.guiTimeFormat(task.getDeadline());
        taskInfo[INDEX_END] = GuiUtil.EMPTY_STRING;
        
        return taskInfo;
    }

    private String[] getEventInformation(Task task) {
        String[] taskInfo = new String[ARR_SIZE];
        taskInfo[INDEX_TASK_TYPE] = task.getTaskType().toString();
        taskInfo[INDEX_TITLE] = task.getContent();
        taskInfo[INDEX_START] = GuiUtil.guiDateTimeFormat(task.getStart());
        taskInfo[INDEX_END] = GuiUtil.guiDateTimeFormat(task.getEnd());
        
        return taskInfo;
    }

    private void removeDefaults(){
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.getButtonTypes().clear();
    }
    
    private void setButton() {
        ButtonType openButton = new ButtonType("Open", ButtonData.OK_DONE);
        alert.getDialogPane().getButtonTypes().addAll(openButton);
    }
    
    public void appear(){
        alert.showAndWait();
    }
}
