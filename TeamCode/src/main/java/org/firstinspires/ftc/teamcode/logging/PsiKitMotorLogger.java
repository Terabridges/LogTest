package org.firstinspires.ftc.teamcode.logging;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.psilynx.psikit.core.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Minimal motor logging via PsiKit outputs.
 *
 * <p>We use {@link Logger#recordOutput} (not auto-wrappers) so we can log values like motor
 * current that aren't necessarily captured by PsiKit's FTC wrappers.
 */
public final class PsiKitMotorLogger {

    private final List<NamedMotor> cachedMotors = new ArrayList<>();
    private boolean cached;

    /**
     * Logs a small, high-signal set of fields for a motor.
     */
    public void log(String motorName, DcMotorEx motor) {
        logMotor(motorName, motor);
    }

    /**
     * Dynamically logs all configured motors found in {@link HardwareMap}.
     *
     * <p>This avoids having to name motors in your OpMode. It does <b>not</b> attempt to infer
     * which motors you "used" this loop; it logs motors that exist in the configuration.
     *
     * <p>Call after {@code psiKitSetup()} so any PsiKit wrapping has already happened.
     */
    public void logAll(HardwareMap hardwareMap) {
        if (!cached) {
            cacheMotors(hardwareMap);
            cached = true;
        }

        for (NamedMotor named : cachedMotors) {
            logMotor(named.name, named.motor);
        }
    }

    private void cacheMotors(HardwareMap hardwareMap) {
        cachedMotors.clear();

        // FTC SDK provides getAll(...) for each device class; this yields device instances.
        // We then ask hardwareMap for all names bound to that instance.
        List<DcMotor> motors = hardwareMap.getAll(DcMotor.class);
        for (DcMotor dcMotor : motors) {
            DcMotorEx motorEx = (dcMotor instanceof DcMotorEx) ? (DcMotorEx) dcMotor : null;
            if (motorEx == null) {
                continue;
            }

            String chosenName = firstNameOrFallback(hardwareMap, dcMotor, "motor");
            cachedMotors.add(new NamedMotor(chosenName, motorEx));
        }
    }

    private static String firstNameOrFallback(HardwareMap hardwareMap, HardwareDevice device, String fallbackPrefix) {
        try {
            Set<String> names = hardwareMap.getNamesOf(device);
            if (names != null && !names.isEmpty()) {
                // Deterministic-ish choice (Set order is not guaranteed, but stable enough for our use).
                return names.iterator().next();
            }
        } catch (Throwable ignored) {
            // Some SDK variants may not support getNamesOf; fall back.
        }
        return fallbackPrefix;
    }

    private static void logMotor(String motorName, DcMotorEx motor) {
        String base = "Motors/" + motorName + "/";

        // Commonly useful for debugging and controls tuning.
        Logger.recordOutput(base + "Power", motor.getPower());
        Logger.recordOutput(base + "PositionTicks", motor.getCurrentPosition());
        Logger.recordOutput(base + "TargetPositionTicks", motor.getTargetPosition());

        // DcMotorEx extras.
        Logger.recordOutput(base + "VelocityTicksPerSec", motor.getVelocity());
        Logger.recordOutput(base + "CurrentAmps", motor.getCurrent(CurrentUnit.AMPS));

        // Represent booleans as 0/1 for maximum viewer compatibility.
        Logger.recordOutput(base + "IsOverCurrent", motor.isOverCurrent() ? 1.0 : 0.0);
    }

    private static final class NamedMotor {
        private final String name;
        private final DcMotorEx motor;

        private NamedMotor(String name, DcMotorEx motor) {
            this.name = name;
            this.motor = motor;
        }
    }
}
