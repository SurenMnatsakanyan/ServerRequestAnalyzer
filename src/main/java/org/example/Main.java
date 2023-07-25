package org.example;


import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String[] container = new String[100];
        Arrays.fill(container, "");
        String[] commands = new String[4];
        commands[0] = "get";
        commands[1] = "set";
        commands[2] = "delete";
        commands[3] = "exit";
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        boolean condition = false;
        while (!condition) {
            String[] decisionAndData = input.split(" ");
            if (decisionAndData[0].equals(commands[3])) {
                condition = true;
            } else if (decisionAndData[0].equals(commands[0])) {
                int index = Integer.parseInt(decisionAndData[1]);
                if (index - 1 > container.length - 1 || index - 1 < 0 || container[index - 1].equals("")) {
                    System.out.println("Error");
                } else {
                    System.out.println(container[index - 1]);
                }
                input = sc.nextLine();
            } else if (decisionAndData[0].equals(commands[1])) {
                int index = Integer.parseInt(decisionAndData[1]);
                if (index - 1 > container.length - 1 || index - 1 < 0) {
                    System.out.println("Error");
                } else {
                    StringBuilder m = new StringBuilder();
                    for (int i = 2; i < decisionAndData.length; i++) {
                        if (i == decisionAndData.length - 1)
                            m.append(decisionAndData[i]);
                        else
                            m.append(decisionAndData[i]).append(" ");

                    }
                    System.out.println("OK");
                    container[index - 1] = m.toString();
                    System.out.println((index - 1) + " is " + container[index - 1]);
                }
                input = sc.nextLine();
            } else if (decisionAndData[0].equals(commands[2])) {
                int index = Integer.parseInt(decisionAndData[1]);
                if (index - 1 > container.length - 1 || index - 1 < 0) {
                    System.out.println("Error");
                } else {
                    System.out.println("OK");
                    container[index - 1] = "";
                }
                input = sc.nextLine();
            }
        }
    }

}