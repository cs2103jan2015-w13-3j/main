package udo.logic.command;

import udo.util.Config.CommandName;

public class DoneCommand extends Command {
    public DoneCommand() {
        super();
        setCommandName(CommandName.DONE);
    }
    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
