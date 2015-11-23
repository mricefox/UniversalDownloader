package com.mf.bourne;

public class Entrance {
    public static void main(String[] args) {
        System.out.println("111");

        try {
            fun();
        } catch (CustomException e) {
            e.printStackTrace();
            System.out.println("333");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("444");
        }
        System.out.println("222");
    }

    public static void fun() throws CustomException{
        System.out.println("fun");
        throw new RuntimeException();
    }

    public static class CustomException extends Exception {
        public CustomException() {
            super("CustomException");
        }
    }
}
