package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.utils.Colors;

import java.util.Scanner;

public class InputManager implements Runnable{
    private String lastInput = null;
    private boolean inputPermitted = false;
    private boolean active = true;
    private final Scanner sc;

    private final Object getInputLock = new Object();

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
}
