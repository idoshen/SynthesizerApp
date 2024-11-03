package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.RefWrapper;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;

public class Noise  extends SynthControlContainer implements AudioEffect{

    private RefWrapper<Integer> noise_level = new RefWrapper<>(0);
    private Synthesizer synthesizer;

    public Noise(Synthesizer synthesizer) {
        super(synthesizer);
        this.synthesizer = synthesizer;
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(5, 525, 147, 100);
        setSize(147, 100);
        setLayout(null);

        JLabel NoiseHeader = new JLabel("Noise:");
        NoiseHeader.setBounds(10, 5, 100, 25);
        add(NoiseHeader);

        JLabel noiseParameter = new JLabel(" 0% ");
        noiseParameter.setBounds(10, 30, 50, 25);
        noiseParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(noiseParameter, this, 0, 1000, 1, noise_level, () -> {
            noiseParameter.setText(" " + (int)(100.0d - getNoiseLevel() * 100.0d) + "%");
        });
        add(noiseParameter);
    }

    @Override
    public double applyEffect(double sample){
        if (synthesizer.isKeyPressed || sample != 0d) {
            double randomeNoiseSample = (Math.random() - 0.5) * 2;
            return (sample * getNoiseLevel() + randomeNoiseSample * (1 - getNoiseLevel()));
        } else {
            return 0d;
        }
    }

//    private double[] pinkNoiseBuffer = new double[100];
//    private int pinkNoiseIndex = 0;
//
//    public double generateNoise(double input){
//        // Update one of the white noise values in the buffer
//        pinkNoiseIndex = (pinkNoiseIndex + 1) % pinkNoiseBuffer.length;
//        pinkNoiseBuffer[pinkNoiseIndex] = Math.random() - 0.5;
//
//        // Sum all values in the buffer for the pink noise effect
//        double sum = 0.0;
//        for (double noise : pinkNoiseBuffer) {
//            sum += noise;
//        }
//        return  (input + (sum / pinkNoiseBuffer.length)) / 2;
//    }

    @Override
    public String getName() {
        return "Noise";
    }

    public double getNoiseLevel() { return (1.0d - noise_level.val / 1000.0d); }

}

