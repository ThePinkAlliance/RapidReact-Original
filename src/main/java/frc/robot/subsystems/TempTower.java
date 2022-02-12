// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;

public class TempTower extends SubsystemBase {

  private final I2C.Port port = I2C.Port.kOnboard;
  private final ColorSensorV3 colorSensor = new ColorSensorV3(port);

  private DoubleSupplier red = () -> 0.0;
  private DoubleSupplier blue = () -> 0.0;

  /** Creates a new TempBase. */
  public TempTower() {}

  public ColorSensorV3 getColorSensor() {
    return this.colorSensor;
  }

  public double getRed() {
    return this.red.getAsDouble();
  }

  public double getBlue() {
    return this.blue.getAsDouble();
  }

  public DoubleSupplier getRedSupplier() {
    return this.red;
  }

  public DoubleSupplier getBlueSupplier() {
    return this.blue;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    if (colorSensor != null) {
      this.blue = () -> colorSensor.getBlue();
      this.red = () -> colorSensor.getRed();

      SmartDashboard.putNumber("blue", colorSensor.getRawColor().blue);
      SmartDashboard.putNumber("red", colorSensor.getRawColor().red);
      SmartDashboard.putNumber("ir", colorSensor.getIR());
      SmartDashboard.putNumber("proximity", colorSensor.getProximity());
    }
  }
}
