package com.github.manolo8.darkbot.core.installer;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.installer.step.*;
import com.github.manolo8.darkbot.core.itf.Installable;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.utils.Observable;

import java.util.ArrayList;
import java.util.List;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class BotInstaller {

    private final List<Step>    steps;
    private final List<Manager> managers;

    public final Observable<Long>    mainApplicationAddress;
    public final Observable<Long>    mainAddress;
    public final Observable<Long>    screenManagerAddress;
    public final Observable<Long>    guiManagerAddress;
    public final Observable<Long>    pingManagerAddress;
    public final Observable<Long>    userDataAddress;
    public final Observable<Long>    settingsAddress;
    public final Observable<Long>    petWindowAddress;
    public final Observable<Long>    itemsControlProxyAddress;
    public final Observable<Long>    highLightProxyAddress;
    public final Observable<Boolean> status;

    private final List<String> currentErrorsToAdd;
    public final  List<String> currentErrors;

    private boolean current;

    private boolean canUpdate;
    private boolean canTickModule;

    public BotInstaller() {

        this.currentErrorsToAdd = new ArrayList<>();
        this.currentErrors = new ArrayList<>();

        this.steps = new ArrayList<>();
        this.managers = new ArrayList<>();

        this.status = new Observable<>(false);

        StepToMainApplication   stepToMainApplication   = new StepToMainApplication();
        StepToMain              stepToMain              = new StepToMain(stepToMainApplication);
        StepToScreenManager     stepToScreenManager     = new StepToScreenManager(stepToMain);
        StepToGuiManager        stepToGuiManager        = new StepToGuiManager(stepToMain);
        StepToPing              stepToPing              = new StepToPing();
        StepToSettings          stepToSettings          = new StepToSettings();
        StepToUserData          stepToUserData          = new StepToUserData(stepToScreenManager);
        StepToPetWindow         stepToPetWindow         = new StepToPetWindow(stepToMain);
        StepToResetMinDistance  stepToResetMinDistance  = new StepToResetMinDistance();
        StepMapChangeBug        stepMapChangeBug        = new StepMapChangeBug(stepToSettings, stepToScreenManager);
        StepToProxyItemsControl stepToProxyItemsControl = new StepToProxyItemsControl();
        StepToProxyHighlight    stepToProxyHighlight    = new StepToProxyHighlight();

        addStep(stepToMainApplication);
        addStep(stepToMain);
        addStep(stepToScreenManager);
        addStep(stepToGuiManager);
        addStep(stepToPing);
        addStep(stepToSettings);
        addStep(stepToUserData);
        addStep(stepToPetWindow);
        addStep(stepToResetMinDistance);
        addStep(stepMapChangeBug);
        addStep(stepToProxyItemsControl);
        addStep(stepToProxyHighlight);

        mainApplicationAddress = stepToMainApplication.addressObservable;
        mainAddress = stepToMain.addressObservable;
        screenManagerAddress = stepToScreenManager.addressObservable;
        guiManagerAddress = stepToGuiManager.addressObservable;
        pingManagerAddress = stepToPing.addressObservable;
        userDataAddress = stepToUserData.addressObservable;
        settingsAddress = stepToSettings.addressObservable;
        petWindowAddress = stepToPetWindow.addressObservable;
        itemsControlProxyAddress = stepToProxyItemsControl.addressObservable;
        highLightProxyAddress = stepToProxyHighlight.addressObservable;
    }

    public void addManager(Manager manager) {
        this.managers.add(manager);
    }

    public void addStep(Step step) {
        this.steps.add(step);
    }

    public void init() {
        for (Manager manager : managers) {
            if (manager instanceof Installable)
                ((Installable) manager).install(this);
            manager.init();
        }
    }

    public boolean canUpdate() {
        return canUpdate;
    }

    public boolean canTickModule() {
        return canTickModule;
    }

    public void check() {

        boolean canUpdate     = true;
        boolean canTickModule = true;

        for (Step step : steps) {

            if (!status.value && step.requireRunning())
                continue;

            if (!step.isValid()) {
                if (step instanceof StepWithValidator) {
                    StepWithValidator validator = (StepWithValidator) step;
                    try {
                        validator.validate();
                    } catch (StepException e) {
                        addError(e.getMessage());
                        if (step.blockUpdate()) {
                            canUpdate = false;
                            canTickModule = false;
                            step.addressObservable.next(0L);
                        }
                    }
                }

                if (!step.isValid()) {
                    step.addressObservable.next(0L);
                    if (step.blockUpdate())
                        canUpdate = false;
                    if (step.blockTick())
                        canTickModule = false;
                    break;
                }
            }
        }

        updateErrors();

        if (canTickModule != current) {
            API.setRender(!canTickModule);
            current = canTickModule;
        }

        this.canUpdate = canUpdate;
        this.canTickModule = canUpdate && canTickModule;
    }

    private void addError(String error) {
        this.currentErrorsToAdd.add(error);
    }

    private void updateErrors() {
        synchronized (currentErrors) {
            currentErrors.clear();
            currentErrors.addAll(currentErrorsToAdd);
            currentErrorsToAdd.clear();
        }
    }

}
