package org.firstinspires.ftc.teamcode.logging;

import org.psilynx.psikit.core.LoggableInputs;
import org.psilynx.psikit.core.LogTable;

/**
 * AdvantageScope-compatible Pose2d encoding.
 *
 * <p>AdvantageScope recognizes a Pose2d when the following keys exist under known paths:
 * <ul>
 *   <li>{@code translation/x} (meters)</li>
 *   <li>{@code translation/y} (meters)</li>
 *   <li>{@code rotation/value} (radians)</li>
 * </ul>
 */
public final class AdvantageScopePose2dInputs implements LoggableInputs {

    private double xMeters;
    private double yMeters;
    private double headingRad;

    public void set(double xMeters, double yMeters, double headingRad) {
        this.xMeters = xMeters;
        this.yMeters = yMeters;
        this.headingRad = headingRad;
    }

    @Override
    public void toLog(LogTable table) {
        table.put("translation/x", xMeters);
        table.put("translation/y", yMeters);
        table.put("rotation/value", headingRad);
    }

    @Override
    public void fromLog(LogTable table) {
        xMeters = table.get("translation/x", 0.0);
        yMeters = table.get("translation/y", 0.0);
        headingRad = table.get("rotation/value", 0.0);
    }
}
