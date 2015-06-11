package com.wolf.annotation;

import java.util.Date;

import com.wolf.annotation.FruitColor.Color;

public class Apple {
	@FruitName(value = "test")
	private String applyName;
	@FruitColor(fruitColor = Color.RED)
	private String applyColor;

	public Apple(String applyName, String applyColor) {
		super();
		this.applyName = applyName;
		this.applyColor = applyColor;
	}

	public String getApplyName() {
		return applyName;
	}

	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	public String getApplyColor() {
		return applyColor;
	}

	public void setApplyColor(String applyColor) {
		this.applyColor = applyColor;
	}

	 public void displayName(){
        System.out.println("...........................");
    }
	 
	public static void main(String[] args) {
		System.out.println(new Date());
		Apple a  = new Apple("name", "color");
		System.out.println(a.getApplyColor() + "== "+a.getApplyName());
	}
}
