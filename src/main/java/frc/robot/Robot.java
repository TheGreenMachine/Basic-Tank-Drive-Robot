package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import java.awt.*;

/**
 * This is a basic robot program that operates a tank-drive style robot (motors
 * on each side) with the arcade drive joystick format - where pushing forwards
 * on the stick will control throttle and pushing left-right will turn the robot
 */
public class Robot extends TimedRobot {
    /**
     * This creates a new Joystick that is plugged into port '0'. You can change the
     * port IDs in the NI Driver Station software. We will be using the Logitech
     * Extreme 3D Pro Flight Stick for this demo code
     */
    private Joystick joystick = new Joystick(0);


    /**
     * PWMVictorSPX is the class that represents a Victor SPX motor controller. This
     * code defines two motor controllers plugged into port 0 and 1 of the roboRIO's
     * PWM port, where the left motor controller is port 0 and the right is port 1
     */
    private PWMVictorSPX leftMotor = new PWMVictorSPX(0);
    private PWMVictorSPX rightMotor = new PWMVictorSPX(1);
    // the module number corresponds to the CanID of the PCM  - ex: ours is 8
    private Solenoid solenoid0 = new Solenoid(8,0);
    // note that just by creating an instance of a solenoid it automatically makes the compressor work

    // this solenoid will be used as a catch (press once = on until you press it again)
    private Solenoid solenoid1 = new Solenoid(8,1);
    // used to turn the latch on or off
    boolean solenoidLatched = false;



    /**
     * DifferentialDrive is a very simple class that operates a tank drive robot's
     * drivetrain. This creates a new DifferentialDrive system using your left and
     * right motor controllers
     */
    DifferentialDrive drivetrain = new DifferentialDrive(leftMotor, rightMotor);

    /**
     * Here we will make two doubles(decimal numbers) to represent our robot's
     * throttle and turn values. These will be values from -1 to 1
     */
    private double throttle;
    private double turn;

    /**
     * robotInit is a method that runs once whenever the roboRIO is booted. We'll
     * use it to setup some basic settings for our motor controllers
     */
    @Override
    public void robotInit() {
        /**
         * This sets one side to run the opposite direction of each other. You will
         * change the true / false values depending on how your robot is set up and
         * wired
         */
        leftMotor.setInverted(true);
        rightMotor.setInverted(false);

        /*
         * WPI drivetrain classes defaultly assume left and right are opposite. call
         * this so we can apply positive values to both sides when moving forward. DO
         * NOT CHANGE THIS LINE
         */
        drivetrain.setRightSideInverted(true);
    }

    /**
     * teleopPeriodic is a method that repeatedly loops/runs while your robot is
     * running in teleoperated mode. We'll use it to continuously check for joystick
     * input and run our drivetrain
     */
    @Override
    public void teleopPeriodic() {
        /**
         * Here we set our throttle and turn values to the value of the joystick's axes,
         * axis 2 is forward-back and axis 1 is left-right
         */
        throttle = joystick.getRawAxis(2);
        turn = joystick.getRawAxis(1);

        /**
         * These if statements create a 'deadzone', that is, within 10% movement of the
         * joystick's full movement axis, we will ignore any signal, this makes it less
         * easy to move the robot by accident
         */
        if (Math.abs(throttle) < 0.10) {
            throttle = 0;
        }
        if (Math.abs(turn) < 0.10) {
            turn = 0;
        }

        // this is set up so that whenever the A button on a logitech joystick is held, solenoid0 will activate
        if(joystick.getRawButton(2)){
            solenoid0.set(true);
        } else {
            solenoid0.set(false);
        }

        // this is set up so that whenever the logitech B button is pressed, solenoid1 will activate until pressed again
        if(joystick.getRawButtonPressed(3)){
            solenoidLatched = !solenoidLatched;
            System.out.println(" aaa ");
        }
        solenoid1.set(solenoidLatched);



        /**
         * This line actually drives the robot based on joystick input
         */
        drivetrain.arcadeDrive(throttle, turn);

        //System.out.println("Throttle: " + throttle + "\tTurn: " + turn); // prints out the throttle and turn values
        //System.out.println("Left Power: " + leftMotor.get() + "\tRight Power" + rightMotor.get()); // prints the raw power sent to the left and right motor
    }
}
