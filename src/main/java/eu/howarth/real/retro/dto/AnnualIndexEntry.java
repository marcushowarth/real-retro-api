package eu.howarth.real.retro.dto;

import java.math.BigDecimal;

/** A single calendar year's price-index reading — the average of its monthly readings. */
public record AnnualIndexEntry(int year, BigDecimal index) {}
