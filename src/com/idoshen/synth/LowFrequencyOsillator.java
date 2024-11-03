package com.idoshen.synth;

import com.idoshen.synth.utils.Utils;
import com.idoshen.synth.utils.RefWrapper;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class LowFrequencyOsillator extends SynthControlContainer{

    private Wavetable wavetable = Wavetable.Sine;
    private RefWrapper<Integer> frequency = new RefWrapper<>(0);
    private RefWrapper<Integer> depth = new RefWrapper<>(0);
    private int wavetableStepSize;
    private int wavetableIndex;

    public LowFrequencyOsillator(Synthesizer synthesizer) {
        super(synthesizer);
        JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());
        comboBox.setSelectedItem(Wavetable.Sine);
        comboBox.setBounds(100, 10, 80, 25);
        comboBox.setFocusable(false);
        comboBox.addItemListener(l ->
        {
            if (l.getStateChange() == ItemEvent.SELECTED) {
                wavetable = (Wavetable)l.getItem();
            }
            synthesizer.updateWaveViewer();
        });
        add(comboBox);

        JLabel LFOLabel = new JLabel("LFO:");
        LFOLabel.setBounds(10, 5, 100, 25);
        add(LFOLabel);

        JCheckBox checkBox = new JCheckBox("Enable Oscillator");
        checkBox.setLocation(267,10);
        checkBox.setSize(20,20);
        JLabel checkBoxLabel = new JLabel("disabled");
        checkBoxLabel.setLocation(220,10);
        checkBoxLabel.setSize(50, 20);
        checkBox.addItemListener(e -> {
            if (checkBox.isSelected()) {
                this.on = true;
                checkBoxLabel.setText("enabled");
            } else {
                this.on = false;
                checkBoxLabel.setText("disabled");
            }
        });
        checkBox.setFocusable(false);
        add(checkBox);
        add(checkBoxLabel);

        JLabel frequencyText = new JLabel("frequency");
        frequencyText.setBounds(100, 40, 75, 25);
        add(frequencyText);

        JLabel frequencyParameter = new JLabel(" 0hz");
        frequencyParameter.setBounds(100, 65, 50, 25);
        frequencyParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(frequencyParameter, this, 0, 120, 1, frequency, () -> {
            applyFrequency();
            frequencyParameter.setText(" " + (int) getFrequency() + "hz");
        });
        add(frequencyParameter);

        JLabel depthText = new JLabel("Depth");
        depthText.setBounds(165, 40, 75, 25);
        add(depthText);

        JLabel depthParameter = new JLabel(" 0%");
        depthParameter.setBounds(165, 65, 50, 25);
        depthParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(depthParameter, this, 0, 100, 1, depth, () -> {
            depthParameter.setText(" " + (int) (getDepth() * 100) + "%");
        });
        add(depthParameter);

        setSize(300, 100);
        setBounds(5, 420, 300, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    private double getFrequency() {
        return frequency.val;
    }
    private double getDepth() {
        return depth.val / 100.0;
    }

    private void applyFrequency() {
        wavetableStepSize = (int)((Wavetable.SIZE * getFrequency() * 200)/ Synthesizer.AudioInfo.SAMPLE_RATE); // TODO: Why 1000 multiplied?
    }

    public double nextSample() {
        double sample = wavetable.getSamples()[wavetableIndex];
        wavetableIndex = (wavetableIndex + wavetableStepSize) % Wavetable.SIZE; // Same as in AudioThread.
        return sample * getDepth();
    }

    public void reset() {
        wavetableIndex = 0;
    }
}
