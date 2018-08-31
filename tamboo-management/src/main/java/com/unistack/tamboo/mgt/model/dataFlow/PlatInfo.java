package com.unistack.tamboo.mgt.model.dataFlow;

public class PlatInfo {

    private int countWf;
    private int running;
    private int warning;
    private int unrunning;


    public PlatInfo(int countWf, int running, int warning, int unrunning) {
        this.countWf=countWf;
        this.running=running;
        this.warning=warning;
        this.unrunning=unrunning;
    }
    public PlatInfo() {
    }

    public int getCountWf() {
        return countWf;
    }

    public void setCountWf(int countWf) {
        this.countWf = countWf;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public int getUnrunning() {
        return unrunning;
    }

    public void setUnrunning(int unrunning) {
        this.unrunning = unrunning;
    }
}
