package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Interface", group="Linear Opmode")
//@Disabled
public class Intake extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftIn = null;
    private DcMotor rightIn = null;


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        leftIn = hardwareMap.get(DcMotor.class, "Sone");
        rightIn = hardwareMap.get(DcMotor.class, "Stwo");


        waitForStart();
        runtime.reset();

        double power = 0.25;

        while (opModeIsActive()) {

            leftIn.setPower(Range.clip(power, 0, 1.0));
            rightIn.setPower(-Range.clip(power, 0, 1.0));

            if (gamepad1.b) {
                leftIn.setDirection(DcMotor.Direction.REVERSE);
                rightIn.setDirection(DcMotor.Direction.REVERSE);
            } else {
                leftIn.setDirection(DcMotor.Direction.FORWARD);
                rightIn.setDirection(DcMotor.Direction.FORWARD);
            }

        }
    }
}