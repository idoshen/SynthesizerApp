package com.idoshen.synth;

import javax.swing.*;
import java.awt.*;

public class SynthControlContainer extends JPanel {
    private Synthesizer synthesizer;
    protected Point mouseClickLocation;
    protected boolean on;

    public SynthControlContainer(Synthesizer synthesizer) { this.synthesizer = synthesizer; }

    public Point getMouseClickLocation()
    {
        return mouseClickLocation;
    }

    public void setMouseClickLocation(Point mouseClickLocation)
    {
        this.mouseClickLocation = mouseClickLocation;
    }

    @Override
    public Component add(Component component) {
        component.addKeyListener(synthesizer.getKeyAdapter());
        return super.add(component);
    }

    @Override
    public Component add(Component component, int index) {
        component.addKeyListener(synthesizer.getKeyAdapter());
        return super.add(component, index);
    }

    @Override
    public Component add(String name, Component component) {
        component.addKeyListener(synthesizer.getKeyAdapter());
        return super.add(name, component);
    }

    @Override
    public void add(Component component, Object constraints) {
        component.addKeyListener(synthesizer.getKeyAdapter());
        super.add(component, constraints);
    }

    @Override
    public void add(Component component, Object constraints, int index) {
        component.addKeyListener(synthesizer.getKeyAdapter());
        super.add(component, constraints, index);
    }
}
