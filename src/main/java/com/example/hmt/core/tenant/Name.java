package com.example.hmt.core.tenant;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Name {
    @NotBlank(message = "First Name can not be blank")
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last Name can not be blank")
    private String lastName;

    public String getName() {
        if (middleName != null && !middleName.isBlank()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
}
