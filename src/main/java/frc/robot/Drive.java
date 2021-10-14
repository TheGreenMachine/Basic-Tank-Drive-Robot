package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team1816.lib.hardware.RobotFactory;


public class Drive {

    private static RobotFactory factory = RobotFactory.getInstance();

    private static final String NAME = "drivetrain";

    private static Drive INSTANCE;

    private IMotorController leftMotor = factory.getMotor("drivetrain", "leftMain");
    private IMotorController leftFollower = factory.getMotor("drivetrain", "leftFollower", leftMotor);
    private IMotorController rightMotor = factory.getMotor("drivetrain", "rightMain");
    private IMotorController rightFollower = factory.getMotor("drivetrain", "rightFollower", rightMotor);

    public Drive() {
    }

    public void arcadeDrive(double throttle, double turn) {
        double percentThrottle = 0.5;
        leftMotor.set(ControlMode.PercentOutput, (throttle - turn)*percentThrottle);
        rightMotor.set(ControlMode.PercentOutput, (throttle + turn)*percentThrottle);
        leftFollower.set(ControlMode.PercentOutput, (throttle - turn)*percentThrottle);
        rightFollower.set(ControlMode.PercentOutput, (throttle - turn)*percentThrottle);

    }
    public synchronized void disableMotors() {
        leftMotor.set(ControlMode.Disabled, 0);
        rightMotor.set(ControlMode.Disabled, 0);
        leftFollower.set(ControlMode.Disabled, 0);
        rightFollower.set(ControlMode.Disabled, 0);

    }
}
