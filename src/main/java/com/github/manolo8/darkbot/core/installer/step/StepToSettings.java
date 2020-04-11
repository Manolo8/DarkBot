package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;

import java.util.Arrays;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToSettings extends StepWithValidator {

    private static final byte[] bytesToSettings = new byte[]{
            0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0, 5
    };

    private long address;

    @Override
    public boolean isValid() {
        return address != 0 && (API.readMemoryInt(address + 4) == 5 || Arrays.equals(bytesToSettings, API.readMemory(address, bytesToSettings.length)));
    }

    @Override
    public void validate()
            throws StepException {

        long[] settingsAddress = API.queryMemory(bytesToSettings, 1);

        if (settingsAddress.length != 1)
            throw new StepException("SettingsAddress not found!");

        this.address = settingsAddress[0];

        addressObservable.next(address - 237);
    }
}
