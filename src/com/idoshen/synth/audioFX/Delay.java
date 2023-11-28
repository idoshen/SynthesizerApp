package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.RefWrapper;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;

public class Delay extends SynthControlContainer {
    public final int MAX_MS_DELAY = 1000;
    public final int MAX_DECAY = 1000;
    private float[] delayLine;
    private int delayLineLength = 0;
    private int currentIndex;

    private RefWrapper<Integer> Delay_ms = new RefWrapper<>(0);
    private RefWrapper<Integer> Decay_factor = new RefWrapper<>(0);

    public Delay(Synthesizer synthesizer) {
        super(synthesizer);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(5, 420, 147, 100);
        setSize(147, 100);
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

    public float process(float inputSample) {
        if (Math.abs(inputSample) < 0.001){
            inputSample = 0f;
        }
        if (delayLineLength == 0){
            return inputSample;
        }
//        System.out.println(delayLine[currentIndex]);
        // Output is the current input plus the delayed sample

//        float mixFactor = 0.9f; // You can adjust this value to control the mix
//        float outputSample = (1 - mixFactor) * inputSample + mixFactor * getDelayedSample(); //MAYBE FOR REVERB!!!

        float outputSample = inputSample / 2 + getDelayedSample() / 2; //TODO: FIND THE CORRECT FORMULA.

        // Store the current input in the delay line
        delayLine[currentIndex] = outputSample;

//        return Math.max(-1, Math.min(1, outputSample));
        return outputSample;
    }

    private void incrementIndex(){
        // Move the delay line index, wrap around if necessary
        currentIndex = (currentIndex + 1) % delayLineLength;
    }

    private float getDelayedSample() {
        delayLine[currentIndex] *= (Decay_factor.val / 1000.0f);
        if (Math.abs(delayLine[currentIndex]) < 1e-4){
            delayLine[currentIndex] = 0f;
        }
        incrementIndex();
        return delayLine[currentIndex];
    }

    public void setDelayTime(int delayMilliseconds) {
        // Adjust the length of the delay line based on the new delay time
        delayLineLength = (delayMilliseconds * Synthesizer.AudioInfo.SAMPLE_RATE) / 1000;

        // Reset the delay line and index
        delayLine = new float[delayLineLength];
        currentIndex = 0;
    }

}

