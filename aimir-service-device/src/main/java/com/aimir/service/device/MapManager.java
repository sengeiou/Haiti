package com.aimir.service.device;

import java.util.List;

import com.aimir.model.device.MCU;

import de.micromata.opengis.kml.v_2_2_0.Kml;

public interface MapManager {

    public Kml getMCU(Integer supplierID);
    public Kml getMCU(Integer supplierID, String sysID);
    public Kml getMCU(Integer supplierID, Integer locationID);
    public Kml getMCU(Integer supplierID, String sysID,Integer locationID);
    public Kml getMCUWithCodi(Integer supplierID, String sysID);
    public Kml getMCUWithCodi(Integer supplierID, String sysID,Integer locationID);
    public Kml getMCUWithRelativeDevice(Integer supplierID, String sysID);
    public Kml getMCUWithRelativeModem(Integer supplierID, String sysID);
    public Kml getMeter(Integer supplierID);
    public Kml getMeter(Integer supplierID, String mdsID);
    public Kml getMeter(Integer supplierID, Integer locationID);
    public Kml getMeter(Integer supplierID, String mdsID,Integer locationID);
    public Kml getMeterWithRelativeDevice(Integer supplierID, String mdsID);
    public Kml getModem(Integer supplierID);
    public Kml getModem(Integer supplierID, String deviceSerial);
    public Kml getModem(Integer supplierID, Integer locationID);
    public Kml getModem(Integer supplierID, String deviceSerial,Integer locationID);
    public Kml getModemWithRelativeDevice(Integer supplierID, String deviceSerial);
    public Boolean setMCUPoint(String sysID, Double gpioX, Double gpioY, Double gpioZ);
    public Boolean setMCUAddress(String sysID, String address);
    public Boolean setModemPoint(String deviceSerial, Double gpioX, Double gpioY, Double gpioZ);
    public Boolean setModemAddress(String deviceSerial, String address);
    public Boolean setMeterPoint(String mdsID, Double gpioX, Double gpioY, Double gpioZ);
    public Boolean setMeterAddress(String mdsID, String address);
    public List<MCU> getMCUbyCodi(String codiID);
    public Kml getMCUCodiWithRelativeModem(Integer supplierID, String sysID);

    /**
     * method name : getMCUMapData<b/>
     * method Desc : Concentrator Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param supplierID
     * @param sysID
     * @return
     */
    public Kml getMCUMapData(Integer supplierID, String sysID);

    /**
     * method name : getMeterMapData<b/>
     * method Desc : Meter Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param supplierID
     * @param mdsID
     * @return
     */
    public Kml getMeterMapData(Integer supplierID, String mdsID);

    /**
     * method name : getModemMapData<b/>
     * method Desc : Modem Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param supplierID
     * @param deviceSerial
     * @return
     */
    public Kml getModemMapData(Integer supplierID, String deviceSerial);
    
    /**
     * method name : getMCUList<b/>
     * method Desc : SP-572
     * @param supplierID
     * @param sysID
     * @param locationID
     * @return
     */
    public  List<Object> getMCUList(Integer supplierID, String sysID,Integer locationID);
    
    /**
     * method name : getMeterList<b/>
     * method Desc : SP-572, SP-1050(UPDATE)
     * @param supplierID
     * @param mdsID
     * @param locationID
     * @oaram msa
     * @return
     */
    public List<Object> getMeterList(Integer supplierID, String mdsID,Integer locationID, String msa);
    
    /**
     * method name : getMeterList<b/>
     * method Desc : SP-572, SP-1050(UPDATE)
     * @param supplierID
     * @param deviceSerial
     * @param locationID
     * @param msa
     * @return
     */
    public List<Object> getModemList(Integer supplierID, String deviceSerial,Integer locationID,String msa);
    
    /**
     * method name : getMCUWithCodiList<b/>
     * method Desc : SP-572
     * @param supplierID
     * @param sysID
     * @param locationID
     * @return
     */
    public List<Object>  getMCUWithCodiList(Integer supplierID, String sysID,Integer locationID) ;
    
    
    /**
     * @param sysId
     * method Desc : SP-1038
     * @return
     */
    public String getMcuMap(String sysId);
}