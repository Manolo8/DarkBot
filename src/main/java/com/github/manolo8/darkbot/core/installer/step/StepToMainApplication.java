package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.utils.Clock;

import java.util.Arrays;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToMainApplication extends StepWithValidator {

    public static final byte[] bytesToMainApplication = new byte[]{
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0
    };

    private final Clock clock;
    private       long  address;

    public StepToMainApplication() {
        this.clock = new Clock();
    }

    @Override
    public boolean isValid() {
        return address != 0 && API.readMemoryInt(address) == 16777216 || Arrays.equals(bytesToMainApplication, API.readMemory(address, bytesToMainApplication.length));
    }

    @Override
    public void validate()
            throws StepException {

        if (!isValid() && !clock.isBiggerThenReset(10_000)) {
            long[] query = API.queryMemory(bytesToMainApplication, 1);

            if (query.length != 1)
                throw new StepException("MainApplicationAddress not found!");

            address = query[0];
        }

        try {
            addressObservable.next(address - 228);
        } catch (Error e) {
            address = 0;
            throw new StepException("MainApplicationAddress found:\n" + e.getMessage());
        }
    }

    @Override
    public boolean blockUpdate() {
        return true;
    }
}
