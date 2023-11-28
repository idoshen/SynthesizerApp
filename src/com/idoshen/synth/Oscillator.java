package com.idoshen.synth;

import com.idoshen.synth.utils.RefWrapper;
import com.idoshen.synth.utils.Utils;


import javax.swing.*;
import java.awt.event.*;

public class Oscillator extends SynthControlContainer {
    private static final int TONE_OFFSET_LIMIT = 200;
    private Wavetable wavetable = Wavetable.Sine;
    private RefWrapper<Integer> toneOffset = new RefWrapper<>(0);
    private RefWrapper<Integer> SemitoneOffset = new RefWrapper<>(0);
    private RefWrapper<Integer> volume = new RefWrapper<>(100);
    private double keyFrequency;
    private int wavetableStepSize;
    private int wavetableIndex;

    public Oscillator(Synthesizer synthesizer) {
        super(synthesizer);
        JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());
        comboBox.setSelectedItem(Wavetable.Sine);
        comboBox.setBounds(10, 10, 80, 25);
        comboBox.setFocusable(false);
        comboBox.addItemListener(l ->
        {
            if (l.getStateChange() == ItemEvent.SELECTED) {
                wavetable = (Wavetable)l.getItem();
            }
            synthesizer.updateWaveViewer();
        });
        add(comboBox);

        JCheckBox checkBox = new JCheckBox("Enable Oscillator");
        checkBox.setLocation(267,10);
        checkBox.setSize(20,20);
        JLabel checkBoxLabel = new JLabel("disabled");
        checkBoxLabel.setLocation(220,10);
        checkBoxLabel.setSize(50, 20);
        checkBox.addItemListener(e -> {
            if (checkBox.isSelected()) {
                Synthesizer.oscillatorCount++;
                this.on = true;
                synthesizer.updateWaveViewer();
                checkBoxLabel.setText("enabled");
            } else {
                Synthesizer.oscillatorCount--;
                this.on = false;
                synthesizer.updateWaveViewer();
                checkBoxLabel.setText("disabled");
            }
        });

        add(checkBox);
        add(checkBoxLabel);

        JLabel SemitoneText = new JLabel("Semitone");
        SemitoneText.setBounds(100, 40, 75, 25);
        add(SemitoneText);
        JLabel SemitoneParameter = new JLabel(" 0st");
        SemitoneParameter.setBounds(100, 65, 50, 25);
        SemitoneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(SemitoneParameter, this, -48, 48, 1, SemitoneOffset, () -> {
            applyToneOffset();
            SemitoneParameter.setText(" " + getSemitonesOffset() + "st");
            synthesizer.updateWaveViewer();
        });
        add(SemitoneParameter);

        JLabel toneText = new JLabel("Cent");
        toneText.setBounds(172, 40, 75, 25);
        add(toneText);
        JLabel toneParameter = new JLabel("0¢");
        toneParameter.setBounds(165, 65, 50, 25);
        toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(toneParameter, this, -TONE_OFFSET_LIMIT, TONE_OFFSET_LIMIT, 1, toneOffset, () -> {
            applyToneOffset();
            toneParameter.setText(" " + (int)(getToneOffset()) + "¢");
            synthesizer.updateWaveViewer();
        });
        add(toneParameter);

        JLabel volumeText = new JLabel("Volume");
        volumeText.setBounds(225, 40, 75, 25);
        add(volumeText);
        JLabel volumeParameter = new JLabel(" 100%");
        volumeParameter.setBounds(222, 65, 50, 25);
        volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(volumeParameter, this, 0, 100, 1, volume, () -> {
            volumeParameter.setText(" "+ volume.val + "%");
            synthesizer.updateWaveViewer();
        });
        add(volumeParameter);

        setSize(300, 100);
        setBounds(5, 315, 300, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    public double nextSample() {
        double sample = wavetable.getSamples()[wavetableIndex] * getVolumeMultiplier();
        wavetableIndex = (wavetableIndex + wavetableStepSize) % Wavetable.SIZE; // Same as in AudioThread.
        return sample;
    }

    public void setKeyFrequency(double frequency) {
        keyFrequency = frequency;
        applyToneOffset();
    }

    public double[] getSampleWaveform(int numSamples) { // only for WaveViewer.
        double[] samples = new double[numSamples];
        double frequency = 1.0 / (numSamples / (double)Synthesizer.AudioInfo.SAMPLE_RATE) * 3.0;
        int index = 0;
        int step = (int)(Wavetable.SIZE * Utils.Math.offsetTone(Utils.Math.offsetSemitone(frequency, getSemitonesOffset()), getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
        for (int i = 0; i < numSamples; ++i) {
            samples[i] = wavetable.getSamples()[index] * getVolumeMultiplier();
            index = (index + step) % Wavetable.SIZE;
        }
        return samples;
    }

    private double getToneOffset() { return toneOffset.val; }

    private int getSemitonesOffset() { return SemitoneOffset.val; }

    private double getVolumeMultiplier() { return volume.val / 100.0; }

    private void applyToneOffset() {
        wavetableStepSize = (int)(Wavetable.SIZE * Utils.Math.offsetTone(Utils.Math.offsetSemitone(keyFrequency, getSemitonesOffset()), getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
    }
}
