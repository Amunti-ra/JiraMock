package com.newjirasystem.app.exception;

import java.time.Instant;

public record ErrorResponse(String errorCode,
                            String mensaje,
                            int status,
                            Instant timeStamp) {

}
