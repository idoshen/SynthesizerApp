package com.idoshen.synth.audioFX;

public interface AudioEffect {
    double applyEffect(double sample);
    String getName();
}
