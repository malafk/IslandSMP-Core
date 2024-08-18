package lol.maltest.islandsmp.manager;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.entities.QueuePlayer;
import lol.maltest.islandsmp.utils.HexUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueManager {

    private LinkedList<QueuePlayer> playersQueue = new LinkedList<>();
    private final int concurrentLimit = 3; // Limit of concurrent players being processed
    private AtomicInteger currentProcessing = new AtomicInteger(0); // Tracks the number of players currently being processed

    private long currentBatchStartTime = -1;

    private IslandSMP plugin;

    public QueueManager(IslandSMP plugin) {
        this.plugin = plugin;
    }

    public void removePlayer(QueuePlayer qP) {
        playersQueue.remove(qP);
        processPlayers();
    }

    public void removePlayer(UUID pUuid) {
        playersQueue.removeIf(queuePlayer -> queuePlayer.getPlayer().equals(pUuid));
    }

    public void addPlayer(QueuePlayer qP) {
        playersQueue.add(qP);
        new BukkitRunnable() {
            @Override
            public void run() {
                OfflinePlayer player = Bukkit.getPlayer(qP.getPlayer());
                if (player != null && player.isOnline()) {
                    int getEstimatedTime = calculateWaitTime(qP);

                    String message = qP.getStatus() == QueuePlayer.QueueStatus.WAITING ?
                            "&#8FE97BYou are in queue for creating an island! &7(" +  getEstimatedTime + "s estimated till done)":
                            "&#69FE5FYour island is being created!";
                    player.getPlayer().sendActionBar(HexUtils.colour(message));
                }

                if (qP.getStatus() == QueuePlayer.QueueStatus.COMPLETE) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 2);

        processPlayers();
    }

    public void processPlayers() {
        while (currentProcessing.get() < concurrentLimit && !playersQueue.isEmpty()) {
            QueuePlayer player = playersQueue.poll();
            currentProcessing.incrementAndGet();
                performTaskForPlayer(player);
        }
    }

    private void performTaskForPlayer(QueuePlayer qP) {
        qP.setStatus(QueuePlayer.QueueStatus.CREATING);
        Player player = Bukkit.getPlayer(qP.getPlayer());
        if (player == null) {
            removePlayer(qP);
            return;
        }
        plugin.getGridManager().createIsland(player, () -> {
            qP.setStatus(QueuePlayer.QueueStatus.COMPLETE);
            removePlayer(qP);
            currentProcessing.decrementAndGet(); // Decrement the count of processing tasks
            processPlayers(); // Check if more players can be processed
        });
    }

    private int calculateWaitTime(QueuePlayer player) {
        int positionInQueue = playersQueue.indexOf(player);
        int batchesBeforePlayer = positionInQueue / concurrentLimit;

        int averageProcessingTimePerBatch = 22; // Assuming 22 seconds per batch
        int estimatedTime = batchesBeforePlayer * averageProcessingTimePerBatch;

        long currentTime = System.currentTimeMillis();
        int timeSpentOnCurrentBatch = 0;
        if (currentBatchStartTime != -1) {
            timeSpentOnCurrentBatch = (int)((currentTime - currentBatchStartTime) / 1000);
        }

        estimatedTime -= timeSpentOnCurrentBatch;
        return Math.max(estimatedTime, 0);
    }
}
