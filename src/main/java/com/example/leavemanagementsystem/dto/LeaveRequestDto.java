package com.example.leavemanagementsystem.dto;

import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDto {
    private Long id;

    private Long employeeId;

    private String employeeName;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private LeaveType leaveType;

    @Size(max = 500)
    private String reason;

    private LeaveStatus status;

    private String comment;

    private Long approvedById;

    private String approvedByName;

    private LocalDateTime requestDate;

    private LocalDateTime responseDate;
}
