package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.RefWrapper;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;

public class Reverb extends SynthControlContainer implements AudioEffect{
    private static final int REVERB_BUFFER_SIZE = 8192;
    private static final float REVERB_DECAY = 0.5f;
    private RefWrapper<Integer> reverb_decay_level = new RefWrapper<>(0);
    private double[] reverbBuffer = new double[REVERB_BUFFER_SIZE];
    private int reverbBufferIndex = 0;

    public Reverb(Synthesizer synthesizer){
        super(synthesizer);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(110, 420, 100, 60);
        setSize(100, 60);
        setLayout(null);

        JLabel ReverbHeader = new JLabel("Reverb:");
        ReverbHeader.setBounds(10, 5, 100, 25);
        add(ReverbHeader);
        JLabel reverbParameter = new JLabel(" 0% ");
        reverbParameter.setBounds(10, 30, 50, 25);
        reverbParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(reverbParameter, this, 0, 1000, 1, reverb_decay_level, () -> {
            reverbParameter.setText(" " + (int)(100.0d - getReverbLevel() * 100.0d) + "%");
            synthesizer.updateWaveViewer();
        });
        add(reverbParameter);
    }

    @Override
    public double applyEffect(double sample) {
        double reverbSample = reverbBuffer[reverbBufferIndex];
        double processedSample = sample + (reverb_decay_level.val / 1000.0f) * reverbSample;
        System.out.println(processedSample);
        reverbBuffer[reverbBufferIndex] = processedSample;
        reverbBufferIndex = (reverbBufferIndex + 1) % REVERB_BUFFER_SIZE;
        normalizeReverbBuffer();
        return processedSample;
    }

    public void resetFX() {
        reverbBuffer = new double[REVERB_BUFFER_SIZE];
    }

    private void normalizeReverbBuffer() {
        double maxAmplitude = 0;

        // Find the maximum absolute amplitude in the buffer
        for (double value : reverbBuffer) {
            double absValue = Math.abs(value);
            if (absValue > maxAmplitude) {
                maxAmplitude = absValue;
            }
        }

        // Scale the buffer if the maximum amplitude exceeds 1.0
        if (maxAmplitude > 1.0f) {
            double scaleFactor = 1.0f / maxAmplitude;

            // Apply scaling to each element in the buffer
            for (int i = 0; i < REVERB_BUFFER_SIZE; i++) {
                reverbBuffer[i] *= scaleFactor;
            }
        }
    }

    @Override
    public String getName() {
        return "Reverb";
    }

    public double getReverbLevel(){ return (1.0d - reverb_decay_level.val / 1000.0d); }
}
