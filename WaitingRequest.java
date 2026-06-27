/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.alg_2;

/**
 *
 * @author Loq
 */

public class WaitingRequest {
    private String studentName;
    private boolean isGraduating;
    private long timestamp;

    public WaitingRequest(String studentName, boolean isGraduating) {
        this.studentName = studentName;
        this.isGraduating = isGraduating;
        this.timestamp = System.nanoTime();
    }

    public String getStudentName() {
        return studentName;
    }

    public boolean isGraduating() {
        return isGraduating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "WaitingRequest{" +
                "Name='" + studentName + '\'' +
                ", Graduating=" + isGraduating +
                '}';
    }
}
