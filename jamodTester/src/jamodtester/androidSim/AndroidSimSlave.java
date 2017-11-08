/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.androidSim;

import java.awt.*;
import javax.swing.*;
import net.wimpi.modbus.*;
import net.wimpi.modbus.net.*;
import net.wimpi.modbus.procimg.*;

/**
 *
 * @author User
 */
public class AndroidSimSlave extends javax.swing.JFrame
{

  /**
   * Creates new form AndroidSimSlave
   */
  
  ModbusTCPListener listener = null;
  SimpleProcessImage spi = null;
  int port = Modbus.DEFAULT_PORT;
  
  //Schreiben
  
  
  private void writeCoils()
  {
    spi.setDigitalOut(10,new SimpleDigitalOut(tbTLF.isSelected()));
    spi.setDigitalOut(11,new SimpleDigitalOut(tbKRF.isSelected()));
    spi.setDigitalOut(12,new SimpleDigitalOut(tbMTF.isSelected()));
    spi.setDigitalOut(13,new SimpleDigitalOut(tbOEF.isSelected()));
    spi.setDigitalOut(14,new SimpleDigitalOut(tbVF.isSelected()));
    spi.setDigitalOut(15,new SimpleDigitalOut(tbTSO1.isSelected()));
    spi.setDigitalOut(16,new SimpleDigitalOut(tbTSU1.isSelected()));
    spi.setDigitalOut(17,new SimpleDigitalOut(tbTSO2.isSelected()));
    spi.setDigitalOut(18,new SimpleDigitalOut(tbTSU2.isSelected()));
    spi.setDigitalOut(19,new SimpleDigitalOut(tbTSO3.isSelected()));
    spi.setDigitalOut(20,new SimpleDigitalOut(tbTSU3.isSelected()));
    spi.setDigitalOut(21,new SimpleDigitalOut(tbTSO4.isSelected()));
    spi.setDigitalOut(22,new SimpleDigitalOut(tbTSU4.isSelected()));
    spi.setDigitalOut(23,new SimpleDigitalOut(tbTSO5.isSelected()));
    spi.setDigitalOut(24,new SimpleDigitalOut(tbTSU5.isSelected()));
    
  }
  
  // Lesen 
  private class motorWorker extends SwingWorker<Object, Object>
    {
        JPanel garagenplatz;
        boolean status;
        JToggleButton TorSensorOben,TorSensorUnten;
        //Constructor
        public motorWorker(JPanel garagenplatz,boolean status, JToggleButton TorSensorOben,JToggleButton TorSensorUnten) {
            this.garagenplatz = garagenplatz;
            this.status = status;
            this.TorSensorOben = TorSensorOben;
            this.TorSensorUnten = TorSensorUnten;
        }
        
        @Override
        protected Object doInBackground() throws Exception {
            garagenplatz.setBackground(Color.green);
            Thread.sleep(5000);
            garagenplatz.setBackground(Color.red);
            if(status == true)
            {
                TorSensorOben.setSelected(true);
                TorSensorUnten.setSelected(false);
            }
            else
            {
                TorSensorOben.setSelected(false);
                TorSensorUnten.setSelected(true);
            }
            spi.setDigitalOut(3, new SimpleDigitalOut(TorSensorOben.isSelected()));
            spi.setDigitalOut(4, new SimpleDigitalOut(TorSensorUnten.isSelected()));
            return 0;
        }
        
    }
  
  private class backgroundWorker extends SwingWorker<Object, Object>
    {
      
        
    
        @Override
        protected Object doInBackground() throws Exception {
            while(true)
            {
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(0).isSet())
                    torAufFahren(0);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(1).isSet())
                    torZuFahren(1);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(2).isSet())
                    torAufFahren(2);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(3).isSet())
                    torZuFahren(3);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(4).isSet())
                    torAufFahren(4);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(5).isSet())
                    torZuFahren(5);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(6).isSet())
                    torAufFahren(6);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(7).isSet())
                    torZuFahren(7);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(8).isSet())
                    torAufFahren(8);
                if(ModbusCoupler.getReference().getProcessImage().getDigitalOut(9).isSet())
                    torZuFahren(9);
            }
        }

        private void torAufFahren(int coil) {
            System.out.println("Tor fährt auf");
            spi.setDigitalOut(coil, new SimpleDigitalOut(false));
            switch(coil)
            {
              case 0:
                new AndroidSimSlave.motorWorker(pta1, true, tbTSO1, tbTSU1).execute();
                break;
              case 2:
                new AndroidSimSlave.motorWorker(pta2, true, tbTSO2, tbTSU2).execute();
                break;
              case 4:
                new AndroidSimSlave.motorWorker(pta3, true, tbTSO3, tbTSU3).execute();
                break;
              case 6:
                new AndroidSimSlave.motorWorker(pta4, true, tbTSO4, tbTSU4).execute();
                break;
              case 8:
                new AndroidSimSlave.motorWorker(pta5, true, tbTSO5, tbTSU5).execute();
                break;
              default:
                System.out.println("ERROR");
                break;
            }
        }

        private void torZuFahren(int coil) {
            System.out.println("Tor fährt zu");
            spi.setDigitalOut(coil+1, new SimpleDigitalOut(false));
            //new ArduinoSimSlave.motorWorker(jPanel4,false).execute();
            switch(coil)
            {
              case 1:
                new AndroidSimSlave.motorWorker(ptz1, true, tbTSO1, tbTSU1).execute();
                break;
              case 3:
                new AndroidSimSlave.motorWorker(pta2, true, tbTSO2, tbTSU2).execute();
                break;
              case 5:
                new AndroidSimSlave.motorWorker(pta3, true, tbTSO3, tbTSU3).execute();
                break;
              case 7:
                new AndroidSimSlave.motorWorker(pta4, true, tbTSO4, tbTSU4).execute();
                break;
              case 9:
                new AndroidSimSlave.motorWorker(pta5, true, tbTSO5, tbTSU5).execute();
                break;
              default:
                System.out.println("ERROR 2");
                break;
            }
        }
        
    }
  
  public AndroidSimSlave()
  {
    initComponents();
    jLabel2.setText("" + sliderMin.getValue());
    jLabel4.setText("" + sliderSec.getValue());
    pta1.setBackground(Color.red);
    ptz1.setBackground(Color.red);
    pta2.setBackground(Color.red);
    ptz2.setBackground(Color.red);
    pta3.setBackground(Color.red);
    ptz3.setBackground(Color.red);
    pta4.setBackground(Color.red);
    ptz4.setBackground(Color.red);
    pta5.setBackground(Color.red);
    ptz5.setBackground(Color.red);
    
    try 
    {
            if(listener != null)
                listener.stop();
            System.out.println("jModbus Modbus Slave (Server)");

            // 1. prepare a process image
            spi = new SimpleProcessImage();
            //Inputs
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            spi.addDigitalIn(new SimpleDigitalIn());
            
            //Outputs
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut(tbTLF.isSelected()));
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            spi.addDigitalOut(new SimpleDigitalOut());
            
            ModbusCoupler.getReference().setUnitID(15);
            ModbusCoupler.getReference().setMaster(false);
            ModbusCoupler.getReference().setProcessImage(spi);
            
            if (Modbus.debug)
                    System.out.println("Listening...");
            listener = new ModbusTCPListener(3);
            listener.setPort(port);

            System.out.println("Listening to "+listener+" on port "+port);
            
            listener.start();
            
            new backgroundWorker().execute();
            
            
            
            
    }
    catch(Exception ex)
    {
      
    }
    
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  

    
    
    
    
  
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    jPanel3 = new javax.swing.JPanel();
    bExit = new javax.swing.JButton();
    jPanel2 = new javax.swing.JPanel();
    jPanel5 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    sliderMin = new javax.swing.JSlider();
    jLabel2 = new javax.swing.JLabel();
    jPanel4 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    sliderSec = new javax.swing.JSlider();
    jLabel4 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    jPanel6 = new javax.swing.JPanel();
    jLabel15 = new javax.swing.JLabel();
    tbTLF = new javax.swing.JToggleButton();
    tbTSO1 = new javax.swing.JToggleButton();
    tbTSU1 = new javax.swing.JToggleButton();
    pta1 = new javax.swing.JPanel();
    ta1 = new javax.swing.JLabel();
    ptz1 = new javax.swing.JPanel();
    tz1 = new javax.swing.JLabel();
    jPanel7 = new javax.swing.JPanel();
    jLabel16 = new javax.swing.JLabel();
    tbKRF = new javax.swing.JToggleButton();
    tbTSO2 = new javax.swing.JToggleButton();
    tbTSU2 = new javax.swing.JToggleButton();
    pta2 = new javax.swing.JPanel();
    ta2 = new javax.swing.JLabel();
    ptz2 = new javax.swing.JPanel();
    tz2 = new javax.swing.JLabel();
    jPanel8 = new javax.swing.JPanel();
    jLabel17 = new javax.swing.JLabel();
    tbMTF = new javax.swing.JToggleButton();
    tbTSO3 = new javax.swing.JToggleButton();
    tbTSU3 = new javax.swing.JToggleButton();
    pta3 = new javax.swing.JPanel();
    ta3 = new javax.swing.JLabel();
    ptz3 = new javax.swing.JPanel();
    tz3 = new javax.swing.JLabel();
    jPanel9 = new javax.swing.JPanel();
    jLabel18 = new javax.swing.JLabel();
    tbOEF = new javax.swing.JToggleButton();
    tbTSO4 = new javax.swing.JToggleButton();
    tbTSU4 = new javax.swing.JToggleButton();
    pta4 = new javax.swing.JPanel();
    ta4 = new javax.swing.JLabel();
    ptz4 = new javax.swing.JPanel();
    tz4 = new javax.swing.JLabel();
    jPanel10 = new javax.swing.JPanel();
    jLabel19 = new javax.swing.JLabel();
    tbVF = new javax.swing.JToggleButton();
    tbTSO5 = new javax.swing.JToggleButton();
    tbTSU5 = new javax.swing.JToggleButton();
    pta5 = new javax.swing.JPanel();
    ta5 = new javax.swing.JLabel();
    ptz5 = new javax.swing.JPanel();
    tz5 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("ArduinoSimSlave");
    getContentPane().setLayout(new java.awt.BorderLayout(8, 8));

    bExit.setText("exit");
    bExit.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        bExitActionPerformed(evt);
      }
    });
    jPanel3.add(bExit);

    getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

    jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
    jPanel2.setLayout(new java.awt.GridLayout(2, 1, 8, 8));

    jPanel5.setLayout(new java.awt.BorderLayout(8, 0));

    jLabel1.setText("min");
    jPanel5.add(jLabel1, java.awt.BorderLayout.WEST);

    sliderMin.setMaximum(255);
    sliderMin.addChangeListener(new javax.swing.event.ChangeListener()
    {
      public void stateChanged(javax.swing.event.ChangeEvent evt)
      {
        minStateChanged(evt);
      }
    });
    jPanel5.add(sliderMin, java.awt.BorderLayout.CENTER);

    jLabel2.setText("jLabel2");
    jPanel5.add(jLabel2, java.awt.BorderLayout.EAST);

    jPanel2.add(jPanel5);

    jPanel4.setLayout(new java.awt.BorderLayout(8, 0));

    jLabel3.setText("sec");
    jPanel4.add(jLabel3, java.awt.BorderLayout.WEST);

    sliderSec.setMaximum(59);
    sliderSec.addChangeListener(new javax.swing.event.ChangeListener()
    {
      public void stateChanged(javax.swing.event.ChangeEvent evt)
      {
        sliderSecStateChanged(evt);
      }
    });
    jPanel4.add(sliderSec, java.awt.BorderLayout.CENTER);

    jLabel4.setText("jLabel4");
    jPanel4.add(jLabel4, java.awt.BorderLayout.EAST);

    jPanel2.add(jPanel4);

    getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

    jPanel1.setLayout(new java.awt.GridLayout(5, 1));

    jLabel15.setText("1");
    jPanel6.add(jLabel15);

    tbTLF.setText("TLF");
    tbTLF.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTLFActionPerformed(evt);
      }
    });
    jPanel6.add(tbTLF);

    tbTSO1.setText("TorSensor oben");
    tbTSO1.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSO1ActionPerformed(evt);
      }
    });
    jPanel6.add(tbTSO1);

    tbTSU1.setText("TorSensor unten");
    tbTSU1.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSU1ActionPerformed(evt);
      }
    });
    jPanel6.add(tbTSU1);

    pta1.setLayout(new java.awt.BorderLayout());

    ta1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    ta1.setText("Tor auf");
    pta1.add(ta1, java.awt.BorderLayout.CENTER);

    jPanel6.add(pta1);

    ptz1.setLayout(new java.awt.BorderLayout());

    tz1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    tz1.setText("Tor zu");
    ptz1.add(tz1, java.awt.BorderLayout.CENTER);

    jPanel6.add(ptz1);

    jPanel1.add(jPanel6);

    jLabel16.setText("2");
    jPanel7.add(jLabel16);

    tbKRF.setText("KRF");
    tbKRF.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbKRFActionPerformed(evt);
      }
    });
    jPanel7.add(tbKRF);

    tbTSO2.setText("TorSensor oben");
    tbTSO2.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSO2ActionPerformed(evt);
      }
    });
    jPanel7.add(tbTSO2);

    tbTSU2.setText("TorSensor unten");
    tbTSU2.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSU2ActionPerformed(evt);
      }
    });
    jPanel7.add(tbTSU2);

    pta2.setLayout(new java.awt.BorderLayout());

    ta2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    ta2.setText("Tor auf");
    pta2.add(ta2, java.awt.BorderLayout.CENTER);

    jPanel7.add(pta2);

    ptz2.setLayout(new java.awt.BorderLayout());

    tz2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    tz2.setText("Tor zu");
    ptz2.add(tz2, java.awt.BorderLayout.CENTER);

    jPanel7.add(ptz2);

    jPanel1.add(jPanel7);

    jLabel17.setText("3");
    jPanel8.add(jLabel17);

    tbMTF.setText("MTF");
    tbMTF.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbMTFActionPerformed(evt);
      }
    });
    jPanel8.add(tbMTF);

    tbTSO3.setText("TorSensor oben");
    tbTSO3.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSO3ActionPerformed(evt);
      }
    });
    jPanel8.add(tbTSO3);

    tbTSU3.setText("TorSensor unten");
    tbTSU3.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSU3ActionPerformed(evt);
      }
    });
    jPanel8.add(tbTSU3);

    pta3.setLayout(new java.awt.BorderLayout());

    ta3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    ta3.setText("Tor auf");
    pta3.add(ta3, java.awt.BorderLayout.CENTER);

    jPanel8.add(pta3);

    ptz3.setLayout(new java.awt.BorderLayout());

    tz3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    tz3.setText("Tor zu");
    ptz3.add(tz3, java.awt.BorderLayout.CENTER);

    jPanel8.add(ptz3);

    jPanel1.add(jPanel8);

    jLabel18.setText("4");
    jPanel9.add(jLabel18);

    tbOEF.setText("ÖF");
    tbOEF.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbOEFActionPerformed(evt);
      }
    });
    jPanel9.add(tbOEF);

    tbTSO4.setText("TorSensor oben");
    tbTSO4.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSO4ActionPerformed(evt);
      }
    });
    jPanel9.add(tbTSO4);

    tbTSU4.setText("TorSensor unten");
    tbTSU4.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSU4ActionPerformed(evt);
      }
    });
    jPanel9.add(tbTSU4);

    pta4.setLayout(new java.awt.BorderLayout());

    ta4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    ta4.setText("Tor auf");
    pta4.add(ta4, java.awt.BorderLayout.CENTER);

    jPanel9.add(pta4);

    ptz4.setLayout(new java.awt.BorderLayout());

    tz4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    tz4.setText("Tor zu");
    ptz4.add(tz4, java.awt.BorderLayout.CENTER);

    jPanel9.add(ptz4);

    jPanel1.add(jPanel9);

    jLabel19.setText("5");
    jPanel10.add(jLabel19);

    tbVF.setText("VF");
    tbVF.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbVFActionPerformed(evt);
      }
    });
    jPanel10.add(tbVF);

    tbTSO5.setText("TorSensor oben");
    tbTSO5.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSO5ActionPerformed(evt);
      }
    });
    jPanel10.add(tbTSO5);

    tbTSU5.setText("TorSensor unten");
    tbTSU5.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        tbTSU5ActionPerformed(evt);
      }
    });
    jPanel10.add(tbTSU5);

    pta5.setLayout(new java.awt.BorderLayout());

    ta5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    ta5.setText("Tor auf");
    pta5.add(ta5, java.awt.BorderLayout.CENTER);

    jPanel10.add(pta5);

    ptz5.setLayout(new java.awt.BorderLayout());

    tz5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    tz5.setText("Tor zu");
    ptz5.add(tz5, java.awt.BorderLayout.CENTER);

    jPanel10.add(ptz5);

    jPanel1.add(jPanel10);

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void minStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_minStateChanged
  {//GEN-HEADEREND:event_minStateChanged
    // TODO add your handling code here:
    jLabel2.setText("" + sliderMin.getValue());
    
  }//GEN-LAST:event_minStateChanged

  private void sliderSecStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sliderSecStateChanged
  {//GEN-HEADEREND:event_sliderSecStateChanged
    // TODO add your handling code here:
    jLabel4.setText("" + sliderSec.getValue());
  }//GEN-LAST:event_sliderSecStateChanged

  private void tbTLFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTLFActionPerformed
  {//GEN-HEADEREND:event_tbTLFActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTLFActionPerformed

  private void tbTSO1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSO1ActionPerformed
  {//GEN-HEADEREND:event_tbTSO1ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSO1ActionPerformed

  private void tbTSU1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSU1ActionPerformed
  {//GEN-HEADEREND:event_tbTSU1ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSU1ActionPerformed

  private void tbKRFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbKRFActionPerformed
  {//GEN-HEADEREND:event_tbKRFActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbKRFActionPerformed

  private void tbTSO2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSO2ActionPerformed
  {//GEN-HEADEREND:event_tbTSO2ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSO2ActionPerformed

  private void tbTSU2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSU2ActionPerformed
  {//GEN-HEADEREND:event_tbTSU2ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSU2ActionPerformed

  private void tbMTFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbMTFActionPerformed
  {//GEN-HEADEREND:event_tbMTFActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbMTFActionPerformed

  private void tbTSO3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSO3ActionPerformed
  {//GEN-HEADEREND:event_tbTSO3ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSO3ActionPerformed

  private void tbTSU3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSU3ActionPerformed
  {//GEN-HEADEREND:event_tbTSU3ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSU3ActionPerformed

  private void tbOEFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbOEFActionPerformed
  {//GEN-HEADEREND:event_tbOEFActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbOEFActionPerformed

  private void tbTSO4ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSO4ActionPerformed
  {//GEN-HEADEREND:event_tbTSO4ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSO4ActionPerformed

  private void tbTSU4ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSU4ActionPerformed
  {//GEN-HEADEREND:event_tbTSU4ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSU4ActionPerformed

  private void tbVFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbVFActionPerformed
  {//GEN-HEADEREND:event_tbVFActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbVFActionPerformed

  private void tbTSO5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSO5ActionPerformed
  {//GEN-HEADEREND:event_tbTSO5ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSO5ActionPerformed

  private void tbTSU5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbTSU5ActionPerformed
  {//GEN-HEADEREND:event_tbTSU5ActionPerformed
    // TODO add your handling code here:
    writeCoils();
  }//GEN-LAST:event_tbTSU5ActionPerformed

  private void bExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bExitActionPerformed
  {//GEN-HEADEREND:event_bExitActionPerformed
    // TODO add your handling code here:
    dispose();
    System.exit(0);
    listener.stop();
  }//GEN-LAST:event_bExitActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try
    {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
      {
        if ("Nimbus".equals(info.getName()))
        {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }
    catch (ClassNotFoundException ex)
    {
      java.util.logging.Logger.getLogger(AndroidSimSlave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (InstantiationException ex)
    {
      java.util.logging.Logger.getLogger(AndroidSimSlave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (IllegalAccessException ex)
    {
      java.util.logging.Logger.getLogger(AndroidSimSlave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (javax.swing.UnsupportedLookAndFeelException ex)
    {
      java.util.logging.Logger.getLogger(AndroidSimSlave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        new AndroidSimSlave().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton bExit;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel17;
  private javax.swing.JLabel jLabel18;
  private javax.swing.JLabel jLabel19;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel10;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JPanel jPanel7;
  private javax.swing.JPanel jPanel8;
  private javax.swing.JPanel jPanel9;
  private javax.swing.JPanel pta1;
  private javax.swing.JPanel pta2;
  private javax.swing.JPanel pta3;
  private javax.swing.JPanel pta4;
  private javax.swing.JPanel pta5;
  private javax.swing.JPanel ptz1;
  private javax.swing.JPanel ptz2;
  private javax.swing.JPanel ptz3;
  private javax.swing.JPanel ptz4;
  private javax.swing.JPanel ptz5;
  private javax.swing.JSlider sliderMin;
  private javax.swing.JSlider sliderSec;
  private javax.swing.JLabel ta1;
  private javax.swing.JLabel ta2;
  private javax.swing.JLabel ta3;
  private javax.swing.JLabel ta4;
  private javax.swing.JLabel ta5;
  private javax.swing.JToggleButton tbKRF;
  private javax.swing.JToggleButton tbMTF;
  private javax.swing.JToggleButton tbOEF;
  private javax.swing.JToggleButton tbTLF;
  private javax.swing.JToggleButton tbTSO1;
  private javax.swing.JToggleButton tbTSO2;
  private javax.swing.JToggleButton tbTSO3;
  private javax.swing.JToggleButton tbTSO4;
  private javax.swing.JToggleButton tbTSO5;
  private javax.swing.JToggleButton tbTSU1;
  private javax.swing.JToggleButton tbTSU2;
  private javax.swing.JToggleButton tbTSU3;
  private javax.swing.JToggleButton tbTSU4;
  private javax.swing.JToggleButton tbTSU5;
  private javax.swing.JToggleButton tbVF;
  private javax.swing.JLabel tz1;
  private javax.swing.JLabel tz2;
  private javax.swing.JLabel tz3;
  private javax.swing.JLabel tz4;
  private javax.swing.JLabel tz5;
  // End of variables declaration//GEN-END:variables
}
