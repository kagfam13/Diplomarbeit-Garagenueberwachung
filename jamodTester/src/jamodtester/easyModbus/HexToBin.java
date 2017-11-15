/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.easyModbus;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Fabian
 * converts String with an hex code to bin
 */
public class HexToBin {
    private final String data;
    private final int length;
    private Boolean[] coils;

    public HexToBin(String data,int length) {
        this.data = data;
        this.length = length;
        calc();
    }

    private void calc() {
        String daten = new StringBuffer(data).reverse().toString();
        String coilsss = "";
        while(!daten.isEmpty())
        {
            int intVal = Integer.parseInt(""+data.charAt(0), 16);
            String bin = Integer.toBinaryString(intVal);
            while(bin.length()<4)
                bin = "0" + bin;
            daten = daten.substring(1);
            coilsss += new StringBuffer(bin).reverse().toString();
        }
        coils = new Boolean[length];
        int i;
        for(i=0;i<coilsss.length();i++)
        {
            if(i<length)
                coils[i] = coilsss.charAt(i) == '1';
        }
    }

    

    public Boolean[] getCoils() {
        return coils;
    }
    
    
    
    public static void main(String[] args) {
    }
}
