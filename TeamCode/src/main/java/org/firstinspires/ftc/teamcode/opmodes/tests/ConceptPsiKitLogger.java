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

    // Default OFF: the driver can enable live streaming during INIT using gamepad1.
    private static final boolean DEFAULT_ENABLE_RLOG_SERVER = false;

    private static RLOGServer sharedRlogServer;
    private static boolean sharedRlogServerRegistered;

    private boolean enableRlogServer = DEFAULT_ENABLE_RLOG_SERVER;
    private boolean initMenuConfigured;
    private boolean lastToggle;

    private final PsiKitDriverStationLogger driverStationLogger = new PsiKitDriverStationLogger();
    private final PsiKitPinpointV2Logger pinpointLogger = new PsiKitPinpointV2Logger();

    @Override
    public void psiKit_init() {
        // If the server was ever registered in this RC app process, it remains a Logger receiver.
        // In PsiKit 0.1.0-beta2, calling Logger.end() after RLOGServer registration can crash the RC app.
        // To avoid leaking multiple writers we can't close, only create an RLOGWriter when the server
        // has never been registered.
        if (!sharedRlogServerRegistered) {
            String filename = this.getClass().getSimpleName()
                    + "_log_"
                    + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
                    + ".rlog";
            Logger.addDataReceiver(new RLOGWriter(filename));
        }

        Logger.recordMetadata("some metadata", "string value");
    }
    public void psiKit_init_loop() {
        // Simple init menu (default OFF). Toggle with gamepad1 A before START.
        boolean toggle = gamepad1.a;
        if (toggle && !lastToggle) {
            enableRlogServer = !enableRlogServer;
            initMenuConfigured = true;
        }
        lastToggle = toggle;

        if (enableRlogServer && !sharedRlogServerRegistered) {
            // Add server receiver during INIT (before Logger.start happens in the base class).
            sharedRlogServer = new RLOGServer();
            Logger.addDataReceiver(sharedRlogServer);
            sharedRlogServerRegistered = true;
            Logger.recordMetadata("rlogServerRequested", "true");
            Logger.recordMetadata("rlogServerActive", "true");
        } else if (!initMenuConfigured) {
            // Record once so logs show that server was left at default.
            Logger.recordMetadata("rlogServerRequested", "false");
            Logger.recordMetadata("rlogServerActive", Boolean.toString(sharedRlogServerRegistered));
            initMenuConfigured = true;
        }

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

        telemetry.addLine("PsiKit Logger Config");
        telemetry.addLine("- Press gamepad1 A to toggle log server");
        telemetry.addData("Log server requested", enableRlogServer ? "ON" : "OFF");
        telemetry.addData("Log server running", sharedRlogServerRegistered ? "YES" : "NO");
        if (sharedRlogServerRegistered && !enableRlogServer) {
            telemetry.addLine("(Server already running from a previous OpMode. Use DS 'Restart Robot' to stop it.)");
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
        // PsiKit 0.1.0-beta2: if RLOGServer is enabled, Logger.end() can crash the RC app.
        // Only end logging when server has NEVER been registered in this RC app process.
        if (!sharedRlogServerRegistered) {
            try {
                Logger.end();
            } catch (Throwable ignored) {
            }
        }
    }

}