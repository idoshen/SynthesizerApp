package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.RefWrapper;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;

public class Delay extends SynthControlContainer implements AudioEffect{
    public final int MAX_MS_DELAY = 1000;
    public final int MAX_DECAY = 1000;
    private double[] delayLine;
    private int delayLineLength = 0;
    private int currentIndex;

    private RefWrapper<Integer> Delay_ms = new RefWrapper<>(0);
    private RefWrapper<Integer> Decay_factor = new RefWrapper<>(0);

    public Delay(Synthesizer synthesizer) {
        super(synthesizer);
        setBorder(Utils.WindowDesign.LINE_BORDER);
//        setBounds(5, 420, 147, 100);
        setBounds(5, 525, 147, 100);
        setLayout(null);

        JLabel DelayHeader = new JLabel("Delay:");
        DelayHeader.setBounds(10, 5, 100, 25);
        add(DelayHeader);

        JLabel TimeText = new JLabel("Time:");
        TimeText.setBounds(15, 40, 75, 25);
        add(TimeText);
        JLabel TimeParameter = new JLabel(" 0ms ");
        TimeParameter.setBounds(10, 65, 50, 25);
        TimeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(TimeParameter, this, 0, MAX_MS_DELAY, 1, Delay_ms, () -> {
            TimeParameter.setText(" " + Delay_ms.val + "ms");
            setDelayTime(Delay_ms.val);
        });
        add(TimeParameter);

        JLabel FeedbackHeader = new JLabel("Feedback:");
        FeedbackHeader.setBounds(75, 40, 75, 25);
        add(FeedbackHeader);
        JLabel FeedbackParameter = new JLabel("0% ");
        FeedbackParameter.setBounds(70, 65, 50, 25);
        FeedbackParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(FeedbackParameter, this, 0, MAX_DECAY, 1, Decay_factor, () -> {
            FeedbackParameter.setText(" " + (int)(Decay_factor.val / 10.0) + "% ");
        });
        add(FeedbackParameter);

    }

    @Override
    public double applyEffect(double inputSample) {
        float floatInputSample = (float) inputSample;
        float Epsilon = 1e-4f;

        // If the input sample is very close to zero, set it to zero
        if (Math.abs(inputSample) < Epsilon){
            floatInputSample = 0f;
        }

        // If the delay line is empty, return the input sample
        if (delayLineLength == 0){
            return floatInputSample;
        }
//        System.out.println(delayLine[currentIndex]);

        //TODO: FIND THE CORRECT FORMULA.
//        float mixFactor = 0.7f; // You can adjust this value to control the mix
//        float outputSample = mixFactor * floatInputSample +  (1 - mixFactor) * getDelayedSample(); // MAYBE FOR REVERB!!!
        double outputSample =  floatInputSample + getDelayedSample();
//        outputSample =  Math.max(-1.0f, Math.min(1.0f, outputSample)); // Not good, audio saturation occur.


        // Store the current input in the delay line
        delayLine[currentIndex] = outputSample;

        // Check for clipping
        if (Math.abs(outputSample) > 1f){
            delayLine = normalize(delayLine); // Normalize the delay line
        }

        outputSample = delayLine[currentIndex];

        return outputSample;
    }

    private void incrementIndex(){
        // Move the delay line index, wrap around if necessary
        currentIndex = (currentIndex + 1) % delayLineLength;
    }

    private double getDelayedSample() {
        delayLine[currentIndex] *= (Decay_factor.val / 1000.0f);

        // Check for clipping
        if (Math.abs(delayLine[currentIndex]) < 1e-4){
            delayLine[currentIndex] = 0f;
        }
        if (Math.abs(delayLine[currentIndex]) > 1){
            delayLine[currentIndex] = 1f;
        }
        incrementIndex();

        return delayLine[currentIndex];
    }

    public void setDelayTime(int delayMilliseconds) {
        // Adjust the length of the delay line based on the new delay time
        delayLineLength = (delayMilliseconds * Synthesizer.AudioInfo.SAMPLE_RATE) / 1000;

        // Reset the delay line and index
        delayLine = new double[delayLineLength];
        currentIndex = 0;
    }

    // Normalizes an array to the range [-1, 1]
    public static double[] normalize(double[] samples) {
        // Find the max absolute value in the array
        double max = 0.0f;
        for (double sample : samples) {
            if (Math.abs(sample) > max) {
                max = Math.abs(sample);
            }
        }

        // Avoid division by zero in case of all-zero input
        if (max == 0.0f) {
            return samples;
        }

        // Normalize all samples to [-1, 1] range
        double[] normalizedSamples = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            normalizedSamples[i] = samples[i] / max;
        }

        return normalizedSamples;
    }

    @Override
    public String getName() {
        return "Delay";
    }
}

