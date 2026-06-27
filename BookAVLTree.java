/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.alg_2;

/**
 *
 * @author Loq
 */

public class BookAVLTree {

    private AVLNode root;

    public AVLNode getRoot() {
        return root;
    }

    private int height(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private int getBalance(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public void insert(Book book) {
        if (book != null && book.getIsbn() != null) {
            root = insert(root, book);
        }
    }

    private AVLNode insert(AVLNode node, Book book) {
        if (node == null) {
            return new AVLNode(book);
        }

        int cmp = book.getIsbn().compareTo(node.book.getIsbn());

        if (cmp < 0) {
            node.left = insert(node.left, book);
        } else if (cmp > 0) {
            node.right = insert(node.right, book);
        } else {
            return node;
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        int balance = getBalance(node);

        if (balance > 1 && book.getIsbn().compareTo(node.left.book.getIsbn()) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && book.getIsbn().compareTo(node.right.book.getIsbn()) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && book.getIsbn().compareTo(node.left.book.getIsbn()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && book.getIsbn().compareTo(node.right.book.getIsbn()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public Book search(String isbn) {
        if (isbn == null) {
            return null;
        }

        AVLNode result = search(root, isbn);

        if (result == null) {
            return null;
        }

        return result.book;
    }

    private AVLNode search(AVLNode node, String isbn) {
        if (node == null) {
            return null;
        }

        int cmp = isbn.compareTo(node.book.getIsbn());

        if (cmp < 0) {
            return search(node.left, isbn);
        } else if (cmp > 0) {
            return search(node.right, isbn);
        }

        return node;
    }

    public void delete(String isbn) {
        if (isbn != null) {
            root = delete(root, isbn);
        }
    }

    private AVLNode delete(AVLNode node, String isbn) {
        if (node == null) {
            return null;
        }

        int cmp = isbn.compareTo(node.book.getIsbn());

        if (cmp < 0) {
            node.left = delete(node.left, isbn);
        } else if (cmp > 0) {
            node.right = delete(node.right, isbn);
        } else {

            if (node.left == null || node.right == null) {

                AVLNode temp;

                if (node.left != null) {
                    temp = node.left;
                } else {
                    temp = node.right;
                }

                if (temp == null) {
                    node = null;
                } else {
                    node = temp;
                }

            } else {

                AVLNode temp = getMinValueNode(node.right);

                node.book = temp.book;

                node.right = delete(node.right, temp.book.getIsbn());
            }
        }

        if (node == null) {
            return null;
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode getMinValueNode(AVLNode node) {
        AVLNode current = node;

        while (current.left != null) {
            current = current.left;
        }

        return current;
    }
}
