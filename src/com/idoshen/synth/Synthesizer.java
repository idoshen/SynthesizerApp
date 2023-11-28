package com.idoshen.synth;

import com.idoshen.synth.utils.Utils;
import com.idoshen.synth.audioFX.*;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Synthesizer {
    public static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
    public static boolean shouldGenerate;
    public static boolean allKeysReleased;
    public static List<Character> pressedKeys = new ArrayList<>();
    public static Character currentPressedKey;
    private final Oscillator[] oscillators = new Oscillator[3];
    public static int oscillatorCount = 0;
    private final ADSR ADSRPanel = new ADSR(this);
    public final Saturation SaturationPanel = new Saturation(this);
    public final Reverb ReverbPanel = new Reverb(this);
    public final Delay DelayPanel = new Delay(this);
//    public final Arpeggiator ArpeggiatorPanel = new Arpeggiator(this);
    public final BeatTimeManagerThread BTMThread = new BeatTimeManagerThread(this);
    private final WaveViewer waveViewer = new WaveViewer(oscillators , SaturationPanel);
    private final ADSRWaveViewer adsrWaveViewer = new ADSRWaveViewer(ADSRPanel);

    private final JFrame frame = new JFrame("Synthesizer");

    private final AudioThread audioThread = new AudioThread(() -> {
        if (!shouldGenerate) {
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
            double sample = 0;
            for (Oscillator oscillator : oscillators) {
                if (oscillator.on){
                    sample += oscillator.nextSample() / oscillatorCount; // computes the mean amplitude.
                }
            }
            sample *= ADSRPanel.generateADSR(); // ADSR - V
            sample = SaturationPanel.saturate(sample); // Saturation - V
            sample = DelayPanel.process((float)sample); // Delay - VX
//            sample = ReverbPanel.applyReverb((float) sample); // Reverb - X

            s[i] = (short) (Short.MAX_VALUE * sample);
        }
        return s;
    });

    private final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("PRESSED");
            if (allKeysReleased) {
                ADSRPanel.resetFX();
            }
            if (!KEY_FREQUENCIES.containsKey(e.getKeyChar())) {
                return;
            } else if (!pressedKeys.contains(e.getKeyChar())) {
                pressedKeys.add(e.getKeyChar());
                updateLastPressedKey();
//                ArpeggiatorPanel.creatNewArpeggioSequence();
                allKeysReleased = false;
            }
            if (!pressedKeys.isEmpty()) {
                double frequency = KEY_FREQUENCIES.get(currentPressedKey);
                for (Oscillator oscillator : oscillators) {
                    oscillator.setKeyFrequency(frequency);
                }
                if (!audioThread.isRunning()) {
                    shouldGenerate = true;
                    audioThread.triggerPlayback();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println("RELEASED");
            pressedKeys.remove(Character.valueOf(e.getKeyChar()));
            updateLastPressedKey();

            if (pressedKeys.isEmpty()){
                allKeysReleased = true;
//                shouldGenerate = false;
            } else {
                for (Oscillator oscillator : oscillators) {
                    oscillator.setKeyFrequency(KEY_FREQUENCIES.get(currentPressedKey));
                }
            }
        }
    };

    static
    {
        final int STARTING_KEY = 40;
        final int KEY_FREQUENCY_INCREMENT = 1;
        final char[] KEYS = "awsedftgyhujkolp;".toCharArray();

        for (int i = STARTING_KEY, key = 0; i < KEYS.length * KEY_FREQUENCY_INCREMENT + STARTING_KEY; i += KEY_FREQUENCY_INCREMENT, ++key) {
            KEY_FREQUENCIES.put(KEYS[key], Utils.Math.getKeyFrequency(i));
        }
    }

    Synthesizer()
    {
        int y = 0;
        for (int i = 0; i < oscillators.length; i++) {
            oscillators[i] = new Oscillator(this);
            oscillators[i].setLocation(5, y);
            frame.add(oscillators[i]);
            y += 105;
        }

        frame.add(waveViewer);
        frame.add(adsrWaveViewer);
        frame.add(ADSRPanel);
        frame.add(SaturationPanel);
        //frame.add(ReverbPanel);
        frame.add(DelayPanel);
        frame.add(BTMThread.beatTimeManager);
        frame.addKeyListener(keyAdapter);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                audioThread.close();
                BTMThread.close();
            }
        });
        // in order to kill all threads on close
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(640,625);
        frame.setResizable(true);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void updateLastPressedKey() {
        if (!pressedKeys.isEmpty()) {
            currentPressedKey = pressedKeys.get(pressedKeys.size() - 1);
        } else {
            currentPressedKey = null;
        }
    }

    public KeyAdapter getKeyAdapter() { return keyAdapter; }
    public void updateWaveViewer() { waveViewer.repaint(); }
    public void updateADSRWaveViewer() { adsrWaveViewer.repaint(); }
    public static class AudioInfo {
        public static  final  int SAMPLE_RATE = 44100;
    }
}