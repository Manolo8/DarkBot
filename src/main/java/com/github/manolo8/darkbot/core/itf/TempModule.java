package com.github.manolo8.darkbot.core.itf;

import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.ModuleManager;
import com.github.manolo8.darkbot.core.utils.module.Module;

public abstract class TempModule
        implements Module {

    private final ModuleManager           moduleManager;
    private       Class<? extends Module> lastModule;

    public TempModule(Core core) {
        this.moduleManager = core.getModuleManager();
    }

    @Override
    public void resume() {
        if (this.lastModule == null)
            this.lastModule = moduleManager.getCurrentModule().getClass();
    }

    protected final void back() {
        this.moduleManager.setModule(lastModule);
        lastModule = null;
    }
}