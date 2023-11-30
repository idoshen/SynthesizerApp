package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.Utils;

public class Arpeggiator extends SynthControlContainer {

    private static int[] arpeggioSequence = {0, 0, 0}; // TODO: change this bad default!!!
    private static int arpeggioIndex = 0;
    public Arpeggiator(Synthesizer synthesizer){
        super(synthesizer);
    }

    public static void creatNewArpeggioSequence(){
        arpeggioSequence = new int[3];
        arpeggioIndex = 0;
        int currentKeyNumber = Utils.Math.frequencyToKeyNumber(Synthesizer.KEY_FREQUENCIES.get(Synthesizer.currentPressedKey));
        arpeggioSequence[0] = currentKeyNumber;
        arpeggioSequence[1] = currentKeyNumber + 3;
        arpeggioSequence[2] = currentKeyNumber + 5;
    }

    public static int yieldNextKeyNum(){
        return arpeggioSequence[arpeggioIndex % arpeggioSequence.length];
    }

    public static void incrementIndex(){
        arpeggioIndex++;
    }

    public static void resetFX(){
        arpeggioSequence = new int[3];
        arpeggioIndex = 0;
    }
}
