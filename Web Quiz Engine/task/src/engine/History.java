package engine;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
class HistPoint{
    @Id
    @NotNull
    private int histPointId;
    @NotNull
    private String userId;
    @NotNull
    private int id;
    @NotNull
    private LocalDateTime completedAt;

    public HistPoint() {}

    public int getHistPointId() {
        return histPointId;
    }

    public String getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setHistPointId(int histPointId) {
        this.histPointId = histPointId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}


