// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.GenericHID;
import frc.robot.utils.RumblePattern;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class Rumble extends Command {
  /** Creates a new Rumble. */
  private final GenericHID controller;
  private final RumblePattern pattern;

  private int step;
  private double stepStartTime;

  public Rumble(GenericHID controller, RumblePattern pattern) {
    this.controller = controller;
    this.pattern = pattern;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    step = 0;
    stepStartTime = timestamp();

    setRumble(pattern.intensity(0));
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double elapsed = timestamp() - stepStartTime;

    if (elapsed >= pattern.duration(step)) {
      step++;

      if (step < pattern.size()) {
        stepStartTime = timestamp();
        setRumble(pattern.intensity(step));
      }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    setRumble(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return step >= pattern.size();
  }

   private void setRumble(double intensity) {
    controller.setRumble(GenericHID.RumbleType.kBothRumble, intensity);
  }

  private static double timestamp() {
    return edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
  }
}
