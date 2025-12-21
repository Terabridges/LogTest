package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.logging.PsiKitPinpointV2Logger;
import org.psilynx.psikit.core.rlog.RLOGServer;
import org.psilynx.psikit.core.rlog.RLOGWriter;
import org.psilynx.psikit.core.Logger;

import org.psilynx.psikit.ftc.PsiKitLinearOpMode;

@TeleOp(name="ConceptPsiKitLoggerLinear")
public class ConceptPsiKitLoggerLinear extends PsiKitLinearOpMode {

    // Default OFF: the driver can enable live streaming during INIT using gamepad1.
    // (PsiKit 0.1.0-beta2: attempting to stop RLOGServer on stop can crash the RC app.)
    private static final boolean DEFAULT_ENABLE_RLOG_SERVER = false;

    // Verification toggle:
    // - false (default): keep the safe workaround (skip Logger.end once server has ever been registered)
    // - true: force Logger.end even when server is/was active, to verify the upstream race fix
    private static final boolean FORCE_END_WITH_RLOG_SERVER = false;

    private static RLOGServer sharedRlogServer;
    private static boolean sharedRlogServerRegistered;

    private DcMotorEx motor;
    private final PsiKitPinpointV2Logger pinpointLogger = new PsiKitPinpointV2Logger();

    @Override
    public void runOpMode() {
        boolean loggerStarted = false;
        boolean enableRlogServer = DEFAULT_ENABLE_RLOG_SERVER;
        boolean rlogServerActive = false;
        try {
            // IMPORTANT: PsiKit wraps `hardwareMap` and `gamepad1/2` in `psiKitSetup()`.
            // Any `hardwareMap.get(...)` calls MUST happen after setup, otherwise devices won't be wrapped/logged.
            psiKitSetup();

            // Get motor *after* psiKitSetup so PsiKit can wrap/log it.
            // We request `DcMotorEx` for full runtime API (e.g. current).
            motor = hardwareMap.get(DcMotorEx.class, "motor0");

            // INIT menu: allow the driver to enable the live log server before START.
            boolean lastToggle = false;
            while (opModeInInit() && !isStopRequested()) {
                boolean toggle = gamepad1.a;
                if (toggle && !lastToggle) {
                    enableRlogServer = !enableRlogServer;
                }
                lastToggle = toggle;

                telemetry.addLine("PsiKit Logger Config");
                telemetry.addLine("- Press gamepad1 A to toggle log server");
                telemetry.addData("Log server requested", enableRlogServer ? "ON" : "OFF");
                telemetry.addData("Log server running", sharedRlogServerRegistered ? "YES" : "NO");
                if (sharedRlogServerRegistered && !enableRlogServer) {
                    telemetry.addLine("(Server is already running from a previous OpMode. Use DS 'Restart Robot' to stop it.)");
                }
                telemetry.update();
                idle();
            }

            if (isStopRequested()) {
                return;
            }

            waitForStart();
            if (isStopRequested()) {
                return;
            }

            // Set up logging *after* the init menu (so default is OFF unless explicitly enabled).
            if (enableRlogServer && !sharedRlogServerRegistered) {
                sharedRlogServer = new RLOGServer();
                Logger.addDataReceiver(sharedRlogServer);
                sharedRlogServerRegistered = true;
            }

            // If the server was ever registered in this RC app process, it remains a Logger receiver.
            // In PsiKit 0.1.0-beta2, calling Logger.end() after RLOGServer registration can crash the RC app.
            // So we treat the server as "active" for the lifetime of the app process.
            rlogServerActive = sharedRlogServerRegistered;

            String filename = this.getClass().getSimpleName() + "_log_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".rlog";
            if (!rlogServerActive) {
                Logger.addDataReceiver(new RLOGWriter(filename));
            }
            Logger.recordMetadata("some metadata", "string value");
            Logger.recordMetadata("rlogServerRequested", Boolean.toString(enableRlogServer));
            Logger.recordMetadata("rlogServerActive", Boolean.toString(rlogServerActive));
            Logger.start();
            loggerStarted = true;
            Logger.periodicAfterUser(0, 0);

            while (opModeIsActive() && !isStopRequested()) {
                double beforeUserStart = Logger.getTimestamp();
                Logger.periodicBeforeUser();
                double beforeUserEnd = Logger.getTimestamp();

                processHardwareInputs();
                pinpointLogger.logAll(hardwareMap);

                double joystickValue = -gamepad1.left_stick_y;
                motor.setPower(joystickValue);

                telemetry.addData("Joystick Left Y", gamepad1.left_stick_y);
                telemetry.update();

                Logger.recordOutput("OpMode/example", 2.0);

                double afterUserStart = Logger.getTimestamp();
                Logger.periodicAfterUser(
                        afterUserStart - beforeUserEnd,
                        beforeUserEnd - beforeUserStart
                );
            }
        } finally {
            // Ensure robot output is safe even if logging shutdown fails.
            try {
                if (motor != null) {
                    motor.setPower(0.0);
                }
            } catch (Throwable ignored) {
            }

            // PsiKit 0.1.0-beta2: if RLOGServer was enabled, Logger.end() can crash the RC app.
            // So we only end logging if the server has NEVER been registered in this RC app process.
            if (loggerStarted && (!rlogServerActive || FORCE_END_WITH_RLOG_SERVER)) {
                try {
                    Logger.end();
                } catch (Throwable ignored) {
                }

                // If we are testing the fixed shutdown path, allow future OpModes to restart the server.
                if (FORCE_END_WITH_RLOG_SERVER) {
                    sharedRlogServer = null;
                    sharedRlogServerRegistered = false;
                }
            }
        }
    }
}