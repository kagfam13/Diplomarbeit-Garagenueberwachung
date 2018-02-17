/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlkaindorf.hoemam11.blaulichtsteuerung.GateControlling.easyModbus;

/**
 * 
 * @author Kager Fabian
 * converts String with an hex code to bin
 */
public class HexToBin {
    private final String data;
    private final int length;
    private Boolean[] coils;

    public HexToBin(String data,int length) {
        this.data = data; // Coil 0 at the end of the hex String
        this.length = length;
        calc();
    }
    
    private void calc() {
        String daten = new StringBuffer(data).reverse().toString(); 
        String coilsss = "";
        while(!daten.isEmpty())
        {
            System.out.println(daten);
            int intVal = Integer.parseInt(""+daten.charAt(0), 16);
            String bin = Integer.toBinaryString(intVal);
            while(bin.length()<4)
                bin = "0" + bin;
            daten = daten.substring(1);
            coilsss += new StringBuffer(bin).reverse().toString();
            System.out.println(coilsss);
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
}
