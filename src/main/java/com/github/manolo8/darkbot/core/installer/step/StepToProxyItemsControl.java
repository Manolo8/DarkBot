package com.github.manolo8.darkbot.core.installer.step;

/**
 * Using SWF objects:
 * <p>
 * ItemsControlMenuProxy
 * Known offsets:
 * - 32 String             (Expected to be 'Game")
 * - 40 String             (Expected to be 'Multi...')
 * - 56 String             (Expected to be 'ItemsControlMenuProxy')
 * - 88 CategoryBarVo
 */
public class StepToProxyItemsControl
        extends StepToProxy {

    public StepToProxyItemsControl() {
        super(
                523986010133L,
                new byte[]{73, 116, 101, 109, 115, 67, 111, 110, 116, 114, 111, 108, 77, 101, 110, 117, 80, 114, 111, 120, 121},
                new byte[]{71, 97, 109, 101}
        );
    }
}
