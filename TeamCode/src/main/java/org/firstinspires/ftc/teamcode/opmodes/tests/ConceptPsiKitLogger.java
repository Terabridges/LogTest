package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.logging.PsiKitDriverStationLogger;
import org.firstinspires.ftc.teamcode.logging.PsiKitPinpointV2Logger;
import org.psilynx.psikit.core.rlog.RLOGServer;
import org.psilynx.psikit.core.rlog.RLOGWriter;
import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.ftc.OpModeControls;
import org.psilynx.psikit.ftc.PsiKitOpMode;

@TeleOp(name="ConceptPsiKitLogger")
public class ConceptPsiKitLogger extends PsiKitOpMode {

    private final PsiKitDriverStationLogger driverStationLogger = new PsiKitDriverStationLogger();
    private final PsiKitPinpointV2Logger pinpointLogger = new PsiKitPinpointV2Logger();

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
        driverStationLogger.log(gamepad1, gamepad2);
        pinpointLogger.logAll(hardwareMap);

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
        driverStationLogger.log(gamepad1, gamepad2);
        pinpointLogger.logAll(hardwareMap);

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