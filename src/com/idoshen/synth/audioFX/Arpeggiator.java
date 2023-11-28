package com.idoshen.synth.audioFX;

import com.idoshen.synth.SynthControlContainer;
import com.idoshen.synth.Synthesizer;
import com.idoshen.synth.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Arpeggiator extends SynthControlContainer {

    private int[] arpeggioSequence;
    private int arpeggioIndex = 0;
    public Arpeggiator(Synthesizer synthesizer){
        super(synthesizer);
    }

    public void creatNewArpeggioSequence(){
        arpeggioSequence = new int[3];
        arpeggioIndex = 0;
        int currentKeyNumber = Utils.Math.frequencyToKeyNumber(Synthesizer.KEY_FREQUENCIES.get(Synthesizer.currentPressedKey));
        arpeggioSequence[0] = currentKeyNumber;
        arpeggioSequence[1] = currentKeyNumber + 3;
        arpeggioSequence[2] = currentKeyNumber + 5;
    }

    public int yieldNextKeyNum(){
        int nextKeyNum = arpeggioSequence[arpeggioIndex % arpeggioSequence.length];
        arpeggioIndex++;
        return nextKeyNum;
    }

    public void resetFX(){
        arpeggioSequence = new int[3];
        arpeggioIndex = 0;
    }
}
