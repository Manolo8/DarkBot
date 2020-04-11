package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.utils.Clock;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToPing extends StepWithValidator {

    private long  holder;
    private Clock clock;

    public StepToPing() {
        clock = new Clock();
    }

    @Override
    public boolean isValid() {
        return addressObservable.value != 0 && API.readMemoryLong(holder + 40) == addressObservable.value;
    }

    @Override
    public void validate()
            throws StepException {

        if (!clock.isBiggerThenReset(10000))
            throw new StepException("PingManager not found!");

        long temp = 0;

        long[] result = API.queryMemoryLong(1000, 10_000);

        for (long value : result) {

            if (API.readMemoryInt(value + 16) != 0)
                continue;

            long address = API.readMemoryLong(value + 8);

            if (address == 0)
                continue;

            if (API.readMemoryInt(address + 12) != 0 || API.readMemoryInt(address + 36) != 0)
                continue;

            if (API.readMemoryLong(address) == 0)
                continue;

            holder = address;
            address = API.readMemoryLong(address + 40);

            int size = API.readMemoryInt(address + 64);

            if (size < 5 || size > 50)
                continue;

            temp = address;
        }

        if (temp == 0)
            throw new StepException("PingManager not found!");

        addressObservable.next(temp);
    }
}
