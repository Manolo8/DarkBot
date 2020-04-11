package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;

import java.util.Arrays;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToProxy
        extends StepWithValidator {

    private final long   sizeWide;
    private final byte[] matchProxyName;
    private final byte[] matchFacade;

    private long storedAddress;

    public StepToProxy(long sizeWide, byte[] matchProxyName, byte[] matchFacade) {
        this.sizeWide = sizeWide;
        this.matchProxyName = matchProxyName;
        this.matchFacade = matchFacade;
    }

    @Override
    public void validate()
            throws StepException {

        long   found = 0;
        long[] result;

        result = API.queryMemoryLong(sizeWide, 20);

        for (long value : result) {
            byte[] array = API.readMemory(API.readMemoryLong(value - 16), matchProxyName.length);

            if (Arrays.equals(array, matchProxyName)) {
                found = value - 32;
                break;
            }
        }

        if (found == 0)
            throw new StepException("Facade not found! " + (getClass().getSimpleName()));

        result = API.queryMemoryLong(found, 100);
        found = 0;


        for (long value : result) {
            byte[] array = API.readMemory(API.readMemoryLong(API.readMemoryLong(value - 24) + 16), matchFacade.length);

            if (Arrays.equals(array, matchFacade)) {
                found = value - 56;
                break;
            }
        }


        if (found == 0)
            throw new StepException("Facade not found! " + (getClass().getSimpleName()));

        try {
            addressObservable.next(found);
            storedAddress = API.readMemoryLong(found + 40);
        } catch (Error e) {
            throw new StepException(e.getMessage());
        }
    }

    @Override
    public boolean isValid() {
        return addressObservable.value != 0 && API.readMemoryLong(addressObservable.value + 40) == storedAddress;
    }
}
