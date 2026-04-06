package eu.howarth.real.income.model;

/**
 * Single ONS RPI CHAW annual data point.
 * year: calendar year (e.g. 2023)
 * index: RPI value (Jan 1987 = 100 base)
 */
public record RpiEntry(int year, double index) {}
