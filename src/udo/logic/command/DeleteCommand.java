package udo.logic.command;

import udo.util.Config;

public class DeleteCommand extends Command {
    public DeleteCommand() {
        super();
        setCommandName(Config.CommandName.DELETE);
    }

    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
