package frc.robot;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team1816.lib.hardware.components.pcm.ISolenoid;
import com.team1816.lib.hardware.RobotFactory;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
//import sun.net.ext.ExtendedSocketOptions;

import java.awt.*;

/**
 * This is a basic robot program that operates a tank-drive style robot (motors
 * on each side) with the arcade drive joystick format - where pushing forwards
 * on the stick will control throttle and pushing left-right will turn the robot
 */
public class Robot extends TimedRobot {


    private static RobotFactory factory = RobotFactory.getInstance();
    /**
     * This creates a new Joystick that is plugged into port '0'. You can change the
     * port IDs in the NI Driver Station software. We will be using the Logitech
     * Extreme 3D Pro Flight Stick for this demo code
     */
    private Joystick joystick = new Joystick(0);
//
//    private ISolenoid solenoid0 = factory.getSolenoid("arm", "arm");
//    // note that just by creating an instance of a solenoid it automatically makes the compressor work
//

    private Drive drivetrain;
    private Shooter shooter;
    private Collector collector;

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
        drivetrain = new Drive();
        shooter = new Shooter();
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
//
//        // this is set up so that whenever the A button on a logitech joystick is held, solenoid0 will activate
//        if(joystick.getRawButton(2)){
//            solenoid0.set(true);
//        } else {
//            solenoid0.set(false);
//        }
//


        /**
         * This line actually drives the robot based on joystick input
         */
        drivetrain.arcadeDrive(throttle, turn);
        if(joystick.getTop()){
            shooter.spin(joystick.getTop());
        } else if(joystick.getTrigger()){
            shooter.collect(joystick.getTrigger());
        }

        //System.out.println("Throttle: " + throttle + "\tTurn: " + turn); // prints out the throttle and turn values
        //System.out.println("Left Power: " + leftMotor.get() + "\tRight Power" + rightMotor.get()); // prints the raw power sent to the left and right motor
    }

    @Override
    public void disabledInit() {
        drivetrain.disableMotors();
    }

    @Override
    public void testPeriodic() {
        drivetrain.arcadeDrive(.3, 0);
    }
}