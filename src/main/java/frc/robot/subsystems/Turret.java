// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

  public static int CAN_ID_TURRET = 37;
  public static double TURRET_DEFAULT_POWER = 0.2;
  public static String TURRET_NAME = "Turret";

  TalonFX motor = new TalonFX(CAN_ID_TURRET);

  /** Creates a new Shooter. */
  public Turret() {
    motor.setNeutralMode(NeutralMode.Coast);
  }

  public void rotate(double power) {
    
    motor.set(ControlMode.PercentOutput, power);

    SmartDashboard.putNumber(
      "turret velocity",
      ((motor.getSelectedSensorVelocity() / 2048) * 10) * 60
    );
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
