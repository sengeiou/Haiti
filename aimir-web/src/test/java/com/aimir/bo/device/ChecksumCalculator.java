package com.aimir.bo.device;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Get Checksum from input file 
 * @author sejin han
 *
 */
public class ChecksumCalculator {
	
	private static MessageDigest md = null;
    
    public static int SCOUR_MD5_BYTE_LIMIT = (20000 * 1024);
	
	   /**
     * Method: getFileMD5Sum Purpose: get the MD5 sum of a file. Scour exchange only
     * counts the first SCOUR_MD5_BYTE_LIMIT bytes of a file for caclulating checksums
     * (probably for efficiency or better comaprison counts against unfinished downloads).
     *
     * @param f
     *            the file to read
     * @return the MD5 sum string
     * @throws IOException
     *             on IO error
     */
    public String getFileMD5Sum(File f) throws Exception 
    {
        String sum = null;
        FileInputStream in;
       
		in = new FileInputStream(f.getAbsolutePath());
        byte[] b = new byte[1024];
        int num = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();        	
    	
		while ((num = in.read(b)) != -1)
		{
		    out.write(b, 0, num);

		    if (out.size() > SCOUR_MD5_BYTE_LIMIT)
		    {
		        //log.debug("Over size: "+out.size()+" file size: "+f.length());
		    	System.out.print("Over size: "+out.size()+" file size: "+f.length());
		        sum = md5Sum(out.toByteArray(), 10000);
		        break;
		    }
		}
        if (sum == null)
            sum = md5Sum(out.toByteArray(), SCOUR_MD5_BYTE_LIMIT);

        in.close();
        out.close();			

        return sum;
    }	
    
    public String md5Sum(byte[] input, int limit)
    {
        try
        {
            if (md == null)
                md = MessageDigest.getInstance("MD5");

            md.reset();
            byte[] digest;

            if (limit == -1)
            {
                digest = md.digest(input);
            }
            else
            {
                md.update(input, 0, limit > input.length ? input.length : limit);
                digest = md.digest();
            }

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < digest.length; i++)
            {
                hexString.append(hexDigit(digest[i]));
            }

            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e.getMessage());
        }
    }
    
    
    /**
     * Method: hexDigit Purpose: convert a hex digit to a String, used by md5Sum.
     *
     * @param x
     *            the digit to translate
     * @return the hex code for the digit
     */
    private String hexDigit(byte x)
    {
        StringBuffer sb = new StringBuffer();
        char c;

        // First nibble
        c = (char) ((x >> 4) & 0xf);
        if (c > 9)
        {
            c = (char) ((c - 10) + 'a');
        }
        else
        {
            c = (char) (c + '0');
        }

        sb.append(c);

        // Second nibble
        c = (char) (x & 0xf);
        if (c > 9)
        {
            c = (char) ((c - 10) + 'a');
        }
        else
        {
            c = (char) (c + '0');
        }

        sb.append(c);
        return sb.toString();
    }
    
    
    public static void main(String[] args){
    	//set the filepath
    	String fwFilePath = "C://Users/nurisj/Downloads/000B12000000030D.tar.gz";
    	//String fwFilePath = "/home/aimir/aimiramm/fw/000B12.tar.gz";
    	File inFile = new File(fwFilePath);
    	
    	try{
    		ChecksumCalculator cc = new ChecksumCalculator();
    		String md5 = cc.getFileMD5Sum(inFile);
    		
    		System.out.println("md5:" + md5);
    		
    	}catch(Exception e){
    		System.out.println("error: " + e);
    	}
    }

}
