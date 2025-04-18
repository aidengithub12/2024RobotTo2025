// Copyright 2021-2024 FRC 6328
// http://github.com/Mechanical-Advantage
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// version 3 as published by the Free Software Foundation or
// available in the root directory of this project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants;
import frc.robot.subsystems.drive.Drive;
import java.util.function.DoubleSupplier;
import org.littletonrobotics.junction.Logger;

public class DriveCommands {
  private static final double DEADBAND = 0.1;
  private static double MAXSPEED;
  private static double MAXSPEED_OMEGA;

  private DriveCommands() {}

  /**
   * Field relative drive command using two joysticks (controlling linear and angular velocities).
   */
  public static Command joystickDrive(
      Drive drive,
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      DoubleSupplier omegaSupplier,
      DoubleSupplier triggerSupplier) {
    return Commands.run(
        () -> {
          if (Math.abs(triggerSupplier.getAsDouble()) > 0.05) {
            MAXSPEED =
                ((Constants.MAX_LINEAR_SPEED_TURBO - Constants.MAX_LINEAR_SPEED)
                        * Math.abs(triggerSupplier.getAsDouble()))
                    + Constants.MAX_LINEAR_SPEED;
            MAXSPEED_OMEGA =
                (((Constants.MAX_LINEAR_SPEED_TURBO - Constants.MAX_LINEAR_SPEED)
                            * Math.abs(triggerSupplier.getAsDouble()))
                        + Constants.MAX_LINEAR_SPEED)
                    / Constants.DRIVE_BASE_RADIUS;
          } else {
            MAXSPEED = drive.getMaxLinearSpeedMetersPerSec();
            MAXSPEED_OMEGA = Constants.MAX_LINEAR_SPEED / Constants.DRIVE_BASE_RADIUS;
          }
          Logger.recordOutput("Drive/DriveSpeed", MAXSPEED);
          Logger.recordOutput("Drive/OmegaSpeed", MAXSPEED_OMEGA);

          // Apply deadband
          double linearMagnitude =
              MathUtil.applyDeadband(
                  Math.hypot(xSupplier.getAsDouble(), ySupplier.getAsDouble()), DEADBAND);
          Rotation2d linearDirection =
              new Rotation2d(xSupplier.getAsDouble(), ySupplier.getAsDouble());
          double omega = MathUtil.applyDeadband(omegaSupplier.getAsDouble(), DEADBAND);

          // Square values
          linearMagnitude = linearMagnitude * linearMagnitude;
          omega = Math.copySign(omega * omega, omega);

          // Calcaulate new linear velocity
          Translation2d linearVelocity =
              new Pose2d(new Translation2d(), linearDirection)
                  .transformBy(new Transform2d(linearMagnitude, 0.0, new Rotation2d()))
                  .getTranslation();

          // Convert to field relative speeds & send command
          boolean isFlipped =
              DriverStation.getAlliance().isPresent()
                  && DriverStation.getAlliance().get() == Alliance.Red;
          //   System.out.println(isFlipped + " isRed?");
          drive.runVelocity(
              ChassisSpeeds.fromFieldRelativeSpeeds(
                  linearVelocity.getX() * MAXSPEED,
                  linearVelocity.getY() * MAXSPEED,
                  omega * MAXSPEED_OMEGA,
                  isFlipped
                      ? drive.getRotation().plus(new Rotation2d(Math.PI))
                      : drive.getRotation()));
        },
        drive);
  }
}
