// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Indexer.Indexer;
import frc.robot.subsystems.Intake.Intake;

public class ManualGrabNote extends Command {
  private Intake intake;
  private int targetSpeed = 2000;
  private Indexer indexer;
  /** Creates a new GrabNote. */
  public ManualGrabNote(Intake Intake, Indexer Indexer, int TargetSpeed) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.intake = Intake;
    this.indexer = Indexer;
    this.targetSpeed = TargetSpeed;
    addRequirements(intake, indexer);
  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    intake.runVelocity(targetSpeed);
    indexer.runVelocity(targetSpeed);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    intake.stop();
    indexer.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
