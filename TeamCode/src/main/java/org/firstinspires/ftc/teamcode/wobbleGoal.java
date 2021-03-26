package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="Wobble Goal", group="Linear Opmode")
public class wobbleGoal extends LinearOpMode {
    //Sets the motor and servo as variables
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor motor = null;
    //Servo info
    static final double INCREMENT   = 0.01;     // amount to slew servo each CYCLE_MS cycle
    static final int    CYCLE_MS    =   50;     // period of each cycle
    static final double MAX_POS     =  1.0;     // Maximum rotational position
    static final double MIN_POS     =  0.0;     // Minimum rotational position
    //defining servo vars
    Servo   servo;
    double  position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
    boolean rampUp = true;

    @Override
    public void runOpMode() {

        motor = hardwareMap.get(DcMotor.class, "wobbleMotor");
        servo = hardwareMap.get(Servo.class, "servo");



        waitForStart();
        runtime.reset();

        double Power = 1;

        while (opModeIsActive()) {
            motor.setPower(0);
            if (gamepad1.dpad_up) {
                motor.setDirection(DcMotor.Direction.FORWARD);
                motor.setPower(Range.clip(Power, -0.25, 0.25));
            } else if (gamepad1.dpad_down) {
                motor.setDirection(DcMotor.Direction.REVERSE);
                motor.setPower(Range.clip(Power, -0.25, 0.25));
            } else if (gamepad1.dpad_left) {
                if (position < MAX_POS) {
                    position += INCREMENT;
                }
            } else if (gamepad1.dpad_right) {
                if (position > MIN_POS) {
                    position -= INCREMENT;
                }
            } else {
                motor.setPower(0);
            }
            telemetry.addData("Servo Position", "%5.2f", position);
            telemetry.update();
            servo.setPosition(position);
            sleep(CYCLE_MS);
            idle();
        }
    }
}