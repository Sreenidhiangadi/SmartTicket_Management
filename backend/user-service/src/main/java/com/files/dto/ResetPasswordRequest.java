package com.files.dto;

public record ResetPasswordRequest(
	    String token,
	    String newPassword
	) {}
