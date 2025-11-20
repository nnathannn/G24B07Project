package com.example.smartair;

import java.time.LocalDate;

public class Item {
    private LocalDate date;

    public Item() {}

    public Item(LocalDate date) { this.date = date; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }
}
