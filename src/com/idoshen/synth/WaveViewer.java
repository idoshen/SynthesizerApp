package com.idoshen.synth;

import com.idoshen.synth.audioFX.Saturation;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class WaveViewer extends JPanel {
    private Oscillator[] oscillators;
    private Saturation saturation;

    public WaveViewer (Oscillator[] oscillators, Saturation saturation) {
        this.oscillators = oscillators;
        this.saturation = saturation;
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(310, 0, 310, 310);
        setSize(310, 310);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        final int PADDING = 10;
        super.paintComponent(graphics);
        int numSamples = (getWidth() - PADDING * 2);
        double[] mixedSamples = new double[numSamples];

        for (Oscillator oscillator : oscillators) {
            if (oscillator.on) {
                double[] samples = oscillator.getSampleWaveform(numSamples);
                for (int i = 0; i < samples.length; ++i) {
                    mixedSamples[i] += saturation.saturate(samples[i]) / Synthesizer.oscillatorCount;
                }
            }
        }

        int midY = getHeight() / 2;
        Function<Double, Integer> sampleToYCoord = sample -> (int)(midY + sample * (midY - PADDING));
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.drawLine(PADDING, midY, getWidth() - PADDING, midY);
        graphics2D.drawLine(PADDING, PADDING, PADDING, getHeight() - PADDING);

        for (int i = 0; i < numSamples; ++i) {
            int nextY = i == numSamples - 1 ? sampleToYCoord.apply(mixedSamples[i]) : sampleToYCoord.apply(mixedSamples[i + 1]);
            graphics2D.drawLine(PADDING + i, sampleToYCoord.apply(mixedSamples[i]), PADDING + i + 1, nextY);
        }
    }
}
