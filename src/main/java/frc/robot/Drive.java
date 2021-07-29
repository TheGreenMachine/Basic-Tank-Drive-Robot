package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team1816.lib.hardware.RobotFactory;
import com.team1816.lib.hardware.components.pcm.ISolenoid;

public class Drive {

    private static RobotFactory factory = RobotFactory.getInstance();

    private static final String NAME = "drivetrain";

    private static Drive INSTANCE;

    private IMotorController leftMotor = factory.getMotor("drivetrain", "leftMain");
    private IMotorController rightMotor = factory.getMotor("drivetrain", "rightMain");
    // the module number corresponds to the CanID of the PCM  - ex: ours is 8

    public Drive() {
        leftMotor.setInverted(true);
        rightMotor.setInverted(false);

    }

    public void arcadeDrive(double throttle, double turn) {
        leftMotor.set(ControlMode.PercentOutput, throttle + turn);
        rightMotor.set(ControlMode.PercentOutput, throttle - turn);

    }
}
