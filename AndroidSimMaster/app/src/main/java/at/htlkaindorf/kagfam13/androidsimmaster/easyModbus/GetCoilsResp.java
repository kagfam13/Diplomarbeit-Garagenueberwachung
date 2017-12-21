/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlkaindorf.kagfam13.androidsimmaster.easyModbus;

import java.util.Arrays;

/**
 *
 * @author Fabian
 */
public class GetCoilsResp {
    private final Boolean[] b;

    public GetCoilsResp(Boolean[] b) {
        this.b = b;
    }
    
    public boolean getCoil(int index) throws Exception
    {
        if(index>=b.length || index<0)
            throw new Exception("Wrong index");
        return b[index];
    }

    @Override
    public String toString() {
        return Arrays.toString(b);
    }
    
    
}
