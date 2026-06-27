package com.mycompany.alg_2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle File Input/Output operations for Books and Borrowers.
 * Persists data in plain text format in the project root.
 * 
 * @author Antigravity
 */
public class IO {
    private static final String BOOKS_FILE = "books.txt";
    private static final String BORROWERS_FILE = "borrowers.txt";

    /**
     * Load books from books.txt into the AVL Tree.
     * If the file doesn't exist, it creates a new one with 3 default books.
     */
    public static void loadBooks(BookAVLTree tree) {
        File file = new File(BOOKS_FILE);
        if (!file.exists()) {
            // Write default 3 books
            List<Book> defaults = new ArrayList<>();
            defaults.add(new Book("978-1", "Java Programming", "Deitel", 3));
            defaults.add(new Book("978-2", "Data Structures & Algorithms", "Lafore", 2));
            defaults.add(new Book("978-3", "Clean Code", "Robert Martin", 5));
            saveBooks(defaults);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String isbn = parts[0].trim();
                    String title = parts[1].trim();
                    String author = parts[2].trim();
                    int total = Integer.parseInt(parts[3].trim());
                    
                    Book book = new Book(isbn, title, author, total);
                    if (parts.length >= 6) {
                        int available = Integer.parseInt(parts[4].trim());
                        int borrows = Integer.parseInt(parts[5].trim());
                        book.setAvailableCopies(available);
                        // Restore borrow count
                        for (int i = 0; i < borrows; i++) {
                            book.incrementBorrowCount();
                        }
                    }
                    tree.insert(book);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading books from file: " + e.getMessage());
        }
    }

    /**
     * Save the list of books to books.txt.
     */
    public static void saveBooks(List<Book> books) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                pw.println(b.getIsbn() + "," + 
                           b.getTitle() + "," + 
                           b.getAuthor() + "," + 
                           b.getTotalCopies() + "," + 
                           b.getAvailableCopies() + "," + 
                           b.getBorrowCount());
            }
        } catch (IOException e) {
            System.err.println("Error saving books to file: " + e.getMessage());
        }
    }

    /**
     * Load registered borrowers from borrowers.txt.
     * If the file doesn't exist, it creates a new one with 3 default borrowers.
     */
    public static List<LibraryUI.Borrower> loadBorrowers() {
        List<LibraryUI.Borrower> list = new ArrayList<>();
        File file = new File(BORROWERS_FILE);
        if (!file.exists()) {
            // Write default 3 borrowers
            list.add(new LibraryUI.Borrower("Ahmad", false));
            list.add(new LibraryUI.Borrower("Ola", true));
            list.add(new LibraryUI.Borrower("Samer", false));
            saveBorrowers(list);
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    boolean graduating = Boolean.parseBoolean(parts[1].trim());
                    list.add(new LibraryUI.Borrower(name, graduating));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading borrowers from file: " + e.getMessage());
        }
        return list;
    }

    /**
     * Save the list of borrowers to borrowers.txt.
     */
    public static void saveBorrowers(List<LibraryUI.Borrower> borrowers) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BORROWERS_FILE))) {
            for (LibraryUI.Borrower b : borrowers) {
                pw.println(b.getName() + "," + b.isGraduating());
            }
        } catch (IOException e) {
            System.err.println("Error saving borrowers to file: " + e.getMessage());
        }
    }
}
