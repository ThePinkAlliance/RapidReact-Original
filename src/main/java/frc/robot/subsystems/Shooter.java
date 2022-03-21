// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ThePinkAlliance.core.math.Projectile;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.platform.DeviceType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.ShooterConstants;

public class Shooter extends SubsystemBase {

  public static final double SHOOTER_POWER_CLOSE_HIGH = 3800; // 3800, 3425
  public static final double SHOOTER_POWER_CLOSE_HIGH_V2 = 3800;
  public static final double SHOOTER_POWER_CLOSE_LOW = 3800;
  public static final double SHOOTER_POWER_CLOSE_DEFAULT =
    SHOOTER_POWER_CLOSE_HIGH_V2;

  public double CURRENT_HOOD_ANGLE = 45;

  public final double REV_TICKS_PER_REV = 42;
  public final double MAX_HOOD_WIDTH_INCHES = 5.582;
  public final double MIN_HOOD_HEIGHT = 2.935020;
  public final double MAX_HOOD_HEIGHT_INCHES = 7.159;
  public final double HOOD_LENGTH_INCHES = 6.4;
  public final double HOOD_WHEEL_CIRCUMFERENCE = 2.5132741229;

  public double CURRENT_HOOD_HEIGHT = MAX_HOOD_HEIGHT_INCHES;

  private double RAMP_RATE = 0;
  private double NOMINAL_FORWARD = 0;
  private double NOMINAL_REVERSE = 0;
  public static double GEAR_MULTIPLYER = 1.6;
  private double PEAK_FORWARD = 1;
  private double PEAK_REVERSE = -1;
  private final int SHOOTER_MOTOR = 11;
  private final int HOOD_MOTOR = 12;
  private boolean isActivated = false;
  private TalonFX motor;
  private CANSparkMax hoodMotor;
  private RelativeEncoder hoodEncoder;

  /** Creates a new Shooter. */
  public Shooter() {
    motor = new TalonFX(SHOOTER_MOTOR);
    hoodMotor = new CANSparkMax(HOOD_MOTOR, MotorType.kBrushless);

    configureMotor();
  }

  public boolean isActivate() {
    return this.isActivated;
  }

  public double getMotorRpms() {
    double velocity =
      (
        (
          (
            (this.motor.getSelectedSensorVelocity()) /
            Base.FULL_TALON_ROTATION_TICKS
          ) /
          Shooter.GEAR_MULTIPLYER
        ) *
        600
      );
    return Math.abs(velocity);
  }

  public double calculateOptimalTrajectory(double distance) {
    return Projectile.calculateRange(CURRENT_HOOD_ANGLE, distance);
  }

  public boolean readyToShoot(double max, double threshold) {
    double velocity = getMotorRpms();
    return Math.abs(velocity - max) <= threshold;
  }

  public double hoodDesiredTicks(double angle) {
    return (
      (
        (Math.tan(angle) * (MAX_HOOD_WIDTH_INCHES - MIN_HOOD_HEIGHT)) /
        HOOD_WHEEL_CIRCUMFERENCE
      ) *
      REV_TICKS_PER_REV
    );
  }

  public SparkMaxPIDController hoodPidController() {
    return hoodMotor.getPIDController();
  }

  public void commandHood(double power) {
    this.hoodMotor.set(power);
  }

  public double getHoodTicks() {
    return hoodEncoder.getPosition();
  }

  public double getHoodAngle() {
    double currentHeight =
      MIN_HOOD_HEIGHT +
      (
        HOOD_WHEEL_CIRCUMFERENCE *
        (hoodEncoder.getPosition() / REV_TICKS_PER_REV)
      );

    CURRENT_HOOD_HEIGHT = currentHeight;

    return (
      (Math.cos(currentHeight) / Math.sin(MAX_HOOD_WIDTH_INCHES)) *
      (180 / Math.PI)
    );
  }

  public double getMotorOutputPercent() {
    return motor.getMotorOutputPercent();
  }

  public void command(double power) {
    // apply power
    motor.set(ControlMode.PercentOutput, power);
    // when power is being applied:  isActivated needs to be true
    this.isActivated = (power != 0) ? true : false;
  }

  public void commandRpm(double rpm) {
    double velo = ((rpm * 2048) * GEAR_MULTIPLYER) / 600;
    motor.set(ControlMode.Velocity, velo);
    // when power is being applied:  isActivated needs to be true
    this.isActivated = (rpm != 0) ? true : false;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  private void configureMotor() {
    hoodEncoder = hoodMotor.getEncoder();

    hoodMotor.setSmartCurrentLimit(20);

    hoodMotor.setSoftLimit(SoftLimitDirection.kReverse, 3);
    hoodMotor.setSoftLimit(SoftLimitDirection.kReverse, 3);

    this.motor.setNeutralMode(NeutralMode.Coast);
    this.motor.configSelectedFeedbackSensor(
        TalonFXFeedbackDevice.IntegratedSensor,
        ShooterConstants.kPIDLoopIdx,
        ShooterConstants.kTimeoutMs
      );
    //this.motor.setSensorPhase(true);  //NOT NEEDED SINCE ITS INTEGRATED SENSOR
    this.motor.configClosedloopRamp(RAMP_RATE);
    this.motor.configNominalOutputForward(
        NOMINAL_FORWARD,
        ShooterConstants.kTimeoutMs
      );
    this.motor.configNominalOutputReverse(
        NOMINAL_REVERSE,
        ShooterConstants.kTimeoutMs
      );
    this.motor.configPeakOutputForward(
        PEAK_FORWARD,
        ShooterConstants.kTimeoutMs
      );
    this.motor.configPeakOutputReverse(
        PEAK_REVERSE,
        ShooterConstants.kTimeoutMs
      );
    this.motor.configAllowableClosedloopError(
        ShooterConstants.kPIDLoopIdx,
        ShooterConstants.ALLOWABLE_CLOSELOOP_ERROR,
        ShooterConstants.kTimeoutMs
      );
    this.motor.config_kF(
        ShooterConstants.kPIDLoopIdx,
        ShooterConstants.kGains.kF,
        ShooterConstants.kTimeoutMs
      );
    this.motor.config_kP(
        ShooterConstants.kPIDLoopIdx,
        ShooterConstants.kGains.kP,
        ShooterConstants.kTimeoutMs
      );
    this.motor.config_kI(
        ShooterConstants.kPIDLoopIdx,
        ShooterConstants.kGains.kI,
        ShooterConstants.kTimeoutMs
      );
    this.motor.config_kD(
        ShooterConstants.kPIDLoopIdx,
        ShooterConstants.kGains.kD,
        ShooterConstants.kTimeoutMs
      );
  }
}
