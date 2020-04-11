package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.utils.Clock;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToPetWindow extends StepWithValidator {

    private final Clock clock;

    private long mainAddress;
    private long gearsAddress;

    public StepToPetWindow(StepToMain stepToMain) {
        this.clock = new Clock();

        stepToMain.addressObservable.subscribe(value -> mainAddress = value);
    }

    @Override
    public boolean isValid() {
        return API.readMemoryInt(gearsAddress + 32) == 500;
    }

    @Override
    public void validate()
            throws StepException {

        if (!isValid() && clock.isBiggerThenReset(10_000)) {

            long[] query = API.queryMemoryLong(mainAddress, 100);

            gearsAddress = 0;

            for (long address : query) {

                long temp = API.readMemoryLong(address + 16);

                if (API.readMemoryInt(temp + 32) != 500)
                    continue;

                gearsAddress = temp;
                break;
            }
        }

        if (gearsAddress == 0)
            throw new StepException("PetAddress not found!");

        try {
            addressObservable.next(API.readMemoryLong(gearsAddress + 80));
        } catch (Error e) {
            gearsAddress = 0;
            throw new StepException(e.getMessage());
        }
    }

    @Override
    public boolean blockUpdate() {
        return false;
    }
}
