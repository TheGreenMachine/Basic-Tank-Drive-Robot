package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team1816.lib.hardware.RobotFactory;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

public class Canon {
    private static RobotFactory factory = RobotFactory.getInstance();
    private static final String NAME = "cannon";


    private Solenoid solenoid;
    private IMotorController mainShooter  = factory.getMotor(NAME, "mainShooter");
    private IMotorController mainFollower  = factory.getMotor(NAME, "mainFollower", mainShooter);

    private static Canon INSTANCE;
    private  double revVal;


    public static Canon getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Canon();
        }
        return INSTANCE;
    }
    public Canon (){}


    public Canon( double val){
        solenoid = new Solenoid(0);
        solenoid.set(false);
        revVal = val;

    }



    public void revUp(boolean Revving){

        if (Revving){
            mainShooter.set(ControlMode.PercentOutput, revVal);
        }
    }
    public void Shoot (boolean buttonPressed){

        if (buttonPressed){
            solenoid.toggle();
        }
    }

    public void Intake(boolean buttonPressed){
        if (buttonPressed) {
            mainShooter.set(ControlMode.PercentOutput, revVal*-0.2);
        }

    }

























}
