// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Arm;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Arm.Arm;

public class ArmAngleSpeaker extends Command {
  private Arm arm;

  /** Creates a new ArmAngleSpeaker. */
  public ArmAngleSpeaker(Arm Arm) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.arm = Arm;
    addRequirements(arm);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    arm.setPosition(Constants.ANGLE_SPEAKER);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (Math.abs(arm.getPosition() - frc.robot.Constants.ANGLE_SPEAKER) > 0.25) {
      return false;
    }
    System.out.println("ArmSpeakerCommand Ended");
    return true;
    // return false;
  }
}
