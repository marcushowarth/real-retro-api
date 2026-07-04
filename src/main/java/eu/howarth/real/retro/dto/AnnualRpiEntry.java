package eu.howarth.real.retro.dto;

import java.math.BigDecimal;

/** A single calendar year's RPI index — the average of its monthly readings. */
public record AnnualRpiEntry(int year, BigDecimal index) {}
