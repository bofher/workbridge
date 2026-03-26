package com.ccp.WorkBridge.dto;

public record ApiResponse<T>(
        boolean isSuccess,
        String message,
        T data
) {
    public static ApiResponse<Void> success() {
        return new ApiResponse<Void>(true, "success", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data ) {
        return new ApiResponse<>(true, message, data);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
