package com.idoshen.synth;

import com.idoshen.synth.utils.Utils;

enum Wavetable {
    Sine, Square, Sawtooth, Triangle;

    static final int SIZE = 44100;

    private final float[] samples = new float[SIZE];

    static {
        final double FUNDAMENTAL_FREQUENCY = 1d / (SIZE / (double)Synthesizer.AudioInfo.SAMPLE_RATE);
        for (int i = 0; i < SIZE; i++) {
            double time = i / (double) Synthesizer.AudioInfo.SAMPLE_RATE;
            double timeDivPeriod = time / (1d / FUNDAMENTAL_FREQUENCY);
            Sine.samples[i] = (float) -Math.sin(Utils.Math.frequencyToAngularFrequency(FUNDAMENTAL_FREQUENCY) * time);
            Square.samples[i] = Math.signum(Sine.samples[i]);
            Sawtooth.samples[i] = (float) (2d * (Math.floor(0.5 + timeDivPeriod) - timeDivPeriod));
            Triangle.samples[i] = (float) (2d * Math.abs(Sawtooth.samples[i]) - 1d);
        }
    }

    float[] getSamples() { return samples; }
}
