package backend.satellite.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class TleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String satNumber;
    private String tleString;
    private LocalDateTime lastUpdated;
    private int fetchCount; // New field to track fetch count

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSatNumber() {
        return satNumber;
    }

    public void setSatNumber(String satNumber) {   //satellite catlog numbers 
        this.satNumber = satNumber;
    }

    public String getTleString() {
        return tleString;
    }

    public void setTleString(String tleString) {
        this.tleString = tleString;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getFetchCount() {
        return fetchCount;
    }

    public void setFetchCount(int fetchCount) {
        this.fetchCount = fetchCount;
    }
}
