package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.psilynx.psikit.core.rlog.RLOGServer;
import org.psilynx.psikit.core.rlog.RLOGWriter;
import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.core.LoggableInputs;

import org.firstinspires.ftc.teamcode.logging.AdvantageScopeJoystickInputs;
import org.psilynx.psikit.ftc.OpModeControls;
import org.psilynx.psikit.ftc.PsiKitOpMode;
import org.psilynx.psikit.ftc.wrappers.GamepadWrapper;

@TeleOp(name="ConceptPsiKitLogger")
public class ConceptPsiKitLogger extends PsiKitOpMode {

    private final AdvantageScopeJoystickInputs joystick0 = new AdvantageScopeJoystickInputs();
    private final AdvantageScopeJoystickInputs joystick1 = new AdvantageScopeJoystickInputs();

    /**
     * Returns a {@link LoggableInputs} view of {@code gamepad1} for PsiKit.
     * <p>
     * PsiKit wraps {@code gamepad1/2} in {@code psiKitSetup()}, but the FTC SDK can still provide
     * a plain {@code Gamepad} instance (or swap instances). Casting would then crash.
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
    public void psiKit_init() {
        Logger.addDataReceiver(new RLOGServer());

        String filename = this.getClass().getSimpleName()
                + "_log_"
                + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
                + ".rlog";
        Logger.addDataReceiver(new RLOGWriter(filename));

        Logger.recordMetadata("some metadata", "string value");
    }
    public void psiKit_init_loop() {
        // PsiKitOpMode currently does NOT call processHardwareInputs() during init.
        // That means OpModeControls.started/stopped never updates, and the OpMode can get stuck
        // in init (and appear stuck in stop). We manually run the periodic + input processing here.
        Logger.periodicBeforeUser();
        processHardwareInputs();
        Logger.processInputs("DriverStation/Gamepad1", loggableGamepad1());
        Logger.processInputs("DriverStation/Gamepad2", loggableGamepad2());

        joystick0.updateFrom(gamepad1);
        joystick1.updateFrom(gamepad2);
        // AdvantageScope's Joysticks tab looks for keys starting with "/DriverStation/JoystickN".
        Logger.processInputs("/DriverStation/Joystick0", joystick0);
        Logger.processInputs("/DriverStation/Joystick1", joystick1);

        // If STOP is pressed before START, force the init loop to exit cleanly.
        if (isStopRequested()) {
            // Kotlin `object` singletons are accessed from Java via `.INSTANCE`.
            OpModeControls.INSTANCE.setStopped(true);
            OpModeControls.INSTANCE.setStarted(true);
            Logger.processInputs("OpModeControls", OpModeControls.INSTANCE);
        }

        Logger.periodicAfterUser(0.0, 0.0);
        idle();
    }
    @Override
    public void psiKit_start() {
        // start logic here
    }
    @Override
    public void psiKit_loop() {
        // Gamepads are wrapped by PsiKit but not automatically logged; do it explicitly.
        Logger.processInputs("DriverStation/Gamepad1", loggableGamepad1());
        Logger.processInputs("DriverStation/Gamepad2", loggableGamepad2());

        joystick0.updateFrom(gamepad1);
        joystick1.updateFrom(gamepad2);
        Logger.processInputs("/DriverStation/Joystick0", joystick0);
        Logger.processInputs("/DriverStation/Joystick1", joystick1);

        Logger.recordOutput("Joystick/LeftY", gamepad1.left_stick_y);

        Logger.recordOutput("OpMode/example", 2.0);
        // example
        telemetry.addData("Joystick Left Y", gamepad1.left_stick_y);
        telemetry.update();

    }
    @Override
    public void psiKit_stop() {
        // end logic goes here
    }

}