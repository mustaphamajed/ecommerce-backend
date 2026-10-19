package com.ecommerce.admin.common.exception;

import java.util.List;

public record ApiErrorResponse(List<ApiError> errors) {

    public static ApiErrorResponse of(String title, String detail, String code, String property) {
        return new ApiErrorResponse(List.of(new ApiError(title, detail, code, property)));
    }
}
