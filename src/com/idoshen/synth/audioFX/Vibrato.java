package com.idoshen.synth.audioFX;

import com.idoshen.synth.LowFrequencyOsillator;
import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.Wavetable;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class Vibrato extends SynthControlContainer implements AudioEffect {
    public LowFrequencyOsillator lfo;
    public double lfoSample;

    public Vibrato(Synthesizer synthesizer) {
        super(synthesizer);
        lfo = new LowFrequencyOsillator(synthesizer);

        JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());
        comboBox.setSelectedItem(Wavetable.Sine);
        comboBox.setBounds(100, 10, 80, 25);
        comboBox.setFocusable(false);
        comboBox.addItemListener(l ->
        {
            if (l.getStateChange() == ItemEvent.SELECTED) {
                lfo.wavetable = (Wavetable) l.getItem();
            }
            synthesizer.updateWaveViewer();
        });
        add(comboBox);

        JLabel LFOLabel = new JLabel("Vibrato:");
        LFOLabel.setBounds(10, 5, 100, 25);
        add(LFOLabel);

        JCheckBox checkBox = new JCheckBox("Enable Oscillator");
        checkBox.setLocation(267, 10);
        checkBox.setSize(20, 20);
        JLabel checkBoxLabel = new JLabel("disabled");
        checkBoxLabel.setLocation(220, 10);
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
        Utils.ParameterHandling.addParametersMouseListeners(frequencyParameter, this, 0, 120, 1, lfo.frequency, () -> {
            lfo.applyFrequency();
            frequencyParameter.setText(" " + (int) lfo.getFrequency() + "hz");
        });
        add(frequencyParameter);

        JLabel depthText = new JLabel("Depth");
        depthText.setBounds(165, 40, 75, 25);
        add(depthText);

        JLabel depthParameter = new JLabel(" 0%");
        depthParameter.setBounds(165, 65, 50, 25);
        depthParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(depthParameter, this, 0, 100, 1, lfo.depth, () -> {
            depthParameter.setText(" " + (int) (lfo.getDepth() * 100) + "%");
        });
        add(depthParameter);

        setSize(300, 100);
        setBounds(310, 525, 310, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    @Override
    public double applyEffect(double currentFrequency) {
        if (on) {
            return currentFrequency * (1 + (lfoSample * lfo.getDepth()));
        }
        return currentFrequency;
    }

    public void update() {
        lfoSample = lfo.nextSample();
    }
}
