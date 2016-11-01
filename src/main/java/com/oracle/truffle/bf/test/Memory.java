/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.truffle.bf.test;

public final class Memory {

    int[] cells;
    int index;


    public Memory() {
        index = 0;
        cells = new int[100];
    }

}
