package com.yb.wealth.care.budget.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CommonUtil {
    // convert ids
    public static Optional<UUID> convertToUUID(String uuid) {
        try {
           return Optional.of(UUID.fromString(uuid));
        } catch (IllegalArgumentException ex) {
            log.info("Invalid UUID:{}", uuid);
        }

        return Optional.empty();
    }
}
