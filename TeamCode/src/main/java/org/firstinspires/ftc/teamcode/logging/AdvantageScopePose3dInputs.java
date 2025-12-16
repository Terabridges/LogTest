package org.firstinspires.ftc.teamcode.logging;

import org.psilynx.psikit.core.LoggableInputs;
import org.psilynx.psikit.core.LogTable;

/**
 * AdvantageScope-compatible Pose3d encoding.
 *
 * <p>AdvantageScope recognizes a Pose3d when the following keys exist:
 * <ul>
 *   <li>{@code translation/x,y,z} (meters)</li>
 *   <li>{@code rotation/q/w,x,y,z} (unit quaternion)</li>
 * </ul>
 */
public final class AdvantageScopePose3dInputs implements LoggableInputs {

    private double xMeters;
    private double yMeters;
    private double zMeters;

    // Quaternion components (w, x, y, z)
    private double qw;
    private double qx;
    private double qy;
    private double qz;

    public void setTranslation(double xMeters, double yMeters, double zMeters) {
        this.xMeters = xMeters;
        this.yMeters = yMeters;
        this.zMeters = zMeters;
    }

    public void setQuaternion(double qw, double qx, double qy, double qz) {
        this.qw = qw;
        this.qx = qx;
        this.qy = qy;
        this.qz = qz;
    }

    @Override
    public void toLog(LogTable table) {
        table.put("translation/x", xMeters);
        table.put("translation/y", yMeters);
        table.put("translation/z", zMeters);

        table.put("rotation/q/w", qw);
        table.put("rotation/q/x", qx);
        table.put("rotation/q/y", qy);
        table.put("rotation/q/z", qz);
    }

    @Override
    public void fromLog(LogTable table) {
        xMeters = table.get("translation/x", 0.0);
        yMeters = table.get("translation/y", 0.0);
        zMeters = table.get("translation/z", 0.0);

        qw = table.get("rotation/q/w", 1.0);
        qx = table.get("rotation/q/x", 0.0);
        qy = table.get("rotation/q/y", 0.0);
        qz = table.get("rotation/q/z", 0.0);
    }
}
