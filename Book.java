/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.alg_2;

/**
 *
 * @author Loq
 */

public class Book {
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;
    private int borrowCount;

    private PriorityQueue waitingList;

    public Book(String isbn, String title, String author, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.borrowCount = 0;
        this.waitingList = new PriorityQueue();
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public boolean borrowBook() {
        if (isAvailable()) {
            availableCopies--;
            borrowCount++;
            return true;
        }
        return false;
    }

    public void returnBook() {
        availableCopies++;
    }

    public void addToWaitingList(String studentName, boolean isGraduating) {
        waitingList.add(new WaitingRequest(studentName, isGraduating));
    }

    public WaitingRequest popNextWaitingBorrower() {
        return waitingList.poll();
    }

    public void updateCopies(int newTotalCopies) {
        int diff = newTotalCopies - this.totalCopies;
        this.totalCopies = newTotalCopies;
        this.availableCopies += diff;
        if (this.availableCopies < 0) {
            this.availableCopies = 0;
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void incrementBorrowCount() {
        this.borrowCount++;
    }

    public PriorityQueue getWaitingList() {
        return waitingList;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + isbn + '\'' +
                ", Title='" + title + '\'' +
                ", Author='" + author + '\'' +
                ", Available=" + availableCopies +
                "/" + totalCopies +
                ", Borrowed=" + borrowCount + " times" +
                ", Waiting=" + waitingList.size() +
                '}';
    }
}
