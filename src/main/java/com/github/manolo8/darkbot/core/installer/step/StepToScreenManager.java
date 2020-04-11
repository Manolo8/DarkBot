package com.github.manolo8.darkbot.core.installer.step;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToScreenManager extends Step {

    public StepToScreenManager(StepToMain stepToMain) {
        stepToMain.addressObservable.subscribe(this::install);
    }

    @Override
    public boolean isValid() {
        return addressObservable.value != 0;
    }

    @Override
    public boolean blockUpdate() {
        return true;
    }

    private void install(long address) {

        if (address == 0) {
            addressObservable.value = 0L;
            return;
        }

        long temp = API.readMemoryLong(address + 504);

        if (temp == 0)
            throw new Error("ScreenManager not found!");

        addressObservable.next(temp);
    }

}
