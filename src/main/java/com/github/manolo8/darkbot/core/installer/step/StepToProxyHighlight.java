package com.github.manolo8.darkbot.core.installer.step;

/**
 * Using SWF objects:
 * <p>
 * HighlightProxy
 * Known offsets:
 * - 32 String              (Expected to be 'Game")
 * - 40 String              (Expected to be 'Multi...')
 * - 48 Dictionary (-1)     (Highlight items)
 * - 56 String              (Expected to be 'HighlightProxy')
 */
public class StepToProxyHighlight
        extends StepToProxy {

    public StepToProxyHighlight() {
        super(
                523986010126L,
                new byte[]{72, 105, 103, 104, 108, 105, 103, 104, 116, 80, 114, 111, 120, 121},
                new byte[]{71, 97, 109, 101}
        );
    }
}
