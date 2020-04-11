package com.github.manolo8.darkbot.backpage.task;

import com.github.manolo8.darkbot.backpage.itf.Task;
import com.github.manolo8.darkbot.backpage.types.NpcRank;
import com.github.manolo8.darkbot.backpage.utils.Client;
import com.github.manolo8.darkbot.backpage.utils.TableWalker;
import com.github.manolo8.darkbot.core.utils.Observable;

import java.util.HashMap;

public class StatsTask
        extends Task {

    private final Client                   client;
    public final  HashMap<String, NpcRank> npcRankList;
    public final  Observable<Long>         lastNpcRankListUpdate;

    public StatsTask(Client client) {
        super(1000 * 300);

        this.client = client;
        this.npcRankList = new HashMap<>();
        this.lastNpcRankListUpdate = new Observable<>(0L);
    }

    @Override
    public void execute() {
        client.getString("indexInternal.es?action=internalHallofFame&view=dailyRank", this::searchNpcRankList);
    }

    private void searchNpcRankList(String data) {

        int tableStart = data.indexOf("<table class=\"hof_shooting_table\" id=\"daily_npc_points\">") + 60;
        int tableEnd   = data.indexOf("</table>", tableStart);

        TableWalker walker = new TableWalker(data, tableStart, tableEnd);

        walker.hasNextRow();

        while (walker.hasNextRow()) {
            NpcRank rank = getOrCreateNpcRank(walker.nextCol());

            walker.skipCol();
            rank.pointsPerDestruction = Integer.parseInt(walker.nextCol());
            walker.skipCol();
            rank.update(Integer.parseInt(walker.nextCol()));
        }

        lastNpcRankListUpdate.next(lastExecuted());
    }

    private NpcRank getOrCreateNpcRank(String name) {
        NpcRank rank = npcRankList.get(name);

        if (rank == null) {
            rank = new NpcRank();
            rank.name = name;
            npcRankList.put(name, rank);
        }

        return rank;
    }
}
