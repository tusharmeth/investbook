package ru.portfolio.portfolio.pojo;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class Issuer {
    @NotNull
    private final Long inn;
    @NotNull
    private final String name;
}