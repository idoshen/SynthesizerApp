package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.RefWrapper;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;

public class Saturation extends SynthControlContainer {

    private RefWrapper<Integer> saturation_level = new RefWrapper<>(0);

    public Saturation(Synthesizer synthesizer) {
        super(synthesizer);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(157, 420, 147, 100);
        setSize(147, 100);
        setLayout(null);

        JLabel SaturationHeader = new JLabel("Saturation:");
        SaturationHeader.setBounds(10, 5, 100, 25);
        add(SaturationHeader);
        JLabel saturationParameter = new JLabel(" 0% ");
        saturationParameter.setBounds(10, 30, 50, 25);
        saturationParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(saturationParameter, this, 0, 1000, 1, saturation_level, () -> {
            saturationParameter.setText(" " + (int)(100.0d - getSatrurationLevel() * 100.0d) + "%");
            synthesizer.updateWaveViewer();
        });
        add(saturationParameter);
    }

    public double saturate(double sample){

        if (Math.abs(sample) < getSatrurationLevel()){
            return sample / getSatrurationLevel();
        }
        return Math.signum(sample);
    }

    public double getSatrurationLevel() { return (1.0d - saturation_level.val / 1000.0d); }
}
