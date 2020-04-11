package com.github.manolo8.darkbot.core.installer.step;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToGuiManager
        extends Step {

    public StepToGuiManager(StepToMain stepToMain) {
        stepToMain.addressObservable.subscribe(this::install);
    }

    @Override
    public boolean isValid() {
        return this.addressObservable.value != 0;
    }

    @Override
    public boolean blockUpdate() {
        return true;
    }

    private void install(long mainAddress) {

        if (mainAddress == 0) {
            addressObservable.value = 0L;
            return;
        }

        long temp = API.readMemoryLong(mainAddress + 512);

        if (temp == 0)
            throw new Error("GuiManagerAddress not found!");

        addressObservable.next(temp);
    }

}
