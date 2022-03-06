// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Shooter;

public class Shoot extends CommandBase {

  private Shooter shooter;
  private double power;
  private double actualPower;
  private Joystick joystick;

  /** Creates a new Shoot. */
  public Shoot(Shooter shooter, double power, Joystick js) {
    // Use addRequirements() here to declare subsystem dependencies.

    this.shooter = shooter;
    this.power = power;
    this.joystick = js;
    this.actualPower = power;

    addRequirements(shooter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

     double dashPower = SmartDashboard.getNumber(Constants.DASH_SHOOTER_POWER, this.power);
     actualPower = dashPower;

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    shooter.command(actualPower);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    shooter.command(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    boolean pressed = joystick.getRawButton(Constants.JOYSTICK_BUTTON_X);

    if (pressed) {
      return false;
    } else {
      return true;
    }
  }
}
