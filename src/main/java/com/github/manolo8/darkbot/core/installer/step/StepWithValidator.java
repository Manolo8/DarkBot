package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.exception.StepException;

public abstract class StepWithValidator
        extends Step {

    public abstract void validate()
            throws StepException;
}
