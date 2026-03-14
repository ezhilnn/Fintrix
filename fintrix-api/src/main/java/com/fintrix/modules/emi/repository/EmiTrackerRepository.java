package com.fintrix.modules.emi.repository;
 
import com.fintrix.modules.emi.domain.EmiTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface EmiTrackerRepository
        extends JpaRepository<EmiTracker, String> {
 
    List<EmiTracker> findByUserIdAndIsActiveTrueOrderByDueDateOfMonthAsc(
            String userId);
 
    // Find EMIs due within N days — for reminder job
    @Query("SELECT e FROM EmiTracker e " +
           "WHERE e.isActive = true " +
           "AND e.dueDateOfMonth BETWEEN :fromDay AND :toDay")
    List<EmiTracker> findDueSoon(
            @Param("fromDay") int fromDay,
            @Param("toDay")   int toDay);
}