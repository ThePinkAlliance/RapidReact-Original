// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import com.ThePinkAlliance.swervelib.SdsModuleConfigurations;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Base;

public class DriveStraight extends CommandBase {

  Base base;
  PIDController straightController = new PIDController(2, 0.4, 0.004);
  PIDController alignController = new PIDController(3, 0.2, 0.002);

  double targetAngle;
  double targetInches;

  /** Creates a new DriveStraight. */
  public DriveStraight(Base base, double targetInches, double targetAngle) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.base = base;
    this.targetInches = targetInches;
    this.targetAngle = targetAngle;

    addRequirements(base);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    alignController.reset();
    straightController.reset();

    base.zeroGyro();

    alignController.enableContinuousInput(-180, 180);

    alignController.setTolerance(2);
    straightController.setTolerance(1);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double currentAngle = base.getSensorYaw();
    double front_left_pos = Math.abs(
      this.base.frontLeftModule.getDrivePosition()
    );
    double front_right_pos = Math.abs(
      this.base.frontRightModule.getDrivePosition()
    );

    double front_left_rot =
      SdsModuleConfigurations.MK4_L1.getDriveReduction() *
      (front_left_pos / 2048);

    double front_right_rot =
      SdsModuleConfigurations.MK4_L1.getDriveReduction() *
      (front_right_pos / 2048);

    double front_left_inches = front_left_rot * Base.circumference;
    double front_right_inches = front_right_rot * Base.circumference;

    double distance_traveled_inches =
      (front_left_inches + front_right_inches) / 2.0;

    double x_power = MathUtil.clamp(
      straightController.calculate(distance_traveled_inches, targetInches),
      -0.8,
      0.8
    );
    double theta_power = MathUtil.clamp(
      alignController.calculate(currentAngle, targetAngle),
      -0.7,
      0.7
    );

    ChassisSpeeds speeds = new ChassisSpeeds(x_power, 0, theta_power);

    base.drive(speeds);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return straightController.atSetpoint() && alignController.atSetpoint();
  }
}
