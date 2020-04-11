package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.utils.Clock;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToUserData
        extends StepWithValidator {

    private final StepToScreenManager stepToScreenManager;
    private       int                 id;
    private final Clock               clock;

    public StepToUserData(StepToScreenManager stepToScreenManager) {
        this.clock = new Clock();
        this.stepToScreenManager = stepToScreenManager;
    }

    @Override
    public boolean isValid() {
        return addressObservable.value != 0 && API.readMemoryInt(addressObservable.value + 48) == id;
    }

    @Override
    public void validate()
            throws StepException {

        if (stepToScreenManager.addressObservable.value == 0)
            return;

        long shipAddress = API.readMemoryLong(stepToScreenManager.addressObservable.value + 240);

        if (shipAddress == 0)
            return;

        id = API.readMemoryInt(shipAddress + 56);

        if (id == 0)
            return;

        if (!clock.isBiggerThenReset(10_000))
            throw new StepException("UserData not found!");

        long temp = 0;

        long[] addressToUserData = API.queryMemoryInt(id, 10);

        for (long value : addressToUserData) {

            int level = API.readMemoryInt(value + 4);
            int speed = API.readMemoryInt(value + 8);
            int bool  = API.readMemoryInt(value + 12);
            int bool2 = API.readMemoryInt(value + 16);

            if (level > 0 && level <= 32 && speed > 50 && speed < 2000 && (bool == 1 || bool == 2) && bool2 == 0) {
                temp = value - 48;
                break;
            }
        }

        if (temp == 0)
            throw new StepException("UserData not found!");

        addressObservable.next(temp);
    }

    @Override
    public boolean blockUpdate() {
        return false;
    }

}
