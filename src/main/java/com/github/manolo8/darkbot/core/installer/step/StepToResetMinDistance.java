package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.utils.Clock;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToResetMinDistance extends StepWithValidator {

    private Clock clock;

    public StepToResetMinDistance() {
        this.clock = new Clock();
    }

    @Override
    public void validate()
            throws StepException {

        if (clock.isBiggerThenReset(10_000)) {

            long[] query = API.queryMemoryLong(193273528320L, 10_000);

            for (long value : query) {

                int arraySize  = API.readMemoryInt(API.readMemoryLong(value + 16) + 56);
                int stringSize = API.readMemoryInt(API.readMemoryLong(value + 32) + 32);

                if (arraySize == 2 && stringSize == 14) {
                    API.writeMemoryInt(value + 4, -1);
                    addressObservable.next(value + 4);
                    return;
                }
            }
        }

        throw new StepException("ShipInfo not found!");
    }

    @Override
    public boolean isValid() {
        return API.readMemoryInt(addressObservable.value) == -1;
    }
}
