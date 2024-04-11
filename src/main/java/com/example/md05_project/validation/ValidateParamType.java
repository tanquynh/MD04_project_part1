package com.example.md05_project.validation;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import com.example.md05_project.model.entity.WaitingRequestStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class ValidateParamType {

    public LocalDate validateDate(String date) throws CustomException {
        try {
            return LocalDate.parse(date.trim());
        } catch (DateTimeParseException dateTimeParseException) {
            throw new CustomException("Invalid fromDate format. Please use the format 'yyyy-MM-dd'.");
        }
    }

    public Long validateId(String id) throws CustomException {
        if (id==null||id.isEmpty()) {
            throw new CustomException("Param " + id + " is mandatory");
        }
        try {
            return Long.parseLong(id.trim());
        } catch (Exception exception) {
            throw new CustomException("Invalid param.");
        }
    }

    public BorrowedCartStatus validateBorrowedCartStatus(String status) throws CustomException {
        if (status==null||status.isEmpty()) {
            throw new CustomException("Param " + status + " is mandatory");
        }
        try {
            return BorrowedCartStatus.valueOf(status.trim());
        } catch (Exception exception) {
            throw new CustomException("Invalid param.");
        }
    }

    public WaitingRequestStatus validateWaitingRequestStatus(String status) throws CustomException {
        if (status==null||status.isEmpty()) {
            throw new CustomException("Param " + status + " is mandatory");
        }
        try {
            return WaitingRequestStatus.valueOf(status.trim());
        } catch (Exception exception) {
            throw new CustomException("Invalid param.");
        }
    }

}
