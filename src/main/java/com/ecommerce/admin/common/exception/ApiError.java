package com.ecommerce.admin.common.exception;

public record ApiError(String title, String detail, String code, String property) {
}
