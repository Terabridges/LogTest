package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.psilynx.psikit.core.rlog.RLOGServer;
import org.psilynx.psikit.core.rlog.RLOGWriter;
import org.psilynx.psikit.core.Logger;

import org.psilynx.psikit.ftc.PsiKitOpMode;

@TeleOp(name="ConceptPsiKitLogger")
public class ConceptPsiKitLogger extends PsiKitOpMode {
    @Override
    public void psiKit_init() {

        Logger.addDataReceiver(new RLOGServer());
        Logger.addDataReceiver(new RLOGWriter("log2.rlog"));
    }
    public void psiKit_init_loop() {
        /*

         init loop logic goes here

        */
    }
    @Override
    public void psiKit_start() {
        // start logic here
    }
    @Override
    public void psiKit_loop() {

        /*

         OpMode logic goes here

        */
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