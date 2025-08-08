package com.afa.devicer.back.dto;

import com.afa.devicer.back.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.util.StringUtils;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserInfoDto {

    private UUID keycloakUuid;
    private Long personId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Character recStatus;
    private EmployeeInfoDto employeeInfo;

    @JsonIgnore
    public boolean isEmployee() {
        return employeeInfo != null;
    }

    @JsonIgnore
    public Long getEmployeeId() {
        return employeeInfo != null ? employeeInfo.getId() : null;
    }

    public Boolean getDeactivated() {
        return recStatus != null && recStatus.equals(DefaultConstants.DELETED);
    }

    @JsonIgnore
    public String getFullName() {
        if (StringUtils.isEmpty(middleName)) {
            return firstName + " " + lastName;
        } else {
            return firstName + " " + middleName + " " + lastName;
        }
    }

    @Data
    @Builder
    public static class EmployeeInfoDto {
        private Long id;
        private Character recStatus;

        public Boolean getDeactivated() {
            return recStatus.equals(DefaultConstants.DELETED);
        }
    }
}
