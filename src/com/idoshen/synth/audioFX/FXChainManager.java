package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.Utils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

enum FXS {
    ADSR, Delay, Saturation, Noise, EQ;
}

public class FXChainManager extends SynthControlContainer {

    public List<AudioEffect> effects = new ArrayList<>();
    private Synthesizer synthesizer;
    private JLabel effectChainLabel;
    public FXChainManager(Synthesizer synthesizer){
        super(synthesizer);
        this.synthesizer = synthesizer;
        effects.add(synthesizer.ADSRPanel);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(5, 630, 147, 100);
        setSize(615, 100);
        setLayout(null);

        JLabel FXChainManagerHeader = new JLabel("Chain:");
        FXChainManagerHeader.setBounds(10, 5, 100, 25);
        add(FXChainManagerHeader);

        JComboBox<FXS> addFX = new JComboBox<>(FXS.values());
        addFX.setSelectedItem(FXS.ADSR);
        addFX.setBounds(10, 30, 100, 25);
        addFX.setFocusable(false);
        add(addFX);

        JButton addFXButton = new JButton("+");
        addFXButton.setFocusable(false);
        addFXButton.setBounds(10, 65, 45, 25);
        addFXButton.addActionListener(e -> {
            FXS selectedEffect = (FXS) addFX.getSelectedItem();
            if (selectedEffect != null) {
                AudioEffect effectInstance = createEffect(selectedEffect);
                addEffect(effectInstance);
                updateChainUI();
            }
        });
        add(addFXButton);

        JButton removeFXButton = new JButton("-");
        removeFXButton.setFocusable(false);
        removeFXButton.setBounds(65, 65, 45, 25);
        removeFXButton.addActionListener(e -> {
            FXS selectedEffect = (FXS) addFX.getSelectedItem();
            AudioEffect effectToRemove = findEffect(selectedEffect);
            if (selectedEffect != null) {
                removeEffect(effectToRemove);
                updateChainUI();
            }
        });
        add(removeFXButton);

        effectChainLabel = new JLabel("Current chain:");
        effectChainLabel.setBounds(140, 30, 400, 25);
        add(effectChainLabel);

        updateChainUI();
    }

    public void updateChainUI(){
        String currentChain = "Current chain:   ";
        for (AudioEffect effect : effects) {
            currentChain += effect.getName() + " -> ";
        }
        currentChain = currentChain.substring(0, currentChain.length() - 4);
        effectChainLabel.setText(currentChain);
    }

    public void addEffect(AudioEffect effect) {
        if (!effects.contains(effect)) {
            effects.add(effect);
        }
    }

    public void removeEffect(AudioEffect effect) {
        effects.remove(effect);
    }

    public void clearEffects() {
        effects.clear();
    }

    public double process(double sample) {
        double processedSample = sample;
        for (AudioEffect effect : effects) {
            processedSample = effect.applyEffect(processedSample);
        }
        return processedSample;
    }

    private AudioEffect createEffect(FXS selectedEffect) {
        switch (selectedEffect) {
            case ADSR:
                return synthesizer.ADSRPanel;
            case Delay:
                System.out.println("Delay");
                return synthesizer.DelayPanel;
            case Saturation:
                return synthesizer.SaturationPanel;
            case Noise:
                return synthesizer.NoisePanel;
            case EQ:
                return synthesizer.EQPanel;
            default:
                return null;
        }
    }

    private AudioEffect findEffect(FXS selectedEffect) {
        for (AudioEffect effect : effects) {
            if (effect.getName().equals(selectedEffect.name())) { // Assuming getName() returns the effect name
                return effect;
            }
        }
        return null;
    }
}

