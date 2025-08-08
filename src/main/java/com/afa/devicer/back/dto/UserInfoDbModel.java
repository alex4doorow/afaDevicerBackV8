package com.afa.devicer.back.dto;

public interface UserInfoDbModel {
    // Person info
    Long getPersonId();
    String getLastName();
    String getMiddleName();
    String getFirstName();
    String getFullName();
    Character getRecStatus();
    void setPersonId(Long personId);
    void setLastName(String lastName);
    void setMiddleName(String middleName);
    void setFirstName(String firstName);
    void setFullName(String fullName);
    void setRecStatus(Character recStatus);

    // Employee info
    Long getEmployeeId();
    void setEmployeeId(Long employeeId);
}

