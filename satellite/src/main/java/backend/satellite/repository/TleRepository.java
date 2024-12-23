package backend.satellite.repository;

import backend.satellite.model.TleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TleRepository extends JpaRepository<TleData, Long> {
    TleData findBySatNumber(String satNumber);

    @Modifying
    @Transactional
    @Query("DELETE FROM TleData t WHERE t.satNumber = :satNumber")
    void deleteBySatNumber(@Param("satNumber") String satNumber);


    @Query(value = "SELECT * FROM tle_data ORDER BY fetch_count DESC LIMIT 1", nativeQuery = true)
    TleData findMostFetched();
}