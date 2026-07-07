package com.mindmirror.client;

/** Raised when the FastAPI AI microservice cannot be reached or returns an error. */
public class AiServiceException extends RuntimeException {
    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
