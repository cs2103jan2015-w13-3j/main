package udo.logic.command;

import udo.util.Config.CommandName;

public class ChdirCommand extends Command {
    public ChdirCommand() {
        super();
        setCommandName(CommandName.CHDIR);
    }
}
