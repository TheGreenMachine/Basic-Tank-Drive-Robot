package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team1816.lib.hardware.RobotFactory;


public class Drive {

    private static final RobotFactory factory = RobotFactory.getInstance();

    private static final String NAME = "drivetrain";

    private static Drive INSTANCE;

    private final IMotorController leftMotor = factory.getMotor(NAME, "leftMain");
    private final IMotorController rightMotor = factory.getMotor(NAME, "rightMain");
    private final IMotorController leftFollower = factory.getMotor(NAME, "leftFollower", leftMotor);
    private final IMotorController rightFollower = factory.getMotor(NAME, "leftFollower", rightMotor);

    public static Drive getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Drive();
        }
        return INSTANCE;
    }

    public Drive() {
        leftMotor.setInverted(true);
        rightMotor.setInverted(false);

    }

    public void arcadeDrive(double throttle, double turn) {
        leftMotor.set(ControlMode.PercentOutput, throttle + turn);
        rightMotor.set(ControlMode.PercentOutput, throttle - turn);
    }
}
