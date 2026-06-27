package com.mycompany.alg_2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Swing User Interface for the Digital Library Management System.
 * Connects directly to the existing classes without modifying them.
 * 
 * Improvements:
 * - Removed pre-seeded sample data (starts clean).
 * - Added a dedicated Borrowers Management tab.
 * - Improved Borrow & Return via dropdown selectors for registered books and borrowers.
 * - Added interactive search and live filtering for all tables.
 * - Adheres strictly to the instructions (do not touch existing files).
 * 
 * @author Antigravity
 */
public class LibraryUI extends JFrame {

    private final BookAVLTree bookTree;
    private final LibrarySystem librarySystem;

    // GUI Themes Colors
    private static final Color BG_DARK = new Color(24, 24, 28);
    private static final Color CARD_DARK = new Color(33, 33, 39);
    private static final Color TEXT_LIGHT = new Color(240, 240, 245);
    private static final Color ACCENT_PRIMARY = new Color(110, 68, 255);
    private static final Color ACCENT_SECONDARY = new Color(3, 218, 198);
    private static final Color INPUT_BG = new Color(42, 42, 50);
    private static final Color BORDER_COLOR = new Color(55, 55, 65);

    // Font definitions
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_CONSOLE = new Font("Consolas", Font.PLAIN, 12);

    // Card Layout Panels
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Registered Borrowers List (managed locally in UI)
    private final List<Borrower> registeredBorrowers = new ArrayList<>();

    // Component References for Refreshing Data
    private DefaultTableModel booksTableModel;
    private DefaultTableModel borrowTableModel;
    private DefaultTableModel waitingListTableModel;
    private DefaultTableModel borrowersTableModel;
    
    private TableRowSorter<DefaultTableModel> booksSorter;
    private TableRowSorter<DefaultTableModel> borrowSorter;
    private TableRowSorter<DefaultTableModel> borrowersSorter;

    private JTable booksTable;
    private JTable borrowTable;
    private JTable waitingListTable;
    private JTable borrowersTable;

    // Dropdown selectors for Borrow & Return form
    private JComboBox<Borrower> cmbBorrower;
    private JComboBox<BookItem> cmbBook;
    private JTextField txtSearchRecords;

    // Dashboard Info Labels
    private JLabel lblTotalBooksVal;
    private JLabel lblTotalCopiesVal;
    private JLabel lblActiveBorrowsVal;
    private JLabel lblWaitingRequestsVal;
    private JLabel lblTotalAvailableVal;
    private DefaultTableModel topBooksModel;
    private DefaultTableModel topAuthorsModel;



    // Internal class representation for Book representation in ComboBoxes
    private static class BookItem {
        private final Book book;

        public BookItem(Book book) {
            this.book = book;
        }

        public Book getBook() {
            return book;
        }

        @Override
        public String toString() {
            return book.getTitle() + " (ISBN: " + book.getIsbn() + ")";
        }
    }

    // Local class representation for registered borrower
    public static class Borrower {
        private String name;
        private boolean graduating;

        public Borrower(String name, boolean graduating) {
            this.name = name;
            this.graduating = graduating;
        }

        public String getName() {
            return name;
        }

        public boolean isGraduating() {
            return graduating;
        }

        @Override
        public String toString() {
            return name + (graduating ? " (Graduating)" : " (Normal)");
        }
    }

    public LibraryUI() {
        // Initialize Core Library Instances
        this.bookTree = new BookAVLTree();
        this.librarySystem = new LibrarySystem();

        // Load persisted books and borrowers from file using IO helper class
        IO.loadBooks(bookTree);
        registeredBorrowers.addAll(IO.loadBorrowers());

        // Configure Frame Properties
        setTitle("Digital Library Management System");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // Initialize and Set Up UI Panels
        setupLeftSidebar();
        setupMainContentArea();



        // Refresh Data on Startup
        refreshAllData();
    }

    private void setupLeftSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(CARD_DARK);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // System Logo/Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(CARD_DARK);
        titlePanel.setBorder(new EmptyBorder(25, 10, 25, 10));
        JLabel systemTitle = new JLabel("LIB-SYSTEM GUI");
        systemTitle.setForeground(ACCENT_SECONDARY);
        systemTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(systemTitle);
        sidebar.add(titlePanel);

        // Sidebar Navigation Buttons
        sidebar.add(createSidebarButton("Dashboard & Reports", "DASHBOARD"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarButton("Books Management", "BOOKS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarButton("Borrowers Management", "BORROWERS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarButton("Borrow & Return Desk", "BORROW"));


        sidebar.add(Box.createVerticalGlue());



        add(sidebar, BorderLayout.WEST);
    }

    private JButton createSidebarButton(String text, final String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_HEADER);
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(CARD_DARK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(210, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Styling hover effects
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(INPUT_BG);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(CARD_DARK);
            }
        });

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            refreshAllData();
        });

        return btn;
    }

    private void setupMainContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);

        // Add individual views
        contentPanel.add(createDashboardPanel(), "DASHBOARD");
        contentPanel.add(createBooksPanel(), "BOOKS");
        contentPanel.add(createBorrowersPanel(), "BORROWERS");
        contentPanel.add(createBorrowPanel(), "BORROW");


        add(contentPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // 1. Dashboard and Analytics Panel
    // ==========================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Title
        JLabel title = new JLabel("Library Analytics Dashboard");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);

        // Core Counters Cards (Grid Layout)
        JPanel countersGrid = new JPanel(new GridLayout(1, 5, 12, 0));
        countersGrid.setBackground(BG_DARK);
        countersGrid.setBorder(new EmptyBorder(20, 0, 20, 0));

        lblTotalBooksVal = new JLabel("0");
        lblTotalCopiesVal = new JLabel("0");
        lblTotalAvailableVal = new JLabel("0");
        lblActiveBorrowsVal = new JLabel("0");
        lblWaitingRequestsVal = new JLabel("0");

        countersGrid.add(createCounterCard("Total Book Titles", lblTotalBooksVal, ACCENT_PRIMARY));
        countersGrid.add(createCounterCard("Total Book Copies", lblTotalCopiesVal, ACCENT_SECONDARY));
        countersGrid.add(createCounterCard("Available Copies", lblTotalAvailableVal, new Color(46, 204, 113)));
        countersGrid.add(createCounterCard("Active Borrows", lblActiveBorrowsVal, new Color(255, 110, 110)));
        countersGrid.add(createCounterCard("Waiting Requests", lblWaitingRequestsVal, new Color(255, 193, 7)));

        // Analytical Reports (Tables)
        JPanel reportsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        reportsPanel.setBackground(BG_DARK);

        // Table 1: Most Borrowed Books
        JPanel leftReport = new JPanel(new BorderLayout());
        leftReport.setBackground(CARD_DARK);
        leftReport.setBorder(createTitledBorder("Top Borrowed Books"));

        topBooksModel = new DefaultTableModel(new String[]{"Book Title", "ISBN", "Borrows"}, 0);
        JTable topBooksTable = createStyledTable(topBooksModel);
        leftReport.add(new JScrollPane(topBooksTable), BorderLayout.CENTER);

        // Table 2: Top Read Authors
        JPanel rightReport = new JPanel(new BorderLayout());
        rightReport.setBackground(CARD_DARK);
        rightReport.setBorder(createTitledBorder("Top Read Authors"));

        topAuthorsModel = new DefaultTableModel(new String[]{"Author Name", "Borrows Count"}, 0);
        JTable topAuthorsTable = createStyledTable(topAuthorsModel);
        rightReport.add(new JScrollPane(topAuthorsTable), BorderLayout.CENTER);

        reportsPanel.add(leftReport);
        reportsPanel.add(rightReport);

        JPanel mainCenter = new JPanel(new BorderLayout());
        mainCenter.setBackground(BG_DARK);
        mainCenter.add(countersGrid, BorderLayout.NORTH);
        mainCenter.add(reportsPanel, BorderLayout.CENTER);

        panel.add(mainCenter, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCounterCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_DARK);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(170, 170, 180));
        card.add(titleLbl, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accent);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // 2. Books Management Panel
    // ==========================================
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Row: Title & Search bar
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(BG_DARK);
        topRow.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Books Management & Inventory");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_LIGHT);
        topRow.add(title, BorderLayout.WEST);

        // Instant Live Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(5, 0));
        searchBar.setBackground(BG_DARK);
        JLabel lblSearch = new JLabel("Search Books: ");
        lblSearch.setFont(FONT_BODY);
        lblSearch.setForeground(TEXT_LIGHT);
        JTextField txtSearch = createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(200, 30));
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        topRow.add(searchBar, BorderLayout.EAST);

        panel.add(topRow, BorderLayout.NORTH);

        // Split Layout: Left Form, Right Table
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_DARK);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                createTitledBorder("Book Details"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setPreferredSize(new Dimension(300, 400));

        JTextField txtIsbn = createStyledTextField();
        JTextField txtTitle = createStyledTextField();
        JTextField txtAuthor = createStyledTextField();
        JTextField txtCopies = createStyledTextField();

        formPanel.add(createFormLabel("ISBN:"));
        formPanel.add(txtIsbn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(createFormLabel("Title:"));
        formPanel.add(txtTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(createFormLabel("Author:"));
        formPanel.add(txtAuthor);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(createFormLabel("Total Copies:"));
        formPanel.add(txtCopies);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Action Buttons Panel (2x2 Grid)
        JPanel actionBtnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        actionBtnGrid.setBackground(CARD_DARK);
        JButton btnAdd = createStyledButton("Add Book", ACCENT_PRIMARY);
        JButton btnUpdate = createStyledButton("Update Book", new Color(130, 90, 240));
        JButton btnDelete = createStyledButton("Delete Book", new Color(220, 80, 80));
        JButton btnClear = createStyledButton("Clear", new Color(120, 120, 130));
        
        actionBtnGrid.add(btnAdd);
        actionBtnGrid.add(btnUpdate);
        actionBtnGrid.add(btnDelete);
        actionBtnGrid.add(btnClear);
        formPanel.add(actionBtnGrid);

        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Right side: Split Pane for Books Table (Top) and Waiting List (Bottom)
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        rightPanel.setBackground(BG_DARK);

        // Books Table ScrollPane
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_DARK);
        tableContainer.setBorder(createTitledBorder("Registered Books (AVL Balanced Tree)"));
        booksTableModel = new DefaultTableModel(new String[]{"ISBN", "Title", "Author", "Available / Total", "Borrows"}, 0);
        booksTable = createStyledTable(booksTableModel);
        
        // Add RowSorter for Live Filtering
        booksSorter = new TableRowSorter<>(booksTableModel);
        booksTable.setRowSorter(booksSorter);

        tableContainer.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        rightPanel.add(tableContainer);

        // Waiting List Table ScrollPane
        JPanel waitingListContainer = new JPanel(new BorderLayout());
        waitingListContainer.setBackground(CARD_DARK);
        waitingListContainer.setBorder(createTitledBorder("Selected Book Waiting List (Priority Queue)"));
        waitingListTableModel = new DefaultTableModel(new String[]{"Queue Position", "Student Name", "Is Graduating?"}, 0);
        waitingListTable = createStyledTable(waitingListTableModel);
        waitingListContainer.add(new JScrollPane(waitingListTable), BorderLayout.CENTER);
        rightPanel.add(waitingListContainer);

        // GridBagConstraints assignment
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        mainContent.add(formPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 15, 0, 0);
        mainContent.add(rightPanel, gbc);

        panel.add(mainContent, BorderLayout.CENTER);

        // AVL Search integration for the search box
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    // Restore all books
                    refreshBooksTable();
                } else {
                    // Search using the AVL Tree search method (O(log N) complexity)
                    Book foundBook = bookTree.search(text);
                    booksTableModel.setRowCount(0);
                    if (foundBook != null) {
                        booksTableModel.addRow(new Object[]{
                                foundBook.getIsbn(),
                                foundBook.getTitle(),
                                foundBook.getAuthor(),
                                foundBook.getAvailableCopies() + " / " + foundBook.getTotalCopies(),
                                foundBook.getBorrowCount()
                        });
                    }
                }
            }
        });

        // Event Listeners for Books Table
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int modelRow = booksTable.convertRowIndexToModel(selectedRow);
                        if (modelRow >= 0 && modelRow < booksTableModel.getRowCount()) {
                            String isbn = booksTableModel.getValueAt(modelRow, 0).toString();
                            String titleStr = booksTableModel.getValueAt(modelRow, 1).toString();
                            String author = booksTableModel.getValueAt(modelRow, 2).toString();
                            String copiesText = booksTableModel.getValueAt(modelRow, 3).toString(); 
                            String totalCopies = copiesText.substring(copiesText.indexOf('/') + 1).trim();

                            txtIsbn.setText(isbn);
                            txtTitle.setText(titleStr);
                            txtAuthor.setText(author);
                            txtCopies.setText(totalCopies);

                            // Load waiting list of this book
                            refreshWaitingList(isbn);
                        }
                    } catch (Exception ex) {
                        // Ignore transition exceptions during table refresh
                    }
                }
            }
        });

        // Add Book Action
        btnAdd.addActionListener(e -> {
            String isbn = txtIsbn.getText().trim();
            String titleStr = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String copiesStr = txtCopies.getText().trim();

            if (isbn.isEmpty() || titleStr.isEmpty() || author.isEmpty() || copiesStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all input fields", "Input Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int copies = Integer.parseInt(copiesStr);
                Book existing = bookTree.search(isbn);
                if (existing != null) {
                    JOptionPane.showMessageDialog(this, "Book with this ISBN already exists! Use 'Update Book' instead.", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    Book newBook = new Book(isbn, titleStr, author, copies);
                    bookTree.insert(newBook);
                    JOptionPane.showMessageDialog(this, "Book added to AVL Tree successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllData();
                    clearForm(txtIsbn, txtTitle, txtAuthor, txtCopies);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Copies must be a positive integer", "Input Validation", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update Book Action
        btnUpdate.addActionListener(e -> {
            String isbn = txtIsbn.getText().trim();
            String titleStr = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String copiesStr = txtCopies.getText().trim();

            if (isbn.isEmpty() || titleStr.isEmpty() || author.isEmpty() || copiesStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all input fields", "Input Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int copies = Integer.parseInt(copiesStr);
                Book existing = bookTree.search(isbn);
                if (existing != null) {
                    existing.setTitle(titleStr);
                    existing.setAuthor(author);
                    
                    int oldTotal = existing.getTotalCopies();
                    int diff = copies - oldTotal;
                    
                    if (diff > 0) {
                        // Use librarySystem to safely add copies and serve the waiting queue
                        librarySystem.addNewCopies(bookTree, isbn, diff);
                        JOptionPane.showMessageDialog(this, "Book updated successfully! Any students in the waiting list have been served.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        existing.updateCopies(copies);
                        JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    refreshAllData();
                    clearForm(txtIsbn, txtTitle, txtAuthor, txtCopies);
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found in the library. Cannot update.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Copies must be a positive integer", "Input Validation", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear Fields Action
        btnClear.addActionListener(e -> {
            clearForm(txtIsbn, txtTitle, txtAuthor, txtCopies);
        });

        // Delete Action
        btnDelete.addActionListener(e -> {
            String isbn = txtIsbn.getText().trim();
            if (isbn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select or input ISBN to delete", "Input Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Book book = bookTree.search(isbn);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Book not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '" + book.getTitle() + "'?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                bookTree.delete(isbn);
                JOptionPane.showMessageDialog(this, "Book deleted from AVL Tree", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
                clearForm(txtIsbn, txtTitle, txtAuthor, txtCopies);
            }
        });



        return panel;
    }

    // ==========================================
    // 3. Borrowers Management Panel
    // ==========================================
    private JPanel createBorrowersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Row: Title & Search bar
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(BG_DARK);
        topRow.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Students & Borrowers Directory");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_LIGHT);
        topRow.add(title, BorderLayout.WEST);



        panel.add(topRow, BorderLayout.NORTH);

        // Main Split Pane
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_DARK);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                createTitledBorder("New Borrower Registration"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setPreferredSize(new Dimension(300, 400));

        JTextField txtName = createStyledTextField();
        JCheckBox chkGrad = new JCheckBox("Student is Graduating (Priority status)");
        chkGrad.setFont(FONT_BODY);
        chkGrad.setForeground(TEXT_LIGHT);
        chkGrad.setBackground(CARD_DARK);
        chkGrad.setFocusPainted(false);

        formPanel.add(createFormLabel("Full Name:"));
        formPanel.add(txtName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(chkGrad);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnRegister = createStyledButton("Register Borrower", ACCENT_PRIMARY);
        JButton btnRemove = createStyledButton("Remove Borrower", new Color(220, 80, 80));
        
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        btnPanel.setBackground(CARD_DARK);
        btnPanel.add(btnRegister);
        btnPanel.add(btnRemove);
        formPanel.add(btnPanel);
        formPanel.add(Box.createVerticalGlue());

        // Right side table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_DARK);
        tableContainer.setBorder(createTitledBorder("Registered Borrowers"));

        borrowersTableModel = new DefaultTableModel(new String[]{"Borrower Name", "Graduating Status"}, 0);
        borrowersTable = createStyledTable(borrowersTableModel);
        
        borrowersSorter = new TableRowSorter<>(borrowersTableModel);
        borrowersTable.setRowSorter(borrowersSorter);

        tableContainer.add(new JScrollPane(borrowersTable), BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.weightx = 0.3;
        mainContent.add(formPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 15, 0, 0);
        mainContent.add(tableContainer, gbc);

        panel.add(mainContent, BorderLayout.CENTER);

        // Actions
        btnRegister.addActionListener(e -> {
            String name = txtName.getText().trim();
            boolean isGrad = chkGrad.isSelected();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter borrower name", "Input Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean exists = false;
            for (Borrower b : registeredBorrowers) {
                if (b.getName().equalsIgnoreCase(name)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                JOptionPane.showMessageDialog(this, "A borrower with this name is already registered.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            registeredBorrowers.add(new Borrower(name, isGrad));
            IO.saveBorrowers(registeredBorrowers); // Persist borrowers
            JOptionPane.showMessageDialog(this, "Borrower registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtName.setText("");
            chkGrad.setSelected(false);
            refreshAllData();
        });

        btnRemove.addActionListener(e -> {
            int selectedRow = borrowersTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Select a borrower from the table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = borrowersTable.convertRowIndexToModel(selectedRow);
            String name = borrowersTableModel.getValueAt(modelRow, 0).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove '" + name + "'?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                registeredBorrowers.removeIf(b -> b.getName().equalsIgnoreCase(name));
                IO.saveBorrowers(registeredBorrowers); // Persist borrowers
                JOptionPane.showMessageDialog(this, "Borrower removed from the system.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            }
        });

        // Sync table click to form fields
        borrowersTable.getSelectionModel().addListSelectionListener(e -> {
            int row = borrowersTable.getSelectedRow();
            if (row >= 0) {
                int modelRow = borrowersTable.convertRowIndexToModel(row);
                txtName.setText(borrowersTableModel.getValueAt(modelRow, 0).toString());
                chkGrad.setSelected(borrowersTableModel.getValueAt(modelRow, 1).toString().contains("Graduating"));
            }
        });



        return panel;
    }

    // ==========================================
    // 4. Borrow and Return Panel
    // ==========================================
    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Row: Title & Search bar
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(BG_DARK);
        topRow.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Borrowing Transactions & Return Desk");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_LIGHT);
        topRow.add(title, BorderLayout.WEST);

        // Instant Live Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(5, 0));
        searchBar.setBackground(BG_DARK);
        JLabel lblSearch = new JLabel("Search Records: ");
        lblSearch.setFont(FONT_BODY);
        lblSearch.setForeground(TEXT_LIGHT);
        JTextField txtSearch = createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(200, 30));
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        topRow.add(searchBar, BorderLayout.EAST);

        panel.add(topRow, BorderLayout.NORTH);

        // Split layout: Form left, Active records table right
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_DARK);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                createTitledBorder("Issue / Return Book"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setPreferredSize(new Dimension(300, 400));

        cmbBorrower = new JComboBox<>();
        cmbBorrower.setBackground(INPUT_BG);
        cmbBorrower.setForeground(TEXT_LIGHT);
        cmbBorrower.setFont(FONT_BODY);

        cmbBook = new JComboBox<>();
        cmbBook.setBackground(INPUT_BG);
        cmbBook.setForeground(TEXT_LIGHT);
        cmbBook.setFont(FONT_BODY);

        JTextField txtExpectedReturn = createStyledTextField();
        // pre-fill with date
        txtExpectedReturn.setText(LocalDate.now().plusDays(14).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        formPanel.add(createFormLabel("Select Registered Borrower:"));
        formPanel.add(cmbBorrower);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createFormLabel("Select Registered Book:"));
        formPanel.add(cmbBook);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createFormLabel("Expected Return Date (YYYY-MM-DD):"));
        formPanel.add(txtExpectedReturn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Buttons
        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        actionPanel.setBackground(CARD_DARK);
        JButton btnBorrow = createStyledButton("Borrow", ACCENT_PRIMARY);
        JButton btnUpdate = createStyledButton("Update Record", new Color(130, 90, 240));
        JButton btnReturn = createStyledButton("Return Book", ACCENT_SECONDARY);
        actionPanel.add(btnBorrow);
        actionPanel.add(btnUpdate);
        actionPanel.add(btnReturn);
        formPanel.add(actionPanel);

        formPanel.add(Box.createVerticalGlue());

        // Right panel table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_DARK);
        tableContainer.setBorder(createTitledBorder("Active Borrowing Logs & Queue Status"));

        // Top row for table: Search bar
        JPanel topSearchPanel = new JPanel(new BorderLayout(8, 0));
        topSearchPanel.setBackground(CARD_DARK);
        topSearchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel lblSearchRecords = new JLabel("Search Records: ");
        lblSearchRecords.setFont(FONT_HEADER);
        lblSearchRecords.setForeground(TEXT_LIGHT);
        txtSearchRecords = createStyledTextField();
        txtSearchRecords.setPreferredSize(new Dimension(220, 30));
        topSearchPanel.add(lblSearchRecords, BorderLayout.WEST);
        topSearchPanel.add(txtSearchRecords, BorderLayout.CENTER);
        tableContainer.add(topSearchPanel, BorderLayout.NORTH);

        borrowTableModel = new DefaultTableModel(new String[]{
                "ID", "Borrower", "Grad?", "Book ISBN", "Lend Date", "Due Date", "Returned?"
        }, 0);
        borrowTable = createStyledTable(borrowTableModel);
        
        borrowSorter = new TableRowSorter<>(borrowTableModel);
        borrowTable.setRowSorter(borrowSorter);

        tableContainer.add(new JScrollPane(borrowTable), BorderLayout.CENTER);

        // Search key listener
        txtSearchRecords.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshBorrowTable();
            }
        });

        gbc.gridx = 0;
        gbc.weightx = 0.3;
        mainContent.add(formPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 15, 0, 0);
        mainContent.add(tableContainer, gbc);

        panel.add(mainContent, BorderLayout.CENTER);

        // Action listeners
        btnBorrow.addActionListener(e -> {
            Borrower selectedBorrower = (Borrower) cmbBorrower.getSelectedItem();
            BookItem selectedBookItem = (BookItem) cmbBook.getSelectedItem();
            String dateStr = txtExpectedReturn.getText().trim();

            if (selectedBorrower == null) {
                JOptionPane.showMessageDialog(this, "Please register and select a borrower first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selectedBookItem == null) {
                JOptionPane.showMessageDialog(this, "Please register and select a book first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate dueDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                // Intercept warning/status constraints to display visual Alerts/Dialogs
                int activeBorrows = 0;
                for (BorrowRecord r : getBorrowRecordsList()) {
                    if (r.getBorrowerName().equalsIgnoreCase(selectedBorrower.getName()) && !r.isReturned()) {
                        activeBorrows++;
                    }
                }

                if (activeBorrows >= 3) {
                    JOptionPane.showMessageDialog(this, "You have hit your limit of borrowing books! The limit is 3 books.", "Borrowing Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                } else {
                    boolean alreadyBorrowed = false;
                    for (BorrowRecord r : getBorrowRecordsList()) {
                        if (r.getBorrowerName().equalsIgnoreCase(selectedBorrower.getName()) && 
                            r.getBookIsbn().equals(selectedBookItem.getBook().getIsbn()) && 
                            !r.isReturned()) {
                            alreadyBorrowed = true;
                            break;
                        }
                    }

                    if (alreadyBorrowed) {
                        JOptionPane.showMessageDialog(this, "You have already borrowed this book!", "Duplicate Borrow Check", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Check availability
                        if (selectedBookItem.getBook().getAvailableCopies() <= 0) {
                            JOptionPane.showMessageDialog(this, "Book is not available at this time. You are being added to the waiting list.", "Added to Waiting List", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Borrowing completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }

                librarySystem.borrowBook(
                        selectedBorrower.getName(),
                        selectedBorrower.isGraduating(),
                        selectedBookItem.getBook(),
                        LocalDate.now(),
                        dueDate
                );
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            int selectedRow = borrowTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Select a borrow record from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = borrowTable.convertRowIndexToModel(selectedRow);
            int recordId = (int) borrowTableModel.getValueAt(modelRow, 0);

            Borrower selectedBorrower = (Borrower) cmbBorrower.getSelectedItem();
            BookItem selectedBookItem = (BookItem) cmbBook.getSelectedItem();
            String dateStr = txtExpectedReturn.getText().trim();

            if (selectedBorrower == null || selectedBookItem == null || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select borrower, book, and expected return date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate dueDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                BorrowRecord record = null;
                for (BorrowRecord r : getBorrowRecordsList()) {
                    if (r.getRecordId() == recordId) {
                        record = r;
                        break;
                    }
                }

                if (record != null) {
                    record.setBorrowerName(selectedBorrower.getName());
                    record.setGraduating(selectedBorrower.isGraduating());
                    record.setBookIsbn(selectedBookItem.getBook().getIsbn());
                    record.setExpectedReturnDate(dueDate);
                    
                    JOptionPane.showMessageDialog(this, "Borrow Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReturn.addActionListener(e -> {
            int selectedRow = borrowTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an active transaction row from the right table to return.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = borrowTable.convertRowIndexToModel(selectedRow);
            String borrowerName = borrowTableModel.getValueAt(modelRow, 1).toString();
            String isbn = borrowTableModel.getValueAt(modelRow, 3).toString();
            String status = borrowTableModel.getValueAt(modelRow, 6).toString();

            if (status.equals("Returned")) {
                JOptionPane.showMessageDialog(this, "This book has already been returned.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Book book = bookTree.search(isbn);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Book not found in inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Intercept return status to display visual success Alert/Dialog
            boolean hasWaiting = !book.getWaitingList().isEmpty();
            if (hasWaiting) {
                JOptionPane.showMessageDialog(this, "Book returned successfully! It has been automatically issued to the next student in the waiting list.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            librarySystem.returnBook(borrowerName, book);
            refreshAllData();
        });



        // Sync table click to fields
        borrowTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = borrowTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = borrowTable.convertRowIndexToModel(selectedRow);
                int recordId = (int) borrowTableModel.getValueAt(modelRow, 0);
                
                BorrowRecord record = null;
                for (BorrowRecord r : getBorrowRecordsList()) {
                    if (r.getRecordId() == recordId) {
                        record = r;
                        break;
                    }
                }
                
                if (record != null) {
                    for (int i = 0; i < cmbBorrower.getItemCount(); i++) {
                        Borrower b = cmbBorrower.getItemAt(i);
                        if (b.getName().equalsIgnoreCase(record.getBorrowerName())) {
                            cmbBorrower.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < cmbBook.getItemCount(); i++) {
                        BookItem bi = cmbBook.getItemAt(i);
                        if (bi.getBook().getIsbn().equals(record.getBookIsbn())) {
                            cmbBook.setSelectedIndex(i);
                            break;
                        }
                    }
                    txtExpectedReturn.setText(record.getExpectedReturnDate().toString());
                }
            }
        });

        // Filtering
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    borrowSorter.setRowFilter(null);
                } else {
                    borrowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        return panel;
    }



    // ==========================================
    // Core Swing Custom GUI Styling Helpers
    // ==========================================
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1, true), title
        );
        border.setTitleFont(FONT_HEADER);
        border.setTitleColor(ACCENT_SECONDARY);
        return border;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(new Color(200, 200, 210));
        lbl.setBorder(new EmptyBorder(5, 0, 3, 0));
        return lbl;
    }

    private JButton createStyledButton(String text, Color background) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_HEADER);
        btn.setForeground(Color.WHITE);
        btn.setBackground(background);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(background.darker(), 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(background.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(background);
            }
        });

        return btn;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setBackground(CARD_DARK);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(26);
        table.setFont(FONT_BODY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(ACCENT_PRIMARY);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(45, 45, 54));
        header.setForeground(TEXT_LIGHT);
        header.setFont(FONT_HEADER);
        header.setBorder(new LineBorder(BORDER_COLOR, 1));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        return table;
    }

    private void clearForm(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
        waitingListTableModel.setRowCount(0);
    }

    // ==========================================
    // Reflection & Data Syncing Controllers
    // ==========================================
    private void refreshAllData() {
        refreshBooksTable();
        refreshBorrowersTable();
        refreshBorrowTable();
        refreshDashboardStats();
        refreshDropdowns();
    }

    private void refreshBooksTable() {
        booksTableModel.setRowCount(0);
        List<Book> books = new ArrayList<>();
        collectBooks(bookTree.getRoot(), books);
        for (Book b : books) {
            booksTableModel.addRow(new Object[]{
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getAvailableCopies() + " / " + b.getTotalCopies(),
                    b.getBorrowCount()
            });
        }
        // Save books to persistent file
        IO.saveBooks(books);
    }

    private void collectBooks(AVLNode node, List<Book> books) {
        if (node == null) return;
        collectBooks(node.left, books);
        books.add(node.book);
        collectBooks(node.right, books);
    }

    private void refreshBorrowersTable() {
        borrowersTableModel.setRowCount(0);
        for (Borrower b : registeredBorrowers) {
            borrowersTableModel.addRow(new Object[]{
                    b.getName(),
                    b.isGraduating() ? "Graduating Student (Priority)" : "Normal Student"
            });
        }
    }

    private void refreshDropdowns() {
        cmbBorrower.removeAllItems();
        cmbBook.removeAllItems();

        for (Borrower b : registeredBorrowers) {
            cmbBorrower.addItem(b);
        }

        List<Book> books = new ArrayList<>();
        collectBooks(bookTree.getRoot(), books);
        for (Book b : books) {
            cmbBook.addItem(new BookItem(b));
        }
    }

    @SuppressWarnings("unchecked")
    private List<BorrowRecord> getBorrowRecordsList() {
        List<BorrowRecord> allRecords = new ArrayList<>();
        try {
            Field field = LibrarySystem.class.getDeclaredField("borrowRecords");
            field.setAccessible(true);
            java.util.Map<String, List<BorrowRecord>> map = (java.util.Map<String, List<BorrowRecord>>) field.get(librarySystem);
            if (map != null) {
                for (List<BorrowRecord> list : map.values()) {
                    allRecords.addAll(list);
                }
            }
        } catch (Exception ex) {
            System.err.println("Reflection Error fetching records: " + ex.getMessage());
        }
        return allRecords;
    }

    private void refreshBorrowTable() {
        borrowTableModel.setRowCount(0);
        String query = txtSearchRecords != null ? txtSearchRecords.getText().trim() : "";
        List<BorrowRecord> records;
        if (query.isEmpty()) {
            records = getBorrowRecordsList();
        } else {
            records = librarySystem.searchRecords(query);
        }
        for (BorrowRecord r : records) {
            borrowTableModel.addRow(new Object[]{
                    r.getRecordId(),
                    r.getBorrowerName(),
                    r.isGraduating() ? "Yes" : "No",
                    r.getBookIsbn(),
                    r.getBorrowDate(),
                    r.getExpectedReturnDate(),
                    r.isReturned() ? "Returned" : "Active"
            });
        }
    }

    private void refreshDashboardStats() {
        lblTotalBooksVal.setText(String.valueOf(bookTree.getTotalBookTitles()));
        lblTotalCopiesVal.setText(String.valueOf(bookTree.getTotalBookCopies()));
        lblTotalAvailableVal.setText(String.valueOf(bookTree.getTotalAvailableCopies()));
        lblActiveBorrowsVal.setText(String.valueOf(librarySystem.getActiveBorrowsCount()));
        lblWaitingRequestsVal.setText(String.valueOf(librarySystem.getTotalWaitingRequestsCount(bookTree)));

        topBooksModel.setRowCount(0);
        for (Book b : bookTree.getTopBorrowedBooks(5)) {
            topBooksModel.addRow(new Object[]{b.getTitle(), b.getIsbn(), b.getBorrowCount()});
        }

        topAuthorsModel.setRowCount(0);
        for (java.util.Map.Entry<String, Integer> entry : librarySystem.getTopAuthors(bookTree, 5)) {
            topAuthorsModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void refreshWaitingList(String isbn) {
        waitingListTableModel.setRowCount(0);
        Book book = bookTree.search(isbn);
        if (book != null) {
            PriorityQueue pq = book.getWaitingList();
            if (pq != null && !pq.isEmpty()) {
                try {
                    Field heapField = PriorityQueue.class.getDeclaredField("heap");
                    heapField.setAccessible(true);
                    WaitingRequest[] heap = (WaitingRequest[]) heapField.get(pq);

                    int position = 1;
                    for (int i = 0; i < pq.size(); i++) {
                        WaitingRequest req = heap[i];
                        if (req != null) {
                            waitingListTableModel.addRow(new Object[]{
                                    position++,
                                    req.getStudentName(),
                                    req.isGraduating() ? "Yes (High Priority)" : "No"
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error accessing waiting list via reflection: " + ex.getMessage());
                }
            }
        }
    }



    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LibraryUI frame = new LibraryUI();
            frame.setVisible(true);
        });
    }
}
