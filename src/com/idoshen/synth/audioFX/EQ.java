package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.RefWrapper;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;

public class EQ extends SynthControlContainer implements AudioEffect{

    private final int MAX_EQ = 22000;
    private final int MIN_EQ = 0;
    private double bassGain = 1.0;
    private double midGain = 1.0;
    private double trebleGain = 1.0;
    private final double sampleRate = Synthesizer.AudioInfo.SAMPLE_RATE;
    private RefWrapper<Integer> bassCutOff = new RefWrapper<>(200);
    private RefWrapper<Integer> midLowCut = new RefWrapper<>(200);
    private RefWrapper<Integer> midHighCut = new RefWrapper<>(2000);
    private RefWrapper<Integer> trebleCutOff = new RefWrapper<>(2000);

    public EQ(Synthesizer synthesizer) {
        super(synthesizer);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(310, 525, 310, 100);
        setSize(310, 100);
        setLayout(null);

        JLabel header = new JLabel("EQ:");
        header.setBounds(10, 5, 100, 25);
        add(header);

        JLabel bassCutOffText = new JLabel("LPF");
        bassCutOffText.setBounds(15, 40, 75, 25);
        add(bassCutOffText);
        JLabel bassCutOffParameter = new JLabel(" 200hz");
        bassCutOffParameter.setBounds(10, 65, 50, 25);
        bassCutOffParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(bassCutOffParameter, this, MIN_EQ, MAX_EQ, 1, bassCutOff, () -> {
            bassCutOffParameter.setText(" " + getBassCutOffValue() + "hz");
            synthesizer.updateADSRWaveViewer();
        });
        add(bassCutOffParameter);

        JLabel midLowCutText = new JLabel("BPFL");
        midLowCutText.setBounds(75, 40, 75, 25);
        add(midLowCutText);
        JLabel midLowCutParameter = new JLabel(" 200hz");
        midLowCutParameter.setBounds(70, 65, 50, 25);
        midLowCutParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(midLowCutParameter, this, MIN_EQ, MAX_EQ, 1, midLowCut, () -> {
            midLowCutParameter.setText(" " + getMidLowCutValue() + "hz");
            synthesizer.updateADSRWaveViewer();
        });
        add(midLowCutParameter);

        JLabel midHighCutText = new JLabel("BPFH");
        midHighCutText.setBounds(133, 40, 75, 25);
        add(midHighCutText);
        JLabel midHighCutParameter = new JLabel(" 2000hz");
        midHighCutParameter.setBounds(130, 65, 50, 25);
        midHighCutParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(midHighCutParameter, this, MIN_EQ, MAX_EQ, 1, midHighCut, () -> {
            midHighCutParameter.setText(" " + getMidHighCutValue() + "hz");
            synthesizer.updateADSRWaveViewer();
        });
        add(midHighCutParameter);

        JLabel trebleCutOffText = new JLabel("HPF");
        trebleCutOffText.setBounds(193, 40, 75, 25);
        add(trebleCutOffText);
        JLabel trebleCutOffParameter = new JLabel(" 2000hz");
        trebleCutOffParameter.setBounds(190, 65, 50, 25);
        trebleCutOffParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(trebleCutOffParameter, this, MIN_EQ, MAX_EQ, 1, trebleCutOff, () -> {
            trebleCutOffParameter.setText(" " + getTrebleCutOffValue() + "hz");
            synthesizer.updateADSRWaveViewer();
        });
        add(trebleCutOffParameter);
    }

    @Override
    public double applyEffect(double sample) {

        double bass = lowPassFilter(sample, sampleRate, getBassCutOffValue());
        double mid = bandPassFilter(sample, sampleRate, getMidLowCutValue(), getMidHighCutValue());
        double treble = highPassFilter(sample, sampleRate, getTrebleCutOffValue());

        // Adjust gain for each band
        bass *= bassGain;
        mid *= midGain;
        treble *= trebleGain;

        double output = (bass + mid + treble) / 3.0;

        return output;
    }

    private double lowPassFilter(double sample, double sampleRate, double cutoff) {
        double rc = 1.0 / (cutoff * 2 * Math.PI);
        double dt = 1.0 / sampleRate;
        double alpha = dt / (rc + dt);
        return alpha * sample;
    }

    private double bandPassFilter(double sample, double sampleRate, double lowCut, double highCut) {
        double low = lowPassFilter(sample, sampleRate, highCut);
        return highPassFilter(low, sampleRate, lowCut);
    }

    private double highPassFilter(double sample, double sampleRate, double cutoff) {
        double rc = 1.0 / (cutoff * 2 * Math.PI);
        double dt = 1.0 / sampleRate;
        double alpha = rc / (rc + dt);
        return alpha * sample;
    }

    @Override
    public String getName() {
        return "EQ";
    }

    public Integer getBassCutOffValue() { return bassCutOff.val; }
    public Integer getMidLowCutValue() { return midLowCut.val; }
    public Integer getMidHighCutValue() { return midHighCut.val; }
    public Integer getTrebleCutOffValue() { return trebleCutOff.val; }

}

