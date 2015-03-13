package udo.logic.command;

import udo.util.Config.CommandName;

public class UndoCommand extends Command {
    public UndoCommand() {
        super();
        setCommandName(CommandName.UNDO);
    }
}