package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.psilynx.psikit.core.rlog.RLOGServer;
import org.psilynx.psikit.core.rlog.RLOGWriter;
import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.core.LoggableInputs;
import org.psilynx.psikit.ftc.wrappers.GamepadWrapper;

import org.psilynx.psikit.ftc.PsiKitLinearOpMode;

@TeleOp(name="ConceptPsiKitLoggerLinear")
public class ConceptPsiKitLoggerLinear extends PsiKitLinearOpMode {

    private DcMotorEx motor;

    /**
     * Returns a {@link LoggableInputs} view of {@code gamepad1} for PsiKit.
     * <p>
     * PsiKit's {@code psiKitSetup()} attempts to replace {@code gamepad1/2} with PsiKit wrappers,
     * but the FTC SDK can still provide a plain {@code Gamepad} instance at runtime (or swap
     * instances). Casting would then crash.
     * <p>
     * This method avoids per-loop allocations by reusing the wrapper when one is already present,
     * and only wrapping when necessary.
     */
    private LoggableInputs loggableGamepad1() {
        if (gamepad1 instanceof LoggableInputs) {
            return (LoggableInputs) gamepad1;
        }
        return new GamepadWrapper(gamepad1);
    }

    /**
     * Same as {@link #loggableGamepad1()}, but for {@code gamepad2}.
     */
    private LoggableInputs loggableGamepad2() {
        if (gamepad2 instanceof LoggableInputs) {
            return (LoggableInputs) gamepad2;
        }
        return new GamepadWrapper(gamepad2);
    }

    @Override
    public void runOpMode() {
        // IMPORTANT: PsiKit wraps `hardwareMap` and `gamepad1/2` in `psiKitSetup()`.
        // Any `hardwareMap.get(...)` calls MUST happen after setup, otherwise devices won't be wrapped/logged.
        psiKitSetup();

        // Get motor *after* psiKitSetup so PsiKit can wrap/log it.
        // PsiKit's HardwareMap wrapper currently registers a motor wrapper for `DcMotor` (not `DcMotorEx`).
        // So we request both:
        // - `DcMotorEx` for full runtime API (e.g. current)
        // - `DcMotor` to ensure PsiKit registers the wrapper for automatic logging.
        motor = hardwareMap.get(DcMotorEx.class, "motor0");
        hardwareMap.get(DcMotor.class, "motor0");

        Logger.addDataReceiver(new RLOGServer());
        String filename = this.getClass().getSimpleName() + "_log_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".rlog";
        Logger.addDataReceiver(new RLOGWriter(filename));
        Logger.recordMetadata("some metadata", "string value");
        Logger.start(); // Start logging! No more data receivers, replay sources, or metadata values may be added.
        Logger.periodicAfterUser(0, 0);

        while(!getPsiKitIsStarted()){
            Logger.periodicBeforeUser();

            processHardwareInputs();
            // PsiKit wraps `gamepad1/2` but does not currently call `Logger.processInputs(...)` for them.
            // So do it explicitly to get gamepad data into the log.
            Logger.processInputs("DriverStation/Gamepad1", loggableGamepad1());
            Logger.processInputs("DriverStation/Gamepad2", loggableGamepad2());
            // this MUST come before any logic

            telemetry.addData("PsiKit hardwareMap", hardwareMap.getClass().getSimpleName());
            telemetry.addData("PsiKit gamepad1 wrapped", gamepad1 instanceof LoggableInputs);
            telemetry.addData("PsiKit gamepad2 wrapped", gamepad2 instanceof LoggableInputs);
            telemetry.update();

         /*

          Init logic goes here

         */

            Logger.periodicAfterUser(0.0, 0.0);
            // logging these timestamps is completely optional
        }

        // alternately the waitForStart() function works as expected.

        while(!getPsiKitIsStopRequested()) {

            double beforeUserStart = Logger.getTimestamp();
            Logger.periodicBeforeUser();
            double beforeUserEnd = Logger.getTimestamp();

            processHardwareInputs();
            Logger.processInputs("DriverStation/Gamepad1", loggableGamepad1());
            Logger.processInputs("DriverStation/Gamepad2", loggableGamepad2());
            // this MUST come before any logic

         /*

          OpMode logic goes here

         */
            double joystickValue = -gamepad1.left_stick_y;  // Up on stick is negative, so invert for positive power forward
            motor.setPower(joystickValue);
            double motorCurrent = motor.getCurrent(CurrentUnit.AMPS);

            telemetry.addData("Joystick Left Y", gamepad1.left_stick_y);
            telemetry.addData("Motor Current (Amps)", motorCurrent);
            telemetry.update();

            Logger.recordOutput("OpMode/example", 2.0);
            // example


            double afterUserStart = Logger.getTimestamp();
            Logger.periodicAfterUser(
                    afterUserStart - beforeUserEnd,
                    beforeUserEnd - beforeUserStart
            );
            // alternetly, keep track of how long some things are taking. up to
            // you on what you want to do
        }
        Logger.end();
    }
}