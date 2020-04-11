package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.utils.Clock;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepMapChangeBug
        extends StepWithValidator {

    private final Clock clock;

    private long settingsAddress;
    private long mapAddressStatic;

    private boolean waiting;

    private int currentMapId;

    public StepMapChangeBug(StepToSettings stepToSettings, StepToScreenManager stepToScreenManager) {
        this.clock = new Clock();
        stepToSettings.addressObservable.subscribe(value -> settingsAddress = value);
        stepToScreenManager.addressObservable.subscribe(value -> mapAddressStatic = value + 256);
    }

    @Override
    public void validate()
            throws StepException {

        int nextMapId = API.readMemoryInt(settingsAddress + 204);
        int temp      = API.readMemoryInt(API.readMemoryLong(mapAddressStatic) + 76);

        if (temp == -1) {
            currentMapId = temp;
            return;
        }

        if (temp != nextMapId) {

            if (!waiting) {
                waiting = true;
                clock.reset();
            }

            if (clock.isBiggerThenReset(20_000))
                API.reload();

            throw new StepException("Changing MAP... (" + (clock.elapsed() / 1000) + "/20)");
        } else {
            waiting = false;
            currentMapId = temp;
        }

    }

    @Override
    public boolean isValid() {

        if (settingsAddress == 0)
            return true;

        int nextMapId = API.readMemoryInt(settingsAddress + 204);

        if (nextMapId <= 0 || currentMapId == -1)
            return true;

        return currentMapId == nextMapId;
    }

    @Override
    public boolean blockTick() {
        return true;
    }
}