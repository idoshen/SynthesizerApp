package com.idoshen.synth;

import com.idoshen.synth.utils.BeatTimeManager;
import com.idoshen.synth.utils.Utils;

public class BeatTimeManagerThread extends Thread {
    public BeatTimeManager beatTimeManager;
    private volatile boolean closed;

    public BeatTimeManagerThread(Synthesizer synthesizer) {
        this.beatTimeManager = new BeatTimeManager(synthesizer);
        start();
    }

    @Override
    public void run() {
        while (!closed) {
//            System.out.println("BeatThread");
            while (!beatTimeManager.running) {
                Utils.handleProcedure(this::wait, false); // while not running, the thread is waiting. It is not a busy waiting.
            }
            beatTimeManager.updateBeatAndBar();
            beatTimeManager.updateLabelParameters(); // Update UI labels
        }
    }

    void close() {
        beatTimeManager.running = true;
        closed = true;
    }
}


