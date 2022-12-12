package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class DemoApplication {
	
	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);
		
		System.out.print("Welcome, enter <1> for Math Calculation, <0> for Exit: ");
		Scanner sc = new Scanner(System.in);
		int selected = 0;
		selected = sc.nextInt();
		sc.nextLine();
		
		while(selected > -1) {
			switch(selected) {
			case 0 :
				System.out.println("Bye.");
				System.exit(0);
				break;
			case 1:
				System.out.print("What is your math expression? (enter <exit> to exit) : ");
				String mathExpression = sc.nextLine();
				
				try {
					if(StringUtils.isNotBlank(mathExpression)) {
						if(mathExpression.equals("exit")) {
							System.out.println("Bye.");
							System.exit(0);
						}
					}
					
					double result = calculate(mathExpression);
					
					System.out.print("Result : " + result);
					System.out.println();
					System.out.println("------------------------------");
				} catch (Exception e) {
					System.out.println("------------------------------");
					System.out.println("Invalid math expression. Kindly input again. ");
					System.out.println("------------------------------");
				}
				
				break;
			default:
				System.out.println("Invalid option. Enter 1 to start match calculation, 0 for exit");
				break;
			}
			
			System.out.print("Next? ( <1> for Math Calculation, <0> for Exit ) :");
			selected = sc.nextInt();
			sc.nextLine();
		}
	}

	public static double calculate(String sum) throws Exception {
		
		String regex = "[0-9\\(\\)+-/*]+";
		String numRegex = "[0-9]*\\.?[0-9]*";
		String expRegex = "(?<=[\\d.])(?=[^\\d.])|(?<=[^\\d.])(?=[\\d.])|(?=[()])|(?<=[()])";
		
		String defaultOperator = "+";
		
		Stack<Node> stack = new Stack<>();
		stack.push(new Node(defaultOperator, (double) 0));
		
		if(sum != null && sum != "" && sum != " ") {
			String[] arrOfSum = null;
			
			sum = sum.replaceAll(" ", "");
			if(sum.matches(regex)) {
				arrOfSum = sum.split(expRegex);
			}
			
			for(int i = 0; i < arrOfSum.length; i++) {
				Node top = stack.peek();
				
				if(arrOfSum[i].matches(numRegex)) {
					int j = i + 1;
					double currValue = Double.parseDouble(arrOfSum[i]);
					if(j >= arrOfSum.length) {
						// do nothing
					} else {
						if(arrOfSum[j].equals("*")) {
							if(arrOfSum[j+1].matches(numRegex)) {
								double nextValue = Double.parseDouble(arrOfSum[j+1]);
								currValue = currValue * nextValue;
								defaultOperator = null;	// reset
								arrOfSum[j+1] = "0";
							}
						} else if(arrOfSum[j].equals("/")) {
							if(arrOfSum[j+1].matches(numRegex)) {
								double nextValue = Double.parseDouble(arrOfSum[j+1]);
								currValue = currValue / nextValue;
								defaultOperator = null;	// reset
								arrOfSum[j+1] = "0";
							}
						}
					}
					
					Node n = new Node(defaultOperator != null ? defaultOperator : "+", currValue);
					defaultOperator = null;	// reset
					top.list.add(n);
				} else if(arrOfSum[i].equals("(")) {
					Node n = new Node(defaultOperator, null);
					defaultOperator = null;	// reset
					top.list.add(n);
					stack.push(n);
				} else if(arrOfSum[i].equals(")")) {
					double value = stack.pop().evaluate();
					top = stack.peek();
					top.list.get(top.list.size() - 1).value = value;
				} else {
					defaultOperator = arrOfSum[i];
				}
				
//				for(Node node : top.list) {
//					System.out.println("Math expression : " + node.operator + " --> Value : " + node.value);
//				}
			}
			
//			return doSimpleMath(arrOfSum);
			return stack.peek().evaluate();
//			System.out.println("Math expression : " + Arrays.toString(arrOfSum));
		}
		
		throw new IllegalArgumentException("Invalid math expression.");
	}
	
	public static class Node {
		String operator;
		Double value;
		List<Node> list;
		
		public Node(String operator, Double value) {
			this.operator = operator;
			this.value = value;
			this.list = new ArrayList<>();
		}
		
		public double evaluate() {
			double total = 0;
			
			for(Node n : list) {
//				if(n.operator.equals("*")) {
//					total = total * n.value;
//				} else if(n.operator.equals("/")) {
//					total = total / n.value;
//				} else 
				if(n.operator.equals("+")) {
					total = total + n.value;
				} else if(n.operator.equals("-")) {
					total = total - n.value;
				}
			}
			
			return total;
		}
	}
}
