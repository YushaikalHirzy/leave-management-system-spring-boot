package com.example.leavemanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto implements Serializable {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String description;

    private Integer employeeCount;
}
