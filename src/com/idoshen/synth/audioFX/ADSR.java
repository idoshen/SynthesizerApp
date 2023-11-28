package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.*;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;
public class ADSR extends SynthControlContainer {

    private static double time = 0.0;

    private static double previousEnvelopeSample = 0.0;
    private static boolean isInterrupted = false;

    private void increaseFXTime() {
        time += 1.0d / Synthesizer.AudioInfo.SAMPLE_RATE;
    }

    private void decreaseFXTime() {
        time -= 1.0d / Synthesizer.AudioInfo.SAMPLE_RATE;
    }

    public void resetFX() {
        time = 0.0d;
        effectFinished = false;
    }

    private boolean effectFinished = false;


    private final int MaxTimeValue = 3000;

    private RefWrapper<Integer> AttackTimeValue = new RefWrapper<>(1);
    private RefWrapper<Integer> DecayTimeValue = new RefWrapper<>(0);
    private RefWrapper<Integer> SustainLevelValue = new RefWrapper<>(1000);
    private RefWrapper<Integer> ReleaseTimeValue = new RefWrapper<>(1);

    public ADSR(Synthesizer synthesizer) {
        super(synthesizer);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(5, 315, 300, 100);
        setSize(300, 100);
        setLayout(null);

        JLabel header = new JLabel("ADSR envelope:");
        header.setBounds(10, 5, 100, 25);
        add(header);

        JLabel attackText = new JLabel("Attack");
        attackText.setBounds(15, 40, 75, 25);
        add(attackText);
        JLabel attackParameter = new JLabel(" 0.000 s");
        attackParameter.setBounds(10, 65, 50, 25);
        attackParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(attackParameter, this, 0, MaxTimeValue, 1, AttackTimeValue, () -> {
            attackParameter.setText(String.format(" %.3f", getAttackTimeValue()) + " s");
            synthesizer.updateADSRWaveViewer();
            Synthesizer.shouldGenerate = false; // TODO: FIND A BETTER SOLUTION!
        });
        add(attackParameter);

        JLabel decayText = new JLabel("Decay");
        decayText.setBounds(75, 40, 75, 25);
        add(decayText);
        JLabel decayParameter = new JLabel(" 0.000 s");
        decayParameter.setBounds(70, 65, 50, 25);
        decayParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(decayParameter, this, 0, MaxTimeValue, 1, DecayTimeValue, () -> {
            decayParameter.setText(String.format(" %.3f", getDecayTimeValue()) + " s");
            synthesizer.updateADSRWaveViewer();
            Synthesizer.shouldGenerate = false; // TODO: FIND A BETTER SOLUTION!
        });
        add(decayParameter);

        JLabel sustainText = new JLabel("Sustain");
        sustainText.setBounds(133, 40, 75, 25);
        add(sustainText);
        JLabel sustainParameter = new JLabel(" 1.000 l");
        sustainParameter.setBounds(130, 65, 50, 25);
        sustainParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(sustainParameter, this, 0, 1000, 1, SustainLevelValue, () -> {
            sustainParameter.setText(String.format(" %.3f", getSustainLevelValue()) + " l");
            synthesizer.updateADSRWaveViewer();
            Synthesizer.shouldGenerate = false; // TODO: FIND A BETTER SOLUTION!
        });
        add(sustainParameter);

        JLabel releaseText = new JLabel("Release");
        releaseText.setBounds(193, 40, 75, 25);
        add(releaseText);
        JLabel releaseParameter = new JLabel(" 0.000 s");
        releaseParameter.setBounds(190, 65, 50, 25);
        releaseParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParametersMouseListeners(releaseParameter, this, 0, MaxTimeValue, 1, ReleaseTimeValue, () -> {
            releaseParameter.setText(String.format(" %.3f", getReleaseTimeValue()) + " s");
            synthesizer.updateADSRWaveViewer();
            Synthesizer.shouldGenerate = false; // TODO: FIND A BETTER SOLUTION!
        });
        add(releaseParameter);
    }

    public float generateADSR() {
        double envelope = 0.0;

        double attackTime = getAttackTimeValue();
        double decayTime = getDecayTimeValue();
        double sustainLevel = getSustainLevelValue();
        double releaseTime = getReleaseTimeValue();

        if (!Synthesizer.allKeysReleased) {
            if (time < attackTime) {
                // Attack phase
                envelope = time / attackTime;
                increaseFXTime();
            } else if (time < attackTime + decayTime) {
                // Decay phase
                envelope = 1.0 - (1.0 - sustainLevel) * ((time - attackTime) / decayTime);
                increaseFXTime();
            } else {
                // Sustain phase
                envelope = sustainLevel;
            }
            previousEnvelopeSample = envelope;
        } else if (time < attackTime + decayTime) {
            time = attackTime + decayTime;
            envelope = previousEnvelopeSample * (1.0 - (time - (attackTime + decayTime)) / releaseTime);
            increaseFXTime();
        } else if (time <= attackTime + decayTime + releaseTime) {
            // Release phase
            envelope = previousEnvelopeSample * (1.0 - (time - (attackTime + decayTime)) / releaseTime);
            increaseFXTime();
        } else {
//            Synthesizer.shouldGenerate = false;
//            effectFinished = true;
        }

        return (float) envelope;
    }

    public double getAttackTimeValue() { return AttackTimeValue.val / 1000.0; }

    public double getDecayTimeValue() { return DecayTimeValue.val / 1000.0; }

    public double getSustainLevelValue() { return SustainLevelValue.val / 1000.0; }

    public double getReleaseTimeValue() { return ReleaseTimeValue.val / 1000.0; }
}

