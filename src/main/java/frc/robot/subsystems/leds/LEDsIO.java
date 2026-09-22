// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.leds;
import org.littletonrobotics.junction.AutoLog;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

/** Add your docs here. */
public interface LEDsIO {
    @AutoLog
    public static class LEDsIOInputs {
        public double fpgaTime = 0.0;
    }

    public static class LEDsIOOutputs {
        public AddressableLEDBuffer buffer;
    }

    default void updateInputs(LEDsIOInputs inputs) {}
    default void applyOutputs(LEDsIOOutputs outputs) {}
}
