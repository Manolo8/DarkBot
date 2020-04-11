package com.github.manolo8.darkbot.view.draw.types;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.manager.*;
import com.github.manolo8.darkbot.core.objects.PetGear;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.module.ModuleAndInfo;
import com.github.manolo8.darkbot.view.draw.Drawable;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;

import java.text.DecimalFormat;
import java.util.List;

import static com.github.manolo8.darkbot.view.draw.Palette.*;

public class InfoDrawer implements Drawable {

    private final HeroManager   hero;
    private final PetManager    petManager;
    private final PingManager   pingManager;
    private final StatsManager  stats;
    private final ModuleManager moduleManager;
    private final CommonConfig  config;
    private final DecimalFormat format;
    private final BotInstaller  installer;


    public InfoDrawer(HeroManager hero,
                      PetManager petManager,
                      PingManager pingManager,
                      StatsManager stats,
                      ModuleManager moduleManager,
                      CommonConfig config,
                      BotInstaller installer) {
        this.hero = hero;
        this.petManager = petManager;
        this.pingManager = pingManager;
        this.stats = stats;
        this.moduleManager = moduleManager;
        this.config = config;
        this.installer = installer;
        this.format = Palette.formatter;
    }

    @Override
    public void draw(GraphicDrawer drawer) {

        drawer.setColor(TEXT_DARK);
        drawer.setFont(FONT_SMALL);

        drawer.set(drawer.width - 10, 20);

        Location location = hero.location;

        ModuleAndInfo module = moduleManager.getCurrentModuleInfo();
        PetGear       gear   = petManager.current();
//
//        drawer.move(0, 15);
//        drawer.drawStringRight("cargo " + stats.deposit + "/" + stats.depositTotal);
//        drawer.move(0, 15);
//        drawer.drawStringRight("deaths");
//        drawer.move(0, 15);
//        drawer.drawStringRight("-hero " + stats.deaths);
//        drawer.move(0, 15);
//        drawer.drawStringRight("-pet  " + stats.petDeaths);

        StringBuilder builder = new StringBuilder();

        builder.append("Hero ");

        builder.append(module == null ? "none" : module.getName())
                .append(' ')
                .append(stats.deaths).append('/');

        if (config.MAX_DEATHS == -1)
            builder.append('∞');
        else
            builder.append(config.MAX_DEATHS);

        builder.append(" | PET ");

        if (gear != null)
            builder.append(gear.inGameName).append(' ');
        else if (hero.pet.isInvalid())
            builder.append("OFF ");

        builder.append(stats.petDeaths)
                .append('/');

        if (config.MAX_PET_DEATHS == -1)
            builder.append('∞');
        else
            builder.append(config.MAX_PET_DEATHS);

        builder.append(" | X ")
                .append((int) location.x)
                .append(" Y ")
                .append((int) location.y)
                .append(" | ")
                .append(pingManager.ping)
                .append(" ms");

        drawer.drawStringLeft(builder.toString());

        drawer.move(0, 15);

        List<String> errors = installer.currentErrors;

        if (errors.size() != 0) {
            synchronized (installer.currentErrors) {
                for (String error : errors) {
                    int last = 0;
                    for (int i = 0; i < error.length(); i++) {
                        char    c      = error.charAt(i);
                        boolean finish = error.length() - 1 == i;
                        if (c == '\n' || finish) {
                            drawer.drawStringLeft(error.substring(last == 0 ? 0 : last + 1, finish ? i + 1 : i));
                            drawer.move(0, 15);
                            last = i;
                        }
                    }
                }
            }
        }


        drawer.set(drawer.midX(), drawer.midY());

        drawer.setFont(FONT_BIG);
        drawer.drawStringCenter(hero.map.name);

        drawer.move(0, 35);
        drawer.setFont(FONT_SMALL);
        drawer.drawStringCenter("RUNNING " + stats.runningTimeStr());

        drawer.set(0, (drawer.midY()) - 47);
        drawer.setColor(TEXT);

        drawer.drawStringRight("cre/h " + format.format(stats.earnedCredits()));
        drawer.move(0, 15);
        drawer.drawStringRight("uri/h " + format.format(stats.earnedUridium()));
        drawer.move(0, 15);
        drawer.drawStringRight("exp/h " + format.format(stats.earnedExperience()));
        drawer.move(0, 15);
        drawer.drawStringRight("hon/h " + format.format(stats.earnedHonor()));
        drawer.move(0, 15);
        drawer.drawStringRight("cargo " + format.format(stats.deposit));
    }

    @Override
    public void redraw(GraphicDrawer drawer) {
        drawer.setTranslate(false);
        draw(drawer);
    }
}
