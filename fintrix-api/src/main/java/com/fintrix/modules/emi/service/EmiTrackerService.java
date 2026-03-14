package com.fintrix.modules.emi.service;
 
import com.fintrix.modules.emi.dto.EmiTrackerRequest;
import com.fintrix.modules.emi.dto.EmiTrackerResponse;
 
import java.util.List;
 
public interface EmiTrackerService {
    EmiTrackerResponse   addEmi(String userId, EmiTrackerRequest request);
    List<EmiTrackerResponse> getMyEmis(String userId);
    void                 deleteEmi(String userId, String emiId);
}