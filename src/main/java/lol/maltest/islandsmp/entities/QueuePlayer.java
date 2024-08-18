package lol.maltest.islandsmp.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
public class QueuePlayer {

    public enum QueueStatus {
        WAITING,
        CREATING,
        COMPLETE
    }

    private final UUID player;
    @Setter private QueueStatus status;
    private Long startDate;

    public QueuePlayer(UUID player) {
        this.player = player;
        this.startDate = new Date().getTime() / 1000; // We dont want timeMs
        this.status = QueueStatus.WAITING;
    }

    public void resetStartDate() {
        this.startDate = new Date().getTime() / 1000;
    }

}
