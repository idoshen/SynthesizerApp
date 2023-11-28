package com.idoshen.synth;

import com.idoshen.synth.utils.Utils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;

import java.util.function.Supplier;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

class AudioThread extends Thread {
    static final int BUFFER_SIZE = 256;
    static final int BUFFER_COUNT = 8;

    private Supplier<short[]> bufferSupplier;
    private final int[] buffers = new int[BUFFER_COUNT];

    // pointer values (openal is written in c).
    private final long device = alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER));
    private final long context = alcCreateContext(device, new int[1]);
    private final int source;

    private int bufferIndex; // index of the current buffer.
    private boolean closed;
    private boolean running;

    AudioThread(Supplier<short[]> bufferSupplier) {
        this.bufferSupplier = bufferSupplier;
        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
        source = alGenSources();

        for (int i = 0; i < BUFFER_COUNT; i++) {
            bufferSamples(new short[0]);
        }

        alSourcePlay(source);
        catchInternalExeptions();
        start();
    }

    boolean isRunning() {
        return running;
    }

    @Override
    public synchronized void run() {
        while (!closed) {
//            System.out.println("AudioThread");
            while (!running) {
                Utils.handleProcedure(this::wait, false); // while not running, the thread is waiting. It is not a busy waiting.
            }
            int processedBufs = alGetSourcei(source, AL_BUFFERS_PROCESSED);
            for (int i = 0; i < processedBufs; ++i) {
                short[] samples = bufferSupplier.get();
                if(samples == null) {
                    running = false;
                    break;
                }
                alDeleteBuffers(alSourceUnqueueBuffers(source));
                buffers[bufferIndex] = alGenBuffers();
                bufferSamples(samples);
            }
            if (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING) {
                alSourcePlay(source);
            }
            catchInternalExeptions();
        }
        alDeleteSources(source);
        alDeleteBuffers(buffers);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    synchronized void triggerPlayback() {
        running = true;
        notify(); // notify the thread to get out of its waiting state.
    }

    void close() {
        closed = true;
        triggerPlayback(); // break out of the loop.
    }

    private void  bufferSamples(short[] samples) {
        int buf = buffers[bufferIndex++];
        alBufferData(buf, AL_FORMAT_MONO16, samples, Synthesizer.AudioInfo.SAMPLE_RATE);
        alSourceQueueBuffers(source, buf);
        bufferIndex %= BUFFER_COUNT; // bufferIndex will go from 0 to 7 and then restarts (as BUFFER_COUNT is 8).
    }

    private void catchInternalExeptions() {
        int err = alcGetError(device);
        if (err !=  ALC_NO_ERROR) {
            throw new OpenALException(err);
        }
    }
}
