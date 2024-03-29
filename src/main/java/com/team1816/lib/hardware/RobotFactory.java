package com.team1816.lib.hardware;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team1816.lib.hardware.components.CanifierImpl;
import com.team1816.lib.hardware.components.GhostCanifier;
import com.team1816.lib.hardware.components.ICanifier;
import com.team1816.lib.hardware.components.pcm.*;
import edu.wpi.first.wpilibj.DriverStation;

import javax.annotation.Nonnull;
import java.util.List;

public class RobotFactory {

    private YamlConfig config;
    private static boolean verbose;
    private static RobotFactory factory;

    public static RobotFactory getInstance() {
        if (factory == null) {
            var robotName = System.getenv("ROBOT_NAME");
            if (robotName == null) {
                robotName = "default";
                DriverStation.reportWarning(
                    "ROBOT_NAME environment variable not defined, falling back to default.config.yml!",
                    false
                );
            }
            factory = new RobotFactory(robotName);
        }
        return factory;
    }

    public RobotFactory(String configName) {
        System.out.println("Loading Config for " + configName);
        try {
            config =
                YamlConfig.loadFrom(
                    this.getClass()
                        .getClassLoader()
                        .getResourceAsStream(configName + ".config.yml")
                );
        } catch (ConfigIsAbstractException e) {
            DriverStation.reportError("Yaml Config was abstract!", e.getStackTrace());
        }
        verbose = getConstant("verbose") >= 1;
    }

    public IMotorControllerEnhanced getMotor(
        String subsystemName,
        String name,
        List<PidConfig> pidConfigs
    ) {
        IMotorControllerEnhanced motor = null;
        var subsystem = getSubsystem(subsystemName);

        // Motor creation
        if (subsystem.isImplemented()) {
            if (isHardwareValid(subsystem.talons.get(name))) {
                motor =
                    CtreMotorFactory.createDefaultTalon(
                        subsystem.talons.get(name),
                        name,
                        false,
                        subsystem,
                        pidConfigs
                    );
            } else if (isHardwareValid(subsystem.falcons.get(name))) {
                motor =
                    CtreMotorFactory.createDefaultTalon(
                        subsystem.falcons.get(name),
                        name,
                        true,
                        subsystem,
                        pidConfigs
                    );
            } // Never make the victor a master
        }
        if (motor == null) {
            reportGhostWarning("Motor", subsystemName, name);
            motor = CtreMotorFactory.createGhostTalon();
        }

        // Motor configuration
        if (subsystem.isImplemented() && subsystem.invertMotor.contains(name)) {
            System.out.println("Inverting " + name + " with ID " + motor.getDeviceID());
        }

        return motor;
    }

    public IMotorControllerEnhanced getMotor(String subsystemName, String name) {
        return getMotor(subsystemName, name, getSubsystem(subsystemName).pid);
    }

    public IMotorController getMotor(
        String subsystemName,
        String name,
        IMotorController master
    ) { // TODO: optimize this method
        IMotorController motor = null;
        var subsystem = getSubsystem(subsystemName);
        if (subsystem.isImplemented() && master != null) {
            if (isHardwareValid(subsystem.talons.get(name))) {
                // Talons must be following another Talon, cannot follow a Victor.
                motor =
                    CtreMotorFactory.createPermanentSlaveTalon(
                        subsystem.talons.get(name),
                        name,
                        false,
                        master,
                        subsystem,
                        subsystem.pid
                    );
            } else if (isHardwareValid(subsystem.falcons.get(name))) {
                motor =
                    CtreMotorFactory.createPermanentSlaveTalon(
                        subsystem.falcons.get(name),
                        name,
                        true,
                        master,
                        subsystem,
                        subsystem.pid
                    );
            } else if (isHardwareValid(subsystem.victors.get(name))) {
                // Victors can follow Talons or another Victor.
                motor =
                    CtreMotorFactory.createPermanentSlaveVictor(
                        subsystem.victors.get(name),
                        master
                    );
            }
        }
        if (motor == null) {
            if (subsystem.isImplemented()) reportGhostWarning(
                "Motor",
                subsystemName,
                name
            );
            motor = CtreMotorFactory.createGhostTalon();
        }
        return motor;
    }

    private boolean isHardwareValid(Integer hardwareId) {
        return hardwareId != null && hardwareId > -1;
    }

    @Nonnull
    public ISolenoid getSolenoid(String subsystemName, String name) {
        var subsystem = getSubsystem(subsystemName);
        Integer solenoidId = subsystem.solenoids.get(name);
        if (subsystem.isImplemented() && isHardwareValid(solenoidId) && isPcmEnabled()) {
            return new SolenoidImpl(config.pcm, solenoidId);
        }
        if (subsystem.isImplemented()) {
            reportGhostWarning("Solenoid", subsystemName, name);
        }
        return new GhostSolenoid();
    }

    @Nonnull
    public IDoubleSolenoid getDoubleSolenoid(String subsystemName, String name) {
        var subsystem = getSubsystem(subsystemName);
        DoubleSolenoidConfig solenoidConfig = getSubsystem(subsystemName)
            .doubleSolenoids.get(name);
        if (
            subsystem.isImplemented() &&
            solenoidConfig != null &&
            isHardwareValid(solenoidConfig.forward) &&
            isHardwareValid(solenoidConfig.reverse) &&
            isPcmEnabled()
        ) {
            return new DoubleSolenoidImpl(
                config.pcm,
                solenoidConfig.forward,
                solenoidConfig.reverse
            );
        }
        reportGhostWarning("DoubleSolenoid", subsystemName, name);
        return new GhostDoubleSolenoid();
    }

    @Nonnull
    public ICanifier getCanifier(String subsystemName) {
        var subsystem = getSubsystem(subsystemName);
        if (subsystem.isImplemented() && isHardwareValid(subsystem.canifier)) {
            return new CanifierImpl(subsystem.canifier);
        }
        reportGhostWarning("CANifier", subsystemName, "canifier");
        return new GhostCanifier();
    }

    public ICompressor getCompressor() {
        if (isPcmEnabled()) {
            return new CompressorImpl(getPcmId());
        }
        reportGhostWarning("Compressor", "ROOT", "on PCM ID " + getPcmId());
        return new GhostCompressor();
    }

    public Double getConstant(String name) {
        return getConstant(name, 0.0);
    }

    public Double getConstant(String name, double defaultVal) {
        if (!config.constants.containsKey(name)) {
            DriverStation.reportError("Yaml constants:" + name + " missing", false);
            return defaultVal;
        }
        return config.constants.get(name);
    }

    public double getConstant(String subsystemName, String name) {
        return getConstant(subsystemName, name, 0.0);
    }

    public double getConstant(String subsystemName, String name, double defaultVal) {
        if (!getSubsystem(subsystemName).isImplemented()) {
            return defaultVal;
        }
        if (!getSubsystem(subsystemName).constants.containsKey(name)) {
            DriverStation.reportError(
                "Yaml " + subsystemName + " constants:" + name + " missing",
                false
            );
            return defaultVal;
        }
        return getSubsystem(subsystemName).constants.get(name);
    }

    public PidConfig getPidConfig(String subsystemName, int slot) {
        return getSubsystem(subsystemName).pid.get(slot);
    }

    public int getPcmId() {
        if (config.pcm == null) return -1;
        return config.pcm;
    }

    public boolean isPcmEnabled() {
        return getPcmId() > -1;
    }

    public SubsystemConfig getSubsystem(String subsystemName) {
        SubsystemConfig subsystem = null;
        // yaml is missing subsystems so just use default
        if(config.subsystems != null) {
            subsystem = config.subsystems.get(subsystemName);
        }
        if (subsystem == null) {
            subsystem = new SubsystemConfig(false);
        }
        return subsystem;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    private void reportGhostWarning(
        String type,
        String subsystemName,
        String componentName
    ) {
        System.out.println(
            "  " +
            type +
            "  " +
            componentName +
            " not defined or invalid in config for subsystem " +
            subsystemName +
            ", using ghost!"
        );
    }
}
