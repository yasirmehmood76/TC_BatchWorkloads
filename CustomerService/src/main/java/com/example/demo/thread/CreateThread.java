package com.example.demo.thread;

import org.springframework.stereotype.Service;


public class CreateThread extends Thread {
    private String threadName;

    public CreateThread(String name) {
        this.threadName = name;
    }

    @Override
    public void run() {
        System.out.println(threadName + " is running.");
        try {
            for(int i=0; i<100000;i++) {
                int sum= 2+2;
            }
            Thread.sleep(5000); // Simulate some work
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(threadName + " finished.");
    }
}
