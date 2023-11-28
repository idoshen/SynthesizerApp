package com.idoshen.synth;

import com.idoshen.synth.audioFX.ADSR;
import com.idoshen.synth.utils.Utils;
import javax.swing.*;
import java.awt.*;

public class ADSRWaveViewer extends JPanel {
    private ADSR adsr;

    public ADSRWaveViewer(ADSR adsr) {
        this.adsr = adsr;
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setBounds(310, 315, 310, 100);
        setSize(310, 100);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        final int PADDING = 10;
        final int OVAL_RADIUS = 5;
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics;

        graphics2D.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING);
        graphics2D.drawLine(PADDING, PADDING, PADDING, getHeight() - PADDING);

        int attackTime = (int)((adsr.getAttackTimeValue() / 3) * 93);
        int decayTime = (int)((adsr.getDecayTimeValue() / 3) * 93);
        int sustainLevel = (int)((getHeight() - 2 * PADDING) - adsr.getSustainLevelValue() * (getHeight() - 2 * PADDING));
        int releaseTime = (int)((adsr.getReleaseTimeValue() / 3) * 93);
        int totalTime = attackTime + decayTime + releaseTime;

        graphics2D.drawOval(PADDING - OVAL_RADIUS, getHeight() - PADDING - OVAL_RADIUS, 2 * OVAL_RADIUS, 2 * OVAL_RADIUS);
        graphics2D.drawOval(attackTime + PADDING - OVAL_RADIUS,  PADDING - OVAL_RADIUS, 2 * OVAL_RADIUS, 2 * OVAL_RADIUS);
        graphics2D.drawOval(attackTime + decayTime + PADDING - OVAL_RADIUS, sustainLevel + PADDING - OVAL_RADIUS, 2 * OVAL_RADIUS, 2 * OVAL_RADIUS);
        graphics2D.drawOval(totalTime + PADDING - OVAL_RADIUS, getHeight() - PADDING - OVAL_RADIUS, 2 * OVAL_RADIUS, 2 * OVAL_RADIUS);

        // Plot attack phase
        graphics2D.drawLine(PADDING, getHeight() - PADDING, attackTime + PADDING, PADDING);

        // Plot decay phase
        graphics2D.drawLine(attackTime + PADDING, PADDING, attackTime + decayTime + PADDING, sustainLevel + PADDING);

        // Plot release phase
        graphics2D.drawLine(attackTime + decayTime + PADDING, sustainLevel + PADDING, totalTime + PADDING, getHeight() - PADDING);
    }

}
