package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.utils.Colors;

import java.util.Scanner;

/**
 * Class InputManager gets all the inputs from the command line and pass it to the CLI function that request them, the class
 * implements the Runnable interface; therefore, the run() method is run by a separated thread that constantly get an input from
 * the command line. If the input is permitted (this happens if a CLI function request an input) the input is memorized as the last
 * input (and nothing else is read until the input is passed to the function that request it). If the input is not permitted it prints
 * a message that notifies that the input inserted was ignored.
 *
 * This class follows a standard producer/consumer thread pattern (a thread produce inputs and some other threads will
 * read those inputs).
 */
public class InputManager implements Runnable{
    private String lastInput = null;
    private boolean inputPermitted = false;
    private boolean active = true;
    private final Scanner sc;
    private final Object getInputLock = new Object();

    /**
     * Construct an InputManager which will read inputs from the specified scanner
     * @param sc the scanner on which inputs will be read
     */
    public InputManager(Scanner sc) {
        this.sc = sc;
    }

    @Override
    public void run() {
        while(isActive()) {
            synchronized (getInputLock) {
                while (lastInput != null) {
                    try {
                        getInputLock.wait();
                    } catch (InterruptedException e) {
                        //TODO
                    }
                }
                String input = sc.nextLine();
                if (isInputPermitted()) {
                    lastInput = input.split("\n")[0];
                    getInputLock.notify();
                }
                else System.out.println(Colors.RED + "You are not permitted to insert an input at this moment, " +
                        "your last input will be ignored" + Colors.RESET);
            }
        }
    }

    /**
     * Return the last input that was read from the scanner. If there are no inputs read (because the last one was already
     * consumed), the method wait until a new input will be read.
     * @return the last input line
     */
    public String getLastInput() {
        boolean inputFound = false;
        String newInput = null;
        while (!inputFound) {
            try {
                synchronized (getInputLock) {
                    while (lastInput == null) getInputLock.wait();
                    newInput = lastInput;
                    lastInput = null;
                    getInputLock.notify();
                    inputFound = true;
                }
            } catch (InterruptedException e) {
                inputFound = false;
            }
        }
        return newInput;
    }

    public synchronized boolean isInputPermitted() {
        return inputPermitted;
    }

    public synchronized void setInputPermitted(boolean inputPermitted) {
        this.inputPermitted = inputPermitted;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Reset the previous input, this is used when a function discover that the input wasn't for him
     * @param lastInput the input to be restored as the last input
     */
    public void restoreLastInput(String lastInput) {
        synchronized (getInputLock) {
            this.lastInput = lastInput;
        }
    }
}
