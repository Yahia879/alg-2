/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.alg_2;

/**
 *
 * @author Loq
 */

public class PriorityQueue {
    private WaitingRequest[] heap;
    private int size;
    private int capacity;

    public PriorityQueue() {
        this.capacity = 10;
        this.size = 0;
        this.heap = new WaitingRequest[capacity];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(WaitingRequest element) {
        if (size >= capacity) {
            resize();
        }
        heap[size] = element;
        siftUp(size);
        size++;
    }

    public WaitingRequest peek() {
        if (isEmpty()) {
            return null;
        }
        return heap[0];
    }

    public WaitingRequest poll() {
        if (isEmpty()) {
            return null;
        }
        WaitingRequest root = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return root;
    }

    private boolean hasHigherPriority(WaitingRequest a, WaitingRequest b) {

        if (a.isGraduating() && !b.isGraduating()) {
            return true;
        }

        if (!a.isGraduating() && b.isGraduating()) {
            return false;
        }

        return a.getTimestamp() < b.getTimestamp();
    }

    private void siftUp(int index) {
        WaitingRequest element = heap[index];
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            WaitingRequest parent = heap[parentIndex];
            
            if (!hasHigherPriority(element, parent)) {
                break;
            }
            
            heap[index] = parent;
            index = parentIndex;
        }
        heap[index] = element;
    }

    private void siftDown(int index) {
        WaitingRequest element = heap[index];
        int half = size / 2;
        while (index < half) {
            int leftChildIndex = 2 * index + 1;
            int rightChildIndex = leftChildIndex + 1;
            int bestChildIndex = leftChildIndex;

            if (rightChildIndex < size && hasHigherPriority(heap[rightChildIndex], heap[leftChildIndex])) {
                bestChildIndex = rightChildIndex;
            }
            if (!hasHigherPriority(heap[bestChildIndex], element)) {
                break;
            }

            heap[index] = heap[bestChildIndex];
            index = bestChildIndex;
        }
        heap[index] = element;
    }

    private void resize() {
        capacity = capacity * 2;
        WaitingRequest[] newHeap = new WaitingRequest[capacity];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }
}

