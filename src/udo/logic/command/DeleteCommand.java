package udo.logic.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.emory.mathcs.backport.java.util.Collections;
import udo.logic.InputParser;
import udo.logic.Logic;
import udo.storage.Task;
import udo.util.Config;

public class DeleteCommand extends Command {
    private static final String STATUS_DELETED = "Task: %s deleted sucessfully";
    private static final String STATUS_ALL_DELETED = "All tasks deleted";
    
    private static final String ERR_INVALID_RANGE = "invalid index range";
    
    private Integer[] indices;
    private static final String INDEX_RANGE_MARKER = "-";
    
    private static final Logger log = Logger.getLogger(DeleteCommand.class.getName());

    public DeleteCommand() {
        super();
        setCommandName(Config.CommandName.DELETE);
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

        assert(indices != null && indices.length > 0);

        Task deletedTask = null; 
        if (indices.length == 1) {
            deletedTask = storage.query(getStorageIndex(indices[0]));
        }

        boolean isSuccessful = deleteTasks();

        if (isSuccessful) {
            setStatus(getDeleteSucessStatus(deletedTask));
            gui.display(storage.query());
        }        

        updateGUIStatus();
        return isSuccessful;
    }

    /**
     * Delete all tasks with index in the array indices
     * @param isSuccessful
     * @return
     */
    private boolean deleteTasks() {
        List<Integer> storageIndices = mapIndexArray();
        if (storageIndices == null) {
            return false;
        }
        
        Collections.sort(storageIndices);
        Collections.reverse(storageIndices);
        
        log.log(Level.INFO, "Deleting task " + storageIndices.toString(),
                storageIndices);

        for (Integer i : storageIndices) {
            if (!storage.delete(i)) {
                setStatus(Logic.formatErrorStr(Logic.ERR_STORAGE));
                return false;
            }
        }

        return true;
    }

    /**
     * Map an array of displayed indices to storage indices
     * @return
     */
    private List<Integer> mapIndexArray() {
        List<Integer> storageIndices = new ArrayList<>();

        for (int i : indices) {
            Integer index = getStorageIndex(i);
            if (index == null) {
                setStatus(Logic.formatErrorStr(Logic.ERR_INVALID_INDEX));
                return null;
            }

            storageIndices.add(index);
        }
        return storageIndices;
    }

    private String getDeleteSucessStatus(Task task) {
        if (task != null) {
            return String.format(STATUS_DELETED,
                                 Logic.summarizeContent(task.getContent()));
        } else {
            return STATUS_ALL_DELETED;
        }
    }

    @Override
    protected boolean parseArg(String arg) {
        Set<Integer> indices = new HashSet<>();
        String[] indicesStr = arg.split("\\s*(\\s|,)\\s*");

        for (String s : indicesStr) {
            if (s.contains(INDEX_RANGE_MARKER)) {
                if (!getIndexRange(s, indices)) {
                    return false;
                }
            } else {
                try {
                    indices.add(Integer.parseInt(s));
                } catch (NumberFormatException e){
                    setStatus(InputParser.ERR_INVALID_INT_FORMAT);
                    log.log(Level.FINE, getStatus());
                    return false;
                }
            }
        }
        
        this.indices = indices.toArray(new Integer[indices.size()]);
        
        return true;
    }

    /**
     * Add all indices specified by the range syntax 'from-to' to the set indices
     * @param s the range string
     * @param indices
     * @return true if the range is valid or false otherwise
     */
    private boolean getIndexRange(String s, Set<Integer> indices) {
        String[] range = s.split(INDEX_RANGE_MARKER);
        
        if (range.length != 2) {
            setStatus(ERR_INVALID_RANGE);
            return false;
        }
        
        try {
            int from = Integer.parseInt(range[0]);
            int to = Integer.parseInt(range[1]);
            
            for (int i = from; i <= to; i++) {
                indices.add(i);
            }
        } catch (NumberFormatException e) {
            setStatus(InputParser.ERR_INVALID_INT_FORMAT);
            log.log(Level.FINE, getStatus());
            return false;
        }
        
        return true;
    }
}
