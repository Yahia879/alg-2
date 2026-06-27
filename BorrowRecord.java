/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.alg_2;

import java.time.LocalDate;

/**
 *
 * @author Loq
 */

public class BorrowRecord {
    private static int idCounter = 1;
    
    private int recordId;
    private String borrowerName;
    private boolean isGraduating;
    private String bookIsbn;
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private boolean isReturned;

    public BorrowRecord(String borrowerName, boolean isGraduating, String bookIsbn, LocalDate borrowDate, LocalDate expectedReturnDate) {
        this.recordId = idCounter++;
        this.borrowerName = borrowerName;
        this.isGraduating = isGraduating;
        this.bookIsbn = bookIsbn;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.isReturned = false;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public boolean isGraduating() {
        return isGraduating;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public void setGraduating(boolean graduating) {
        this.isGraduating = graduating;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "ID=" + recordId +
                ", Borrower='" + borrowerName + '\'' +
                ", Graduating=" + isGraduating +
                ", ISBN='" + bookIsbn + '\'' +
                ", BorrowDate=" + borrowDate +
                ", DueDate=" + expectedReturnDate +
                ", Status=" + (isReturned ? "Returned" : "Active") +
                '}';
    }
}
