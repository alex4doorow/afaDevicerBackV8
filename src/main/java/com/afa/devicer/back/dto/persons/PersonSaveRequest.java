package com.afa.devicer.back.dto.persons;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "person save request")
public class PersonSaveRequest {

    @Schema(description = "keycloak uuid (for users only)")
    private UUID keycloakUuid;

    @NotBlank
    @Size(min = 2, max = 64)
    @Schema(description = "first name", example = "Алексей")
    private String firstName;

    @Size(max = 32)
    @Schema(description = "middle name", example = "Степанович")
    private String middleName;

    @Size(min = 2, max = 64)
    @Schema(description = "last name", example = "Егоров")
    private String lastName;

    @NotBlank
    @Size(min = 15, max = 15)
    @Schema(description = "phone number", example = "(916) 111-11-22")
    private String phoneNumber;

    @Column(name = "email")
    private String email;
}
