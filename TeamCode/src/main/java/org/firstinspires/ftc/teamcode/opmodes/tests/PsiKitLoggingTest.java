package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.core.rlog.RLOGServer;
import org.psilynx.psikit.core.rlog.RLOGWriter;
import org.psilynx.psikit.ftc.HardwareMapWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;

//import org.littletonrobotics.junction.Logger;  // Assuming PsiKit uses a similar Logger class from AdvantageKit port; adjust package if needed

@TeleOp(name = "PsiKitLoggingTest", group = "Tests")
public class PsiKitLoggingTest extends OpMode {

    private DcMotorEx motor;

    @Override
    public void init() {
        // Map the motor from port 0 on the Control Hub (assuming configured as "motor0" in the robot configuration)
        motor = hardwareMap.get(DcMotorEx.class, "motor0");


        // If PsiKit requires explicit starting of the logger, uncomment and adjust as per docs
        Logger.addDataReceiver(new RLOGServer());
        //String filename = "log_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".rlog";
        //String filename = "log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".rlog";
        String filename = this.getClass().getSimpleName() + "_log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".rlog";
        Logger.addDataReceiver(new RLOGWriter(filename));
        // Pull with:  adb pull /sdcard/FIRST/PsiKit/log.rlog
    }
    @Override
    public void start(){
        Logger.start();
        Logger.periodicAfterUser(0, 0);

    }
    @Override
    public void loop() {
        double beforeUserStart = Logger.getTimestamp();
        Logger.periodicBeforeUser();
        double beforeUserEnd = Logger.getTimestamp();

        // Get joystick value from left stick Y-axis (invert if needed for direction)
        double joystickValue = -gamepad1.left_stick_y;  // Up on stick is negative, so invert for positive power forward

        // Set motor power based on joystick
        motor.setPower(joystickValue);

        // Get motor current
        double motorCurrent = motor.getCurrent(CurrentUnit.AMPS);

        // Log using PsiKit (assuming similar API to AdvantageKit)
        Logger.recordOutput("Joystick/LeftY", gamepad1.left_stick_y);
        Logger.recordOutput("Motor/Current", motorCurrent);

        // Optional: Add to telemetry for on-device viewing during testing
        telemetry.addData("Joystick Left Y", gamepad1.left_stick_y);
        telemetry.addData("Motor Current (Amps)", motorCurrent);
        telemetry.update();
        double afterUserStart = Logger.getTimestamp();
        Logger.periodicAfterUser(
                afterUserStart - beforeUserEnd,
                beforeUserEnd - beforeUserStart
        );
    }

    @Override
    public void stop() {
        // If PsiKit requires explicit stopping or flushing logs, add here
        Logger.end();
    }
}