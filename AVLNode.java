/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.alg_2;

/**
 *
 * @author Loq
 */

public class AVLNode {
    public Book book;
    public AVLNode left;
    public AVLNode right;
    public int height = 1;

    public AVLNode(Book book) {
        this.book = book;
    }
}
