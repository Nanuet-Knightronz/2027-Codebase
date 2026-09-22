package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.utils.RumblePattern;

public class Rumble extends Command {
    private final CommandXboxController controller;
    private final RumblePattern pattern;

    private int step;
    private double stepStartTime;

    public Rumble(CommandXboxController controller, RumblePattern pattern) {
        this.controller = controller;
        this.pattern = pattern;
    }

    @Override
    public void initialize() {
        step = 0;
        stepStartTime = Timer.getFPGATimestamp();
        setRumble(pattern.intensity(0));
    }

    @Override
    public void execute() {
        if (Timer.getFPGATimestamp() - stepStartTime >= pattern.duration(step)) {
            step++;

            if (step < pattern.size()) {
                stepStartTime = Timer.getFPGATimestamp();
                setRumble(pattern.intensity(step));
            }
        }
    }

    @Override
    public void end(boolean interrupted) {
        setRumble(0.0);
    }

    @Override
    public boolean isFinished() {
        return step >= pattern.size();
    }

    private void setRumble(double intensity) {
    controller.getHID().setRumble(
        GenericHID.RumbleType.kBothRumble,
        intensity);
}
}