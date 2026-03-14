package com.fintrix.modules.emi.service;
 
import com.fintrix.common.exception.ResourceNotFoundException;
import com.fintrix.modules.emi.domain.EmiTracker;
import com.fintrix.modules.emi.dto.EmiTrackerRequest;
import com.fintrix.modules.emi.dto.EmiTrackerResponse;
import com.fintrix.modules.emi.repository.EmiTrackerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class EmiTrackerServiceImpl implements EmiTrackerService {
 
    private final EmiTrackerRepository emiRepository;
 
    @Override
    @Transactional
    public EmiTrackerResponse addEmi(String userId,
            EmiTrackerRequest request) {
        log.info("Adding EMI tracker for userId: {}", userId);
        long totalMonths = ChronoUnit.MONTHS.between(
                request.getStartDate(), request.getEndDate());
 
        EmiTracker emi = EmiTracker.builder()
                .userId(userId)
                .loanName(request.getLoanName())
                .lenderName(request.getLenderName())
                .loanType(request.getLoanType())
                .principalAmount(request.getPrincipalAmount())
                .emiAmount(request.getEmiAmount())
                .dueDateOfMonth(request.getDueDateOfMonth())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .remainingEmis((int) totalMonths)
                .reminderDaysBefore(request.getReminderDaysBefore())
                .build();
 
        return mapToResponse(emiRepository.save(emi));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<EmiTrackerResponse> getMyEmis(String userId) {
        return emiRepository
                .findByUserIdAndIsActiveTrueOrderByDueDateOfMonthAsc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
 
    @Override
    @Transactional
    public void deleteEmi(String userId, String emiId) {
        EmiTracker emi = emiRepository.findById(emiId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmiTracker", "id", emiId));
        emi.setIsActive(false);
        emiRepository.save(emi);
    }
 
    private EmiTrackerResponse mapToResponse(EmiTracker e) {
        int today    = LocalDate.now().getDayOfMonth();
        int due      = e.getDueDateOfMonth();
        boolean soon = Math.abs(due - today) <= e.getReminderDaysBefore()
                || (due < today && (30 - today + due)
                        <= e.getReminderDaysBefore());
        return EmiTrackerResponse.builder()
                .id(e.getId())
                .loanName(e.getLoanName())
                .lenderName(e.getLenderName())
                .loanType(e.getLoanType())
                .principalAmount(e.getPrincipalAmount())
                .emiAmount(e.getEmiAmount())
                .dueDateOfMonth(e.getDueDateOfMonth())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .remainingEmis(e.getRemainingEmis())
                .isDueSoon(soon)
                .dueDateLabel("Due on " + e.getDueDateOfMonth()
                        + ordinal(e.getDueDateOfMonth()) + " of every month")
                .build();
    }
 
    private String ordinal(int n) {
        if (n >= 11 && n <= 13) return "th";
        return switch (n % 10) {
            case 1  -> "st";
            case 2  -> "nd";
            case 3  -> "rd";
            default -> "th";
        };
    }
}