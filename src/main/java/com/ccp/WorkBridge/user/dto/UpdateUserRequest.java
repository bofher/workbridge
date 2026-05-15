package com.ccp.WorkBridge.user.dto;

import java.time.ZoneId;

public record UpdateUserRequest(
        String name,
        String email,
        String phone,
        ZoneId timeZone
) {}
