package com.java;

public class Manager extends Employee{
    private double bonus;

    public Manager(String name, double salary, int year, int month, int day)
    {
        super(name, salary, year, month, day);
    }

    @Override
    public double getSalary() {
        return super.getSalary() + this.bonus;
    }

    public  void setBonus(double bonus) {
        this.bonus = bonus;
    }
}
