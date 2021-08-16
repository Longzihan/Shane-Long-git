package com.java;

import java.time.LocalDate;
import java.util.Random;

public class Employee {
    private static int nextID;

    private String name;
    private int ID;
    private double salary;
    private LocalDate hireDay;

    static
    {
        Random generator = new Random();
        nextID = generator.nextInt(100000);
    }

    {
        ID = nextID;
        nextID++;
    }

    public Employee(String name, double salary, int year, int month, int day) {
        this.name = name;
        this.salary = salary;
        this.hireDay = LocalDate.of(year, month, day);
    }

    public Employee(double salary, int year, int month, int day)
    {
        this("Employee #" + nextID, salary, year, month, day);
    }

    public Employee() {}

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.ID;
    }

    public double getSalary() {
        return this.salary;
    }

    public LocalDate getHireDay()
    {
        return this.hireDay;
    }

    public void raiseSalary(int byPercent)
    {
        this.salary *= (byPercent/100) + 1;
    }
}
