package com.example.scalablechat.util;

import java.util.UUID;

public final class RequestId {
    private RequestId() {
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }
}
