package com.idoshen.synth.utils;

import com.idoshen.synth.SynthControlContainer;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class Utils {
    public static void handleProcedure(Procedure procedure, boolean printStackTrace) {
        try {
            procedure.invoke();
        }
        catch (Exception e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
    }

    public static class ParameterHandling {
        public static final Robot PARAMETER_ROBOT;

        static {
            try {
                PARAMETER_ROBOT = new Robot();
            }
            catch (AWTException e) {
                throw new ExceptionInInitializerError("Cannot construct robot instance");
            }
        }

        public static void addParametersMouseListeners(Component component, SynthControlContainer container, int minValue, int maxValue, int step,
                                                       RefWrapper<Integer> parameter, Procedure onChangeProcedure) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
                            new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank_cursor");
                    component.setCursor(BLANK_CURSOR);
                    container.setMouseClickLocation(e.getLocationOnScreen());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    component.setCursor(Cursor.getDefaultCursor());
                }
            });
            component.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (container.getMouseClickLocation().y != e.getYOnScreen()) {
                        boolean mouseMovingUp = container.getMouseClickLocation().y - e.getYOnScreen() > 0;
                        if (mouseMovingUp && parameter.val < maxValue) {
                            parameter.val += step;
                        } else if (!mouseMovingUp && parameter.val > minValue) {
                            parameter.val -= step;
                        }
                        if (onChangeProcedure != null) {
                            handleProcedure(onChangeProcedure, true);
                        }
                        PARAMETER_ROBOT.mouseMove(container.getMouseClickLocation().x, container.getMouseClickLocation().y);
                    }
                }
            });
        }
    }

    public static class WindowDesign {
        public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK);
    }

    public static class Math {
        public static double offsetTone(double baseFrequency, double cent) { return baseFrequency * pow(2.0, cent / 1200.0); } // TODO: not responsive
        public static double offsetSemitone(double baseFrequency, double semitones) { return baseFrequency * pow(2.0, semitones / 12.0); }
        public static double frequencyToAngularFrequency(double frequency) { return 2 * PI * frequency; }
        public static double getKeyFrequency(int keyNumber) { return pow(root(2, 12), keyNumber - 49) * 440; }
        public static int frequencyToKeyNumber(double frequency) {
            return (int) java.lang.Math.round(12.0 * java.lang.Math.log(frequency / 440) / java.lang.Math.log(2) + 49);
        }
        public static double root(double number, double rootOrder) { return  pow(E, log(number) / rootOrder); }
    }
}
