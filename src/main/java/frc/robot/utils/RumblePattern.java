package frc.robot.utils;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public record RumblePattern(double[] intensities, double[] durations) {
  public RumblePattern {
    if (intensities.length == 0 || intensities.length != durations.length) {
      throw new IllegalArgumentException("Invalid rumble pattern");
    }

    intensities = intensities.clone();
    durations = durations.clone();

    for (double intensity : intensities) {
      if (intensity < 0.0 || intensity > 1.0) {
        throw new IllegalArgumentException("Rumble intensity must be 0-1");
      }
    }

    for (double duration : durations) {
      if (duration <= 0.0) {
        throw new IllegalArgumentException("Rumble duration must be > 0");
      }
    }
  }

  public double duration() {
    double total = 0.0;
    for (double duration : durations) {
      total += duration;
    }
    return total;
  }

  public double intensity(int index) {
    return intensities[index];
  }

  public double duration(int index) {
    return durations[index];
  }

  public int size() {
    return intensities.length;
  }

  public static RumblePattern constant(double intensity, double duration) {
    return new RumblePattern(
        new double[] {intensity},
        new double[] {duration});
  }

  public static RumblePattern pulse(
      double intensity,
      double onTime,
      double offTime,
      int pulses) {

    double[] intensities = new double[pulses * 2];
    double[] durations = new double[pulses * 2];

    for (int i = 0; i < pulses; i++) {
      intensities[i * 2] = intensity;
      durations[i * 2] = onTime;

      intensities[i * 2 + 1] = 0.0;
      durations[i * 2 + 1] = offTime;
    }

    return new RumblePattern(intensities, durations);
  }

  public static final RumblePattern VISION_TARGET =
      pulse(1.0, 0.15, 0.10, 2);

  public static final RumblePattern INTAKE_DETECTED =
      pulse(0.7, 0.10, 0.10, 1);

  public static final RumblePattern CLIMB_READY =
      pulse(1.0, 0.20, 0.10, 3);
}