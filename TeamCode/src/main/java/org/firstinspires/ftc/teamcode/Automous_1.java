/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */







package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Ultimate Goal game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = " Autonomous Testing 1 ", group = "Concept")
//@Disabled
public class Automous_1 extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    public static final String LABEL_SECOND_ELEMENT = "Single";
    private DcMotor right_Drive =null;
    private DcMotor Left_Drive =null;
    private DcMotor Arm =null;
    public Servo claw = null;




    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            " ATyOegL/////AAABmUhQT/JIOUluom3TiXFFDSMStTZGtJWE++LVReWvLAK6CobLktoj1VsM8h3SIcaMVvdJBg2dxRpY4Ua52dTb1gCdchH7QlUYLNPVVt/3zMyZEODIsKo8E6CYC9OFDq5oOUgH9iwdkT+fIQzfcHXU6iVfID0r/5KCWmAMqw0D7QteYU51W3uA330uvBCp09qs/zJvtDYlzsqgXxSSimiMP8cgY4jX268+Yr02HZPPrw65o7frwfNaQtEBoA4MZvu81imiuSEuMviQ45LnIlUBMWIAGfcjKpFyXf3qv2raTDhZzxXncAH1qhe2T4ivfjKzgCcuJxb+5xZUko6aoDeuKKpAGaDIGsBJZN5b2KUszuVr";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;






    @Override
    public void runOpMode() {

        right_Drive = hardwareMap.get(DcMotor.class, "right_drive");
        Left_Drive = hardwareMap.get(DcMotor.class, "left_drive");
        Arm = hardwareMap.get(DcMotor.class, "arm ");
        claw = hardwareMap.get(Servo.class, "claw");


        right_Drive.setDirection(DcMotor.Direction.FORWARD);
        Left_Drive.setDirection(DcMotor.Direction.REVERSE);

        // Left_Drive  = hardwareMap.get(DcMotor.class, "Left_drive");
        //Right_Drive = hardwareMap.get(DcMotor.class, "Right_drive");


        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 1.78 or 16/9).

            // Uncomment the following line if you want to adjust the magnification and/or the aspect ratio of the input images.
            tfod.setZoom(2.5, 1.78);
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        waitForStart();


        if (opModeIsActive()) {


            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    // step through the list of recognitions and display boundary info.
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        if (recognition.getLabel().equals(LABEL_FIRST_ELEMENT)) {
                            // go to target zone C
                            telemetry.addLine("Target_C ");
                            //right_Drive.setPower(1);
                            //Left_Drive.setPower(1);
                            // sleep(2000);
                            Target_C();
                            sleep(1000);


                        } else if (recognition.getLabel().equals((LABEL_SECOND_ELEMENT))) {
                            // the robot will go to target zone b
                            telemetry.addLine("Target_B");
                            Target_B();
                            tfod.shutdown();
                            sleep(1000);


                        } else {
                            // do something else or go to target A
                            telemetry.addLine("Target A ");
                            // TODO: 4/7/21 finich the autonoumous for target a by the end of the day
                            claw.setPosition(270);
                            sleep(500);
                            tfod.shutdown();
                            sleep(1000);
                            claw.setPosition(0);
                            sleep(3000);
                            arm("UP", 100);
                            Forward(40, .5);
                            sleep(1000);
                            resetEncoders();
                            Left(.6, .5);
                            sleep(530);//
                            resetEncoders();
                            sleep(100);
                            Forward(.8, .5);
                            sleep(50);
                            resetEncoders();
                            sleep(2000);
                            claw.setPosition(270);
                            sleep(1000);
                            arm("DOWN", 300);
                            resetEncoders();
                            sleep(20);
                            Reverse(.3, .5);
                            sleep(50);


                        }
                    }

                        /*telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                          recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());*/
                }
                telemetry.update();
            }
        }
        if (tfod != null) {
            tfod.shutdown();
        }
    }

    public void Forward (double Inches, double Speed) {

        double Diameter = 6.28;
        double EncoderTurns = 288;
        double DesiredPos = Inches * EncoderTurns / Diameter;
        resetEncoders();
        //right_Drive.setDirection(direction);
        //hLeft_Drive.setDirection(REVERSE);

        right_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Left_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        right_Drive.setTargetPosition((int) DesiredPos);
        Left_Drive.setTargetPosition((int) DesiredPos);

        right_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Left_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right_Drive.setPower(Speed);
        Left_Drive.setPower(Speed);

//

    }
    public void Reverse (double Inches, double Speed) {

        double Diameter = 6.28;
        double EncoderTurns = 288;
        double DesiredPos = Inches * EncoderTurns / Diameter;
        resetEncoders();
        //right_Drive.setDirection(direction);
        //Left_Drive.setDirection(direction);

        right_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Left_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        right_Drive.setTargetPosition((int) -DesiredPos);
        Left_Drive.setTargetPosition((int) -DesiredPos);

        right_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Left_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right_Drive.setPower(Speed);
        Left_Drive.setPower(Speed);

    }
    public void encoderTurn(double Inches, double Speed, int SleepTime,String Direction) {


        //12in = 90 degrees

        double Diameter = 11.21;
        double EncoderTurns = 288;
        double DesiredPos = Inches * EncoderTurns / Diameter;

        if (Direction == "RIGHT") {
            right_Drive.setTargetPosition((int) DesiredPos);
            Left_Drive.setTargetPosition((int) DesiredPos);

        } else if (Direction == "LEFT") {
            right_Drive.setTargetPosition((int) DesiredPos);
            Left_Drive.setTargetPosition((int) DesiredPos);
        } else {
            right_Drive.setTargetPosition(0);
            Left_Drive.setTargetPosition(0);

        }

        right_Drive.setPower(Speed);
        Left_Drive.setPower(Speed);


        right_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Left_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        sleep(SleepTime);

        resetEncoders();

    }
    public void Target_C(){
        //right_Drive.setPower(1);
        //Left_Drive.setPower(1);
        // sleep(2000);
        sleep(1000);
        claw.setPosition(0);
        sleep(3000);
        arm("UP",100);

        Forward(52,1);
        sleep(100);
        //pick up the wobble goal and drive forward  to Target C
        claw.setPosition(0);
        arm("DOWN",300);

        sleep(1800);

        Reverse(10,-1);
        sleep(1000);

        resetEncoders();
        // reverse to the launch zone
        sleep(2500);
        Reverse(-.87, 1);
        sleep(700);
        sleep(480);

        //90 100
        //Drive(-20,1);

        resetEncoders();

    }
    public void Target_B(){
        tfod.shutdown();
        sleep(1000);
        claw.setPosition(0);
        sleep(3000);
        arm("UP",100);
        Forward(40,.5);
        sleep(1000);
        resetEncoders();
        Left(.6,.5);
        sleep(530);//
        resetEncoders();
        sleep(100);
        Forward(.8,.5);
        sleep(80);
        resetEncoders();
        sleep(2000);
        claw.setPosition(270);
        sleep(1000);
        arm("DOWN",300);
        resetEncoders();
        sleep(20);
        Reverse(.3,.5);
        sleep(50);



    }
    public void Target_A(){
        sleep(1000);
        arm("UP",1000);
        sleep(100);
        Forward(40,.5 );


        sleep(1000);
        arm("DOWN",100);
        sleep(100);
        claw.setPosition(0);
        sleep(100);
        arm("UP",100);
        sleep(100);
        Reverse(4,1);
        resetEncoders();
        arm("DOWN",100);



    }

    public void RIGHT(double Inches , double Speed) {

        double Diameter = 6.28;
        double EncoderTurns = 288;
        double DesiredPos = Inches * EncoderTurns / Diameter;


        resetEncoders();
        //right_Drive.setDirection(direction);
        //hLeft_Drive.setDirection(REVERSE);

        right_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Left_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        right_Drive.setTargetPosition((int) DesiredPos);
        Left_Drive.setTargetPosition((int) -DesiredPos);

        right_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Left_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right_Drive.setPower(Speed);
        Left_Drive.setPower(Speed);

    }
    public void Left (double Inches , double Speed) {

        double Diameter = 6.28;
        double EncoderTurns = 288;
        double DesiredPos = Inches * EncoderTurns / Diameter;


        resetEncoders();
        //right_Drive.setDirection(direction);
        //hLeft_Drive.setDirection(REVERSE);

        right_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Left_Drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        right_Drive.setTargetPosition((int) -DesiredPos);
        Left_Drive.setTargetPosition((int) DesiredPos);

        right_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Left_Drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right_Drive.setPower(Speed);
        Left_Drive.setPower(Speed);

    }
    public void resetEncoders(){
        right_Drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Left_Drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    //public void Target_A(){
    // telemetry.addLine("Target A ");
    // Drive(11.8,1);
    //Drive(12,1);


    //}
    public void arm(String position, int SleepTime) {

        sleep(SleepTime);

        if (position == "UP") {
            Arm.setPower(-.5);
            sleep(300);

        } else if (position == "DOWN") {
            Arm.setPower(.5);
            sleep(400);
            Arm.setPower(0);

        } else {
            Arm.setPower(0);



        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
}
