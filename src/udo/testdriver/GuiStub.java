package udo.testdriver;

import java.util.List;

import udo.gui.Gui;
import udo.storage.Task;

public class GuiStub extends Gui {
    public void displayStatus(String status) {};
    public void display(List<Task> tasks) {};
}
