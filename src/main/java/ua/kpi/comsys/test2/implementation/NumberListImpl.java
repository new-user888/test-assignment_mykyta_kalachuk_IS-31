/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of INumberList interface.
 * Has to be implemented by each student independently.
 * 
 * Linear doubly linked list, hexadecimal number system
 * Additional operation: multiplication
 * Secondary system: binary
 *
 * @author Mykyta Kalachuk, variant 9
 */
public class NumberListImpl implements NumberList {
    
    private Node head;
    private Node tail;
    private int size;
    private int radix; // 16 for hex, 2 for binary
    
    // Node for linear doubly linked list
    private static class Node {
        byte data;
        Node next;
        Node prev;
        
        Node(byte data) {
            this.data = data;
        }
    }

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.radix = 16; // hex by default
    }
    
    /**
     * Constructor with specified radix
     */
    private NumberListImpl(int radix) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.radix = radix;
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try {
            if (file != null && file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath())).trim();
                initFromDecimalString(content);
            }
        } catch (IOException e) {
            // file not found - leave empty
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }
    
    private void initFromDecimalString(String decimal) {
        if (decimal == null || decimal.isEmpty()) {
            return;
        }
        
        // validate input: check that string contains only digits
        if (!decimal.matches("\\d+")) {
            return; // invalid input - leave list empty
        }
        
        String hex = decimalToHex(decimal);
        
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            byte digit = (byte) Character.digit(c, 16);
            add(digit);
        }
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(toDecimalString());
        } catch (IOException e) {
            throw new RuntimeException("Error writing file", e);
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 9;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        String decimal = toDecimalString();
        String binary = decimalToBinary(decimal);
        
        NumberListImpl result = new NumberListImpl(2); // binary radix
        for (int i = 0; i < binary.length(); i++) {
            byte digit = (byte) (binary.charAt(i) - '0');
            // add digits directly with correct radix
            Node newNode = new Node(digit);
            if (result.head == null) {
                result.head = result.tail = newNode;
            } else {
                result.tail.next = newNode;
                newNode.prev = result.tail;
                result.tail = newNode;
            }
            result.size++;
        }
        return result;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        // multiplication operation
        String thisDecimal = toDecimalString();
        String argDecimal = ((NumberListImpl) arg).toDecimalString();
        
        String result = multiplyDecimal(thisDecimal, argDecimal);
        return new NumberListImpl(result);
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) {
            return "0";
        }
        
        String str = toString();
        
        // convert to decimal depending on number system
        if (radix == 2) {
            return binaryToDecimal(str);
        } else {
            return hexToDecimal(str);
        }
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "0";
        }
        
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            if (radix == 2) {
                // for binary just append the digit
                sb.append(current.data);
            } else {
                // for hex format as hex
                sb.append(Integer.toHexString(current.data).toUpperCase());
            }
            current = current.next;
        }
        
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberList)) return false;
        
        NumberList other = (NumberList) o;
        if (this.size() != other.size()) return false;
        
        Iterator<Byte> it1 = this.iterator();
        Iterator<Byte> it2 = other.iterator();
        
        while (it1.hasNext() && it2.hasNext()) {
            if (!it1.next().equals(it2.next())) {
                return false;
            }
        }
        
        return true;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) return false;
        
        Node current = head;
        while (current != null) {
            if (current.data == (Byte) o) {
                return true;
            }
            current = current.next;
        }
        
        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            
            @Override
            public boolean hasNext() {
                return current != null;
            }
            
            @Override
            public Byte next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                byte data = current.data;
                current = current.next;
                return data;
            }
        };
    }


    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Byte b : this) {
            result[i++] = b;
        }
        return result;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }


    @Override
    public boolean add(Byte e) {
        if (e == null || e < 0) {
            throw new IllegalArgumentException("Element must be non-negative");
        }
        
        int maxDigit = radix - 1;
        if (e > maxDigit) {
            throw new IllegalArgumentException("Element must be 0-" + maxDigit + " for radix " + radix);
        }
        
        Node newNode = new Node(e);
        
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        
        size++;
        return true;
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte) || isEmpty()) {
            return false;
        }
        
        byte value = (Byte) o;
        Node current = head;
        
        while (current != null) {
            if (current.data == value) {
                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }
                
                if (current.next != null) {
                    current.next.prev = current.prev;
                } else {
                    tail = current.prev;
                }
                
                size--;
                return true;
            }
            current = current.next;
        }
        
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        
        int i = index;
        for (Byte e : c) {
            add(i++, e);
        }
        return !c.isEmpty();
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            while (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node current = head;
        
        while (current != null) {
            Node next = current.next;
            if (!c.contains(current.data)) {
                remove((Byte) current.data);
                modified = true;
            }
            current = next;
        }
        
        return modified;
    }


    @Override
    public void clear() {
        head = tail = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }


    @Override
    public Byte set(int index, Byte element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (element == null || element < 0) {
            throw new IllegalArgumentException("Element must be non-negative");
        }
        
        int maxDigit = radix - 1;
        if (element > maxDigit) {
            throw new IllegalArgumentException("Element must be 0-" + maxDigit + " for radix " + radix);
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        byte oldValue = current.data;
        current.data = element;
        return oldValue;
    }


    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (element == null || element < 0) {
            throw new IllegalArgumentException("Element must be non-negative");
        }
        
        int maxDigit = radix - 1;
        if (element > maxDigit) {
            throw new IllegalArgumentException("Element must be 0-" + maxDigit + " for radix " + radix);
        }
        
        if (index == size) {
            add(element);
            return;
        }
        
        Node newNode = new Node(element);
        
        if (index == 0) {
            newNode.next = head;
            if (head != null) {
                head.prev = newNode;
            }
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
        } else {
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            
            newNode.next = current;
            newNode.prev = current.prev;
            if (current.prev != null) {
                current.prev.next = newNode;
            }
            current.prev = newNode;
        }
        
        size++;
    }


    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        
        byte removed = current.data;
        
        if (current.prev != null) {
            current.prev.next = current.next;
        } else {
            head = current.next;
        }
        
        if (current.next != null) {
            current.next.prev = current.prev;
        } else {
            tail = current.prev;
        }
        
        size--;
        return removed;
    }


    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte) || isEmpty()) {
            return -1;
        }
        
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.data == (Byte) o) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte) || isEmpty()) {
            return -1;
        }
        
        int lastIndex = -1;
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.data == (Byte) o) {
                lastIndex = i;
            }
            current = current.next;
        }
        return lastIndex;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        return listIterator(0);
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        
        return new ListIterator<Byte>() {
            private int currentIndex = index;
            
            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }
            
            @Override
            public Byte next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return get(currentIndex++);
            }
            
            @Override
            public boolean hasPrevious() {
                return currentIndex > 0;
            }
            
            @Override
            public Byte previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return get(--currentIndex);
            }
            
            @Override
            public int nextIndex() {
                return currentIndex;
            }
            
            @Override
            public int previousIndex() {
                return currentIndex - 1;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void set(Byte e) {
                NumberListImpl.this.set(currentIndex - 1, e);
            }
            
            @Override
            public void add(Byte e) {
                NumberListImpl.this.add(currentIndex++, e);
            }
        };
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        
        List<Byte> result = new ArrayList<>();
        for (int i = fromIndex; i < toIndex; i++) {
            result.add(get(i));
        }
        return result;
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }
        
        if (index1 == index2) {
            return true;
        }
        
        Byte temp = get(index1);
        set(index1, get(index2));
        set(index2, temp);
        return true;
    }


    @Override
    public void sortAscending() {
        if (size <= 1) return;
        
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) > get(j + 1)) {
                    swap(j, j + 1);
                }
            }
        }
    }


    @Override
    public void sortDescending() {
        if (size <= 1) return;
        
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) < get(j + 1)) {
                    swap(j, j + 1);
                }
            }
        }
    }


    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        
        byte first = head.data;
        Node current = head;
        
        while (current.next != null) {
            current.data = current.next.data;
            current = current.next;
        }
        
        current.data = first;
    }


    @Override
    public void shiftRight() {
        if (size <= 1) return;
        
        byte last = tail.data;
        Node current = tail;
        
        while (current.prev != null) {
            current.data = current.prev.data;
            current = current.prev;
        }
        
        current.data = last;
    }
    
    // Helper methods for number conversion
    
    private String decimalToHex(String decimal) {
        if (decimal.equals("0")) return "0";
        
        StringBuilder result = new StringBuilder();
        List<Integer> digits = new ArrayList<>();
        
        for (char c : decimal.toCharArray()) {
            digits.add(c - '0');
        }
        
        while (!isZero(digits)) {
            int remainder = divideBy16(digits);
            result.insert(0, Integer.toHexString(remainder).toUpperCase());
        }
        
        return result.length() == 0 ? "0" : result.toString();
    }
    
    private String hexToDecimal(String hex) {
        if (hex.equals("0")) return "0";
        
        String result = "0";
        String power = "1";
        
        for (int i = hex.length() - 1; i >= 0; i--) {
            int digit = Character.digit(hex.charAt(i), 16);
            String term = multiplyDecimal(power, String.valueOf(digit));
            result = addDecimal(result, term);
            power = multiplyDecimal(power, "16");
        }
        
        return result;
    }
    
    private String decimalToBinary(String decimal) {
        if (decimal.equals("0")) return "0";
        
        StringBuilder result = new StringBuilder();
        List<Integer> digits = new ArrayList<>();
        
        for (char c : decimal.toCharArray()) {
            digits.add(c - '0');
        }
        
        while (!isZero(digits)) {
            int remainder = divideBy2(digits);
            result.insert(0, remainder);
        }
        
        return result.length() == 0 ? "0" : result.toString();
    }
    
    private String binaryToDecimal(String binary) {
        if (binary.equals("0")) return "0";
        
        String result = "0";
        String power = "1";
        
        for (int i = binary.length() - 1; i >= 0; i--) {
            int digit = binary.charAt(i) - '0';
            if (digit == 1) {
                result = addDecimal(result, power);
            }
            power = multiplyDecimal(power, "2");
        }
        
        return result;
    }
    
    private boolean isZero(List<Integer> digits) {
        for (int d : digits) {
            if (d != 0) return false;
        }
        return true;
    }
    
    private int divideBy16(List<Integer> digits) {
        int remainder = 0;
        for (int i = 0; i < digits.size(); i++) {
            int current = remainder * 10 + digits.get(i);
            digits.set(i, current / 16);
            remainder = current % 16;
        }
        
        while (digits.size() > 1 && digits.get(0) == 0) {
            digits.remove(0);
        }
        
        return remainder;
    }
    
    private int divideBy2(List<Integer> digits) {
        int remainder = 0;
        for (int i = 0; i < digits.size(); i++) {
            int current = remainder * 10 + digits.get(i);
            digits.set(i, current / 2);
            remainder = current % 2;
        }
        
        while (digits.size() > 1 && digits.get(0) == 0) {
            digits.remove(0);
        }
        
        return remainder;
    }
    
    private String addDecimal(String a, String b) {
        StringBuilder result = new StringBuilder();
        int carry = 0;
        int i = a.length() - 1;
        int j = b.length() - 1;
        
        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;
            if (i >= 0) sum += a.charAt(i--) - '0';
            if (j >= 0) sum += b.charAt(j--) - '0';
            
            result.insert(0, sum % 10);
            carry = sum / 10;
        }
        
        return result.toString();
    }
    
    private String multiplyDecimal(String a, String b) {
        if (a.equals("0") || b.equals("0")) return "0";
        
        int[] result = new int[a.length() + b.length()];
        
        for (int i = a.length() - 1; i >= 0; i--) {
            for (int j = b.length() - 1; j >= 0; j--) {
                int mul = (a.charAt(i) - '0') * (b.charAt(j) - '0');
                int p1 = i + j;
                int p2 = i + j + 1;
                int sum = mul + result[p2];
                
                result[p2] = sum % 10;
                result[p1] += sum / 10;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (int num : result) {
            if (!(sb.length() == 0 && num == 0)) {
                sb.append(num);
            }
        }
        
        return sb.length() == 0 ? "0" : sb.toString();
    }
}
