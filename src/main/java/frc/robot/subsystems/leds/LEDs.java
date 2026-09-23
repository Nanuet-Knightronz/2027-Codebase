// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.leds;

import frc.robot.subsystems.leds.LEDsConstants;
import frc.robot.subsystems.leds.LEDsIO.LEDsIOInputs;
import frc.robot.subsystems.leds.LEDsIO.LEDsIOOutputs;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.Timer;

import java.util.List;
import java.util.Optional;
import org.littletonrobotics.junction.Logger;

public class LEDs extends SubsystemBase {
  
  //state booleans
  public Optional<Alliance> alliance = Optional.empty();
  public boolean isEStopped = false;
  public boolean lowBatteryAlert = false;

  private final LEDsIO io;
  private final LEDsIOInputsAutoLogged inputs = new LEDsIOInputsAutoLogged();
  private final LEDsIOOutputs outputs = new LEDsIOOutputs();

  //entire LED strip
  private static final Section fullSection = new Section(0, LEDsConstants.length);
  
  public LEDs(LEDsIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("LEDs", inputs);

    if (DriverStation.isFMSAttached()) {
      alliance = DriverStation.getAlliance();
    } else {
      alliance = Optional.empty();
    }

    if (DriverStation.isEStopped()) {
      isEStopped = true; 
    }

    solid(fullSection, Color.kBlack); 

    //state machine for LED animations
    if (isEStopped) {
        solid(fullSection, Color.kRed);

    } else if (lowBatteryAlert) {
        strobe(
            fullSection,
            Color.kOrange,
            Color.kBlack,
            LEDsConstants.strobeDuration);

    } else if (DriverStation.isDisabled()) {
        breathe(
            fullSection,
            Color.kGhostWhite,
            Color.kDimGray,
            2.0,
            Timer.getTimestamp());

    } else if (DriverStation.isAutonomous()) {
        wave(
            fullSection,
            Color.kGold,
            alliance.get() == Alliance.Red
                    ? Color.kFirstRed
                    : Color.kFirstBlue,
            LEDsConstants.waveFastCycleLength,
            LEDsConstants.waveFastDuration);

    } else {
        if (alliance.isEmpty()) {
            breathe(
                fullSection,
                Color.kGold,
                Color.kDarkGoldenrod,
                2.0,
                Timer.getTimestamp());
        } else {
            solid(
                fullSection,
                alliance.get() == Alliance.Red
                    ? Color.kFirstRed
                    : Color.kFirstBlue);
        }
    }
    outputs.buffer = buffer;
    io.applyOutputs(outputs);
  }

  //**
  // Color functions and animations used from FRC6328's 2026 code
  //  
  // @author FRC6328 Mechanical Advantage
  // @see <a href=https://github.com/Mechanical-Advantage/RobotCode2026Public/blob/main/src/main/java/org/littletonrobotics/frc2026/subsystems/leds/Leds.java#L172</a>
  // */

  private Color solid(Section section, Color color) {
    if (color != null) {
      for (int i = section.start(); i < section.end(); i++) {
        setLED(i, color);
      }
    }
    return color;
  }

  private Color strobe(Section section, Color c1, Color c2, double duration) {
    boolean c1On = ((Timer.getTimestamp() % duration) / duration) > 0.5;
    if ((c1On && c1 == null) || (!c1On && c2 == null)) return null;
    return solid(section, c1On ? c1 : c2);
  }

  @SuppressWarnings("unused")
  private Color breathe(Section section, Color c1, Color c2, double duration, double timestamp) {
    Color color = breathCalculate(section, c1, c2, duration, timestamp);
    solid(section, color);
    return color;
  }

  private Color breathCalculate(
      Section section, Color c1, Color c2, double duration, double timestamp) {
    double x = ((timestamp % duration) / duration) * 2.0 * Math.PI;
    double ratio = (Math.sin(x) + 1.0) / 2.0;
    double red = (c1.red * (1 - ratio)) + (c2.red * ratio);
    double green = (c1.green * (1 - ratio)) + (c2.green * ratio);
    double blue = (c1.blue * (1 - ratio)) + (c2.blue * ratio);
    var color = new Color(red, green, blue);
    return color;
  }

  @SuppressWarnings("unused")
  private void rainbow(Section section, double cycleLength, double duration) {
    double x = (1 - ((Timer.getTimestamp() / duration) % 1.0)) * 180.0;
    double xDiffPerLed = 180.0 / cycleLength;
    for (int i = section.end() - 1; i >= section.start(); i--) {
      x += xDiffPerLed;
      x %= 180.0;
      setHSV(i, (int) x, 255, 255);
    }
  }

  private void wave(Section section, Color c1, Color c2, double cycleLength, double duration) {
    double x = (1 - ((Timer.getTimestamp() % duration) / duration)) * 2.0 * Math.PI;
    double xDiffPerLed = (2.0 * Math.PI) / cycleLength;
    x += xDiffPerLed * (LEDsConstants.length - section.end());
    for (int i = section.end() - 1; i >= section.start(); i--) {
      x += xDiffPerLed;
      double ratio = (Math.pow(Math.sin(x), LEDsConstants.waveExponent) + 1.0) / 2.0;
      if (Double.isNaN(ratio)) {
        ratio = (-Math.pow(Math.sin(x + Math.PI), LEDsConstants.waveExponent) + 1.0) / 2.0;
      }
      if (Double.isNaN(ratio)) {
        ratio = 0.5;
      }
      double red = (c1.red * (1 - ratio)) + (c2.red * ratio);
      double green = (c1.green * (1 - ratio)) + (c2.green * ratio);
      double blue = (c1.blue * (1 - ratio)) + (c2.blue * ratio);
      setLED(i, new Color(red, green, blue));
    }
  }

  private void stripes(Section section, List<Color> colors, int stripeLength, double duration) {
    int offset = (int) (Timer.getTimestamp() % duration / duration * stripeLength * colors.size());
    for (int i = section.end() - 1; i >= section.start(); i--) {
      int colorIndex =
          (int) (Math.floor((double) (i - offset) / stripeLength) + colors.size()) % colors.size();
      colorIndex = colors.size() - 1 - colorIndex;
      setLED(i, colors.get(colorIndex));
    }
  }

  private void setHSV(int index, int h, int s, int v) {
    setLED(index, Color.fromHSV(h, s, v));
  }

  private final AddressableLEDBuffer buffer =
    new AddressableLEDBuffer(LEDsConstants.length);

private void setLED(int index, Color color) {
    buffer.setLED(index, color);
}

  private record Section(int start, int end) {}
}
