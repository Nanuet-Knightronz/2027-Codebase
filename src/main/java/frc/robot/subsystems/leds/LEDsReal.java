// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.leds;

import edu.wpi.first.wpilibj.AddressableLED;
import frc.robot.subsystems.leds.LEDsConstants;

/** Add your docs here. */
public class LEDsReal implements LEDsIO {
    private final AddressableLED led;

    public LEDsReal() {
        led = new AddressableLED(LEDsConstants.port);
        led.setLength(LEDsConstants.length);
        led.start();
    }

    @Override
    public void applyOutputs(LEDsIOOutputs outputs) {
        led.setData(outputs.buffer);
    }
}
