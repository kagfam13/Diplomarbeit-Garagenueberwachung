/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.easyModbus;

/**
 *
 * @author Fabian
 */
public class StringCoilsResp {
    private final String coils;

    public StringCoilsResp(String coils) {
        this.coils = coils;
    }
    
    public boolean getCoil(int id) throws Exception
    {
        if(id<0 || id>= coils.length())
            throw new Exception("id zu groß");
        if (coils.charAt(id) == '1')
            return true;
        return false;
    }
    
    public int getCoilCount()
    {
        return coils.length();
    }
}
