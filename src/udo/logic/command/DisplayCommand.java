package udo.logic.command;

import udo.util.Config.CommandName;

public class DisplayCommand extends Command {
    public DisplayCommand() {
        super();
        setCommandName(CommandName.DISPLAY);
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    @Override
    public boolean execute() {
        if (!super.execute()) {
            return false;
        }

        status = getDisplaySucessStatus();
        gui.display(storage.query());
        return true;
    }

    private String getDisplaySucessStatus() {
        return "";
    }
}
