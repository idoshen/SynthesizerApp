package com.idoshen.synth.utils;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.audioFX.Arpeggiator;

import javax.swing.*;

public class BeatTimeManager extends SynthControlContainer {

    private long startTime;  // Time when the time manager is started
    private int currentBeat = 1;  // Current beat count
    private int currentBar = 1;  // Current bar count
    private final int MaxBPMValue = 10000; //  - Max 1000 BPM
    private final int MaxBPBValue = 32; // - Max 32 BPB

    private final int MinBPMValue = 1; //  - Min 0.1 BPM
    private final int MinBPBValue = 1; // - Min 1 BPB

    private final RefWrapper<Integer> beatsPerMinute = new RefWrapper<>(1000); // Beats per minute (BPM)
    private final RefWrapper<Integer> beatsPerBar = new RefWrapper<>(4); // Beats per bar
    private final RefWrapper<Integer> State = new RefWrapper<>(0); // Beats per bar

    private final JLabel currentBarLabel = new JLabel("Current Bar: 1");
    private final JLabel currentBeatLabel = new JLabel("Current Beat: 1");
    public boolean running = false;

    public BeatTimeManager(Synthesizer synthesizer) {
        super(synthesizer);
        resetParameters();
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(310, 420, 310, 100);
        setSize(310, 100);
        setLayout(null);

        JLabel header = new JLabel("Time:");
        header.setBounds(10, 5, 100, 25);
        add(header);

        JLabel BPMText = new JLabel("BPM");
        BPMText.setBounds(15, 40, 75, 25);
        add(BPMText);
        JLabel BPMParameter = new JLabel(" 100.0");
        BPMParameter.setBounds(10, 65, 50, 25);
        BPMParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(BPMParameter, this, MinBPMValue, MaxBPMValue, 1, beatsPerMinute, () -> {
            BPMParameter.setText(String.format(" %.1f", getBPMValue()));
            resetParameters();
        });
        add(BPMParameter);

        JLabel BPBText = new JLabel("Beats Per Bar:");
        BPBText.setBounds(75, 40, 100, 25);
        add(BPBText);
        JLabel BPBParameter = new JLabel(" 4");
        BPBParameter.setBounds(70, 65, 50, 25);
        BPBParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(BPBParameter, this, MinBPBValue, MaxBPBValue, 1, beatsPerBar, () -> {
            BPBParameter.setText(String.format(" %d",getBPBValue()));
            resetParameters();
        });
        add(BPBParameter);

        currentBarLabel.setBounds(180, 5, 100, 25);
        add(currentBarLabel);

        currentBeatLabel.setBounds(180, 30, 100, 25);
        add(currentBeatLabel);

        JButton BTMStartButton = new JButton("Start");
        BTMStartButton.setFocusable(false);
        BTMStartButton.setLocation(150,70);
        BTMStartButton.setSize(70,20);
        BTMStartButton.addActionListener(e -> {
            resetParameters();
            running = true;
        });

        JButton BTMStopButton = new JButton("Stop");
        BTMStopButton.setFocusable(false);
        BTMStopButton.setLocation(220,70);
        BTMStopButton.setSize(70, 20);
        BTMStopButton.addActionListener(e -> {
            resetParameters();
            updateLabelParameters();
            running = false;
        });

        add(BTMStartButton);
        add(BTMStopButton);
    }

    private int getStateParameter() {
        return State.val;
    }

    private void resetParameters() {
        this.startTime = System.nanoTime();
        this.currentBar = 1;
        this.currentBeat = 1;
    }

    private double getBPMValue() { return beatsPerMinute.val / 10.0d; }
    private int getBPBValue() { return beatsPerBar.val; }

    /**
     * Get the elapsed time in seconds since the time manager started.
     *
     * @return Elapsed time in seconds.
     */
    public double getElapsedTime() {
        long currentTime = System.nanoTime();
        return (currentTime - startTime) / 1e9;  // Convert nanoseconds to seconds
    }

    /**
     * Get the current beat count.
     *
     * @return Current beat count.
     */
    public int getCurrentBeat() {
        return currentBeat;
    }

    /**
     * Get the current bar count.
     *
     * @return Current bar count.
     */
    public int getCurrentBar() {
        return currentBar;
    }

    /**
     * Update the beat and bar counts based on elapsed time.
     */
    public void updateBeatAndBar() {
        double beatsPerMinute = getBPMValue();
        int beatsPerBar = getBPBValue();
        double elapsedTime = getElapsedTime();
        double beatsPerSecond = beatsPerMinute / 60.0;
        double totalBeats = elapsedTime * beatsPerSecond;

        if (currentBeat == (int) (totalBeats % beatsPerBar) + 1){
            Synthesizer.isNewBeat = false;
        } else {
            Synthesizer.isNewBeat = true;
            Arpeggiator.incrementIndex();
            Synthesizer.setOscillatorsFreq(Utils.Math.getKeyFrequency(Arpeggiator.yieldNextKeyNum()));
        }

        if (currentBar == (int) (totalBeats / beatsPerBar) + 1){
            Synthesizer.isNewBar = false;
        } else {
            Synthesizer.isNewBar = true;
        }

        currentBeat = (int) (totalBeats % beatsPerBar) + 1;
        currentBar = (int) (totalBeats / beatsPerBar) + 1;
        updateLabelParameters();
    }

    /**
     * Update the UI to display current bar and beat.
     */
    public void updateLabelParameters() {
        currentBarLabel.setText(String.format("Current Bar: " + currentBar));
        currentBeatLabel.setText(String.format("Current Beat: " + currentBeat));
    }
}


