package udo.logic.command;

import udo.util.Config.CommandName;

public class ModifyCommand extends Command {
    public ModifyCommand() {
        super();
        setCommandName(CommandName.MODIFY);
    }
    @Override
    protected boolean parseArg(String arg) {
        return parseIndexContentPair(arg);
    }
}
