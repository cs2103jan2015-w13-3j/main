package udo.logic;

public class Logic {
    private InputParser parser;
    
    public Logic() {
        parser = new InputParser();
        /* TODO:
         * Initialize Storage
         * Initialize and start up passive thread for reminder
         */
    }

    public void executeCommand(String command) {
        System.out.println(command); 
    }
}
