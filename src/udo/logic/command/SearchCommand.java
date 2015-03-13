package udo.logic.command;

import udo.util.Config.CommandName;

public class SearchCommand extends Command {
    public SearchCommand() {
        super();
        setCommandName(CommandName.SEARCH);
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
        
        return true;
    }
}
