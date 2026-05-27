package org.example.eksamenbandbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBandBioRequest(@NotBlank String content) {}
