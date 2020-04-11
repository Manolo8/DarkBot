package com.github.manolo8.darkbot.core.installer.step;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToMain extends Step {

    public StepToMain(StepToMainApplication stepToMainApplication) {
        stepToMainApplication.addressObservable.subscribe(this::install);
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
            addressObservable.next(0L);
            return;
        }

        long temp = API.readMemoryLong(address + 1344);

        if (temp == 0)
            throw new Error("MainAddress not found!");

        try {
            addressObservable.next(temp);
        } catch (Error e) {
            throw new Error("Error when installing StepToMain because of:\n" + e.getMessage());
        }
    }

}
