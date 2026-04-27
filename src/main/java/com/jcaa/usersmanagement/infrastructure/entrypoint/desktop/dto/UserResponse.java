package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto;

// Clean Code - Regla 15 (inmutabilidad como preferencia):
// Los DTOs de respuesta se modelan como records para garantizar inmutabilidad.
public record UserResponse(
    String id,
    String name,
    String email,
    String role,
    String status) {}
