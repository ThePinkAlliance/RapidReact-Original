// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.subsystems.Base;
import frc.robot.subsystems.Collector;
import frc.robot.subsystems.Shooter;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ShootLeaveTarmac extends SequentialCommandGroup {

  /** Creates a new ShootLeaveTarmac. */
  public ShootLeaveTarmac(
    Base m_base,
    Shooter m_shooter,
    Collector m_collector
  ) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new AutoShoot(m_shooter, m_collector, Constants.SHOOTER_POWER_CLOSE_HIGH),
      new Navigate(m_base, LeaveTarmack.TRAVEL_DISTANCE)
    );
  }
}
