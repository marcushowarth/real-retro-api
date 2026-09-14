package eu.howarth.real.retro.dto;

import java.math.BigDecimal;

/** A single calendar year's CPI index — the average of its monthly readings. */
public record AnnualCpiEntry(int year, BigDecimal index) {}
