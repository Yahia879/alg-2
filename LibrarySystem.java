/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.alg_2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Loq
 */

public class LibrarySystem {
    private Map<String, List<BorrowRecord>> borrowRecords;
    private static final int MAX_BORROW_LIMIT = 3;

    public LibrarySystem() {
        this.borrowRecords = new HashMap<>();
    }

    public void borrowBook(String borrowerName, boolean isGraduating, Book book, LocalDate borrowDate, LocalDate expectedReturnDate) {
        if (book == null) {
            System.out.println("Error: The passed book is null.");
            return;
        }

        int activeBorrows = countActiveBorrows(borrowerName);
        if (activeBorrows >= MAX_BORROW_LIMIT) {
            System.out.println("you have hit your limit of borrowing books the limit is 3 books");
            return;
        }

        if (hasActiveBorrowForBook(borrowerName, book.getIsbn())) {
            System.out.println("you are already borrowed the book");
            return;
        }

        if (book.borrowBook()) {
            BorrowRecord record = new BorrowRecord(borrowerName, isGraduating, book.getIsbn(), borrowDate, expectedReturnDate);
            borrowRecords.computeIfAbsent(borrowerName.toLowerCase(), k -> new ArrayList<>()).add(record);
            System.out.println("Borrowing successfully done");
        } else {
            book.addToWaitingList(borrowerName, isGraduating);
            System.out.println("book is not available in this time , you are being added to the waiting list");
        }
    }

    private void processBorrowing(String borrowerName, boolean isGraduating, Book book, LocalDate borrowDate, LocalDate expectedReturnDate) {
        if (book.borrowBook()) {
            BorrowRecord record = new BorrowRecord(borrowerName, isGraduating, book.getIsbn(), borrowDate, expectedReturnDate);
            borrowRecords.computeIfAbsent(borrowerName.toLowerCase(), k -> new ArrayList<>()).add(record);
            System.out.println("Borrowing successfully done");
        }
    }

    public void returnBook(String borrowerName, Book book) {
        if (book == null) {
            System.out.println("book not exited in library");
            return;
        }

        BorrowRecord activeRecord = findActiveRecord(borrowerName, book.getIsbn());
        if (activeRecord == null) {
            System.out.println("no active borrow record for this book");
            return;
        }

        activeRecord.setReturned(true);
        System.out.println("Book returned successfully");

        book.returnBook();
        
        if (!book.getWaitingList().isEmpty()) {
            WaitingRequest nextRequest = book.popNextWaitingBorrower();
            System.out.println("book borrowed successfully by another student");
            processBorrowing(nextRequest.getStudentName(), nextRequest.isGraduating(), book, LocalDate.now(), LocalDate.now().plusDays(14));
        } else {
            System.out.println("Available copies for book increased by 1");
        }
    }

    public void printAllRecords() {
        System.out.println("--- All Borrow Records in the System ---");
        if (borrowRecords.isEmpty()) {
            System.out.println("No borrow records exist currently");
        } else {
            for (List<BorrowRecord> list : borrowRecords.values()) {
                for (BorrowRecord r : list) {
                    System.out.println(r);
                }
            }
        }
        System.out.println("----------------------------------------");
    }

    private int countActiveBorrows(String borrowerName) {
        List<BorrowRecord> list = borrowRecords.get(borrowerName.toLowerCase());
        if (list == null) {
            return 0;
        }
        int count = 0;
        for (BorrowRecord r : list) {
            if (!r.isReturned()) {
                count++;
            }
        }
        return count;
    }

    private boolean hasActiveBorrowForBook(String borrowerName, String isbn) {
        List<BorrowRecord> list = borrowRecords.get(borrowerName.toLowerCase());
        if (list == null) {
            return false;
        }
        for (BorrowRecord r : list) {
            if (r.getBookIsbn().equals(isbn) && !r.isReturned()) {
                return true;
            }
        }
        return false;
    }

    private BorrowRecord findActiveRecord(String borrowerName, String isbn) {
        List<BorrowRecord> list = borrowRecords.get(borrowerName.toLowerCase());
        if (list == null) {
            return null;
        }
        for (BorrowRecord r : list) {
            if (r.getBookIsbn().equals(isbn) && !r.isReturned()) {
                return r;
            }
        }
        return null;
    }

    public void generateReports(BookAVLTree bookTree) {
        if (bookTree == null) return;

        List<Book> books = new ArrayList<>();
        collectBooks(bookTree.getRoot(), books);

        if (books.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }

        System.out.println("--- Most Borrowed Books ---");
        List<Book> sortedBooks = new ArrayList<>(books);
        sortedBooks.sort((b1, b2) -> Integer.compare(b2.getBorrowCount(), b1.getBorrowCount()));
        for (int i = 0; i < Math.min(5, sortedBooks.size()); i++) {
            Book b = sortedBooks.get(i);
            if (b.getBorrowCount() > 0) {
                System.out.println(b.getTitle() + " (ISBN: " + b.getIsbn() + ") - Borrowed " + b.getBorrowCount() + " times");
            }
        }

        System.out.println("\n--- Most Read Authors ---");
        Map<String, Integer> authorStats = new HashMap<>();
        for (Book b : books) {
            authorStats.put(b.getAuthor(), authorStats.getOrDefault(b.getAuthor(), 0) + b.getBorrowCount());
        }
        List<Map.Entry<String, Integer>> sortedAuthors = new ArrayList<>(authorStats.entrySet());
        sortedAuthors.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));
        for (int i = 0; i < Math.min(5, sortedAuthors.size()); i++) {
            Map.Entry<String, Integer> e = sortedAuthors.get(i);
            if (e.getValue() > 0) {
                System.out.println(e.getKey() + ": " + e.getValue() + " borrows");
            }
        }

        System.out.println("\n--- Available Books ---");
        int availableCopies = 0;
        for (Book b : books) {
            availableCopies += b.getAvailableCopies();
        }
        System.out.println("Total available copies: " + availableCopies);
    }

    private void collectBooks(AVLNode node, List<Book> books) {
        if (node == null) return;
        collectBooks(node.left, books);
        books.add(node.book);
        collectBooks(node.right, books);
    }

    public void addNewCopies(BookAVLTree bookTree, String isbn, int additionalCopies) {
        if (bookTree == null || additionalCopies <= 0) return;

        Book book = bookTree.search(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        book.updateCopies(book.getTotalCopies() + additionalCopies);

        while (book.getAvailableCopies() > 0 && !book.getWaitingList().isEmpty()) {
            WaitingRequest next = book.popNextWaitingBorrower();
            this.borrowBook(next.getStudentName(), next.isGraduating(), book, LocalDate.now(), LocalDate.now().plusDays(14));
        }
    }

    // ==========================================
    // For Cards
    // ==========================================

    public int getActiveBorrowsCount() {
        int active = 0;
        for (List<BorrowRecord> list : borrowRecords.values()) {
            for (BorrowRecord r : list) {
                if (!r.isReturned()) {
                    active++;
                }
            }
        }
        return active;
    }

    public int getTotalWaitingRequestsCount(BookAVLTree bookTree) {
        if (bookTree == null) return 0;
        int count = 0;
        for (Book b : bookTree.getAllBooks()) {
            count += b.getWaitingList().size();
        }
        return count;
    }

    public List<Map.Entry<String, Integer>> getTopAuthors(BookAVLTree bookTree, int limit) {
        if (bookTree == null) return new ArrayList<>();
        Map<String, Integer> authorStats = new HashMap<>();
        for (Book b : bookTree.getAllBooks()) {
            authorStats.put(b.getAuthor(), authorStats.getOrDefault(b.getAuthor(), 0) + b.getBorrowCount());
        }
        List<Map.Entry<String, Integer>> sortedAuthors = new ArrayList<>(authorStats.entrySet());
        sortedAuthors.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));
        List<Map.Entry<String, Integer>> topAuthors = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sortedAuthors.size()); i++) {
            Map.Entry<String, Integer> e = sortedAuthors.get(i);
            if (e.getValue() > 0) {
                topAuthors.add(e);
            }
        }
        return topAuthors;
    }

    public List<BorrowRecord> searchRecords(String query) {
        List<BorrowRecord> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            for (List<BorrowRecord> list : borrowRecords.values()) {
                results.addAll(list);
            }
            return results;
        }
        
        String q = query.toLowerCase().trim();
        for (List<BorrowRecord> list : borrowRecords.values()) {
            for (BorrowRecord r : list) {
                if (r.getBorrowerName().toLowerCase().contains(q) || 
                    r.getBookIsbn().contains(q) || 
                    String.valueOf(r.getRecordId()).contains(q)) {
                    results.add(r);
                }
            }
        }
        return results;
    }
}

