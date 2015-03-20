package udo.logic.command;

import udo.util.Config;
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

        if (getOption(Config.OPT_PRIO) != null) {
            gui.display(storage.query(true));
        } else if (getOption(Config.OPT_FREE) != null) {
            gui.display(storage.findFreeSlots());
        } else {
            gui.display(storage.query());
        }

        setStatus(getDisplaySucessStatus());
        return true;
    }

    private String getDisplaySucessStatus() {
        return "";
    }
}
