package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.psilynx.psikit.core.rlog.RLOGServer;
import org.psilynx.psikit.core.rlog.RLOGWriter;
import org.psilynx.psikit.core.Logger;

import org.psilynx.psikit.ftc.PsiKitLinearOpMode;

@TeleOp(name="ConceptPsiKitLoggerLinear")
public class ConceptPsiKitLoggerLinear extends PsiKitLinearOpMode {

    public Gamepad currentGamepad1;
    private DcMotorEx motor;

    @Override
    public void runOpMode() {
        //Robot robot = new Robot(hardwareMap, telemetry, gamepad1, gamepad2);
        currentGamepad1 = new Gamepad();
        motor = hardwareMap.get(DcMotorEx.class, "motor0");
        psiKitSetup();
        Logger.addDataReceiver(new RLOGServer());
        String filename = this.getClass().getSimpleName() + "_log_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".rlog";
        Logger.addDataReceiver(new RLOGWriter(filename));
        Logger.recordMetadata("some metadata", "string value");
        Logger.start(); // Start logging! No more data receivers, replay sources, or metadata values may be added.
        Logger.periodicAfterUser(0, 0);

        while(!getPsiKitIsStarted()){
            Logger.periodicBeforeUser();

            processHardwareInputs();
            // this MUST come before any logic

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