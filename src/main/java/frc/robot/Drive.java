package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team1816.lib.hardware.RobotFactory;


public class Drive {

    private static RobotFactory factory = RobotFactory.getInstance();

    private static final String NAME = "drivetrain";

    private static Drive INSTANCE;

    private IMotorController leftMotor = factory.getMotor(NAME, "leftMain");
    private IMotorController rightMotor = factory.getMotor(NAME, "rightMain");
    private IMotorController leftFollower = factory.getMotor(NAME, "leftFollower",leftMotor);
    private IMotorController rightFollower = factory.getMotor(NAME,"rightFollower",rightMotor);


    public Drive() {
        leftMotor.setInverted(true);
        rightMotor.setInverted(false);

    }

    public void arcadeDrive(double throttle, double turn) {
        leftMotor.set(ControlMode.PercentOutput, throttle + turn);
        rightMotor.set(ControlMode.PercentOutput, throttle - turn);
    }
}
