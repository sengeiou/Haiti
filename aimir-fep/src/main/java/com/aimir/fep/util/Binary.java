package com.aimir.fep.util;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Formatting Java class into binary representation of C structure.
 * Scanning Java class from binary representation of C structure.
 */
public class Binary {

	static Log log = LogFactory.getLog(Binary.class.getName());

	/**
	 * Default alignment is 4;
	 */
	private int align=4;

	public Binary (){
	}

	/**
	 * If you want to use alignment other than 4, use this constructor.
	 */
	public Binary (int align){
		this.align=align;
	}
	
	/**
	 * JAVA OBJECT => C STRUCTURE in byte array
	 */
	public byte[] pack(Object obj) {

		Class<? extends Object> cl = obj.getClass();
		Field[] fl=cl.getDeclaredFields();
		Field f;
		int len,pos,pad;
		byte[] ba=new byte[8];
		byte[] ba_null={0,0,0,0};

		ByteArrayOutputStream bao=new ByteArrayOutputStream ();
		len=0;

		for(int i=0;i<fl.length; i++){
			f=fl[i];
			Object val=null;
			int ival;

			try{
				val=f.get(obj);
			}catch (IllegalAccessException e) { }

			pad=(align - (len%align))%align;
			// pad=len%align;

			if (val instanceof Integer) {
				pos=len+pad;
				
				bao.write(ba_null, 0, pad);

				ival=((Integer) val).intValue();
	    		ba[0] = (byte) (ival >> 24);
	    		ba[1] = (byte) (ival >> 16);
	    		ba[2] = (byte) (ival >> 8);
	    		ba[3] = (byte) ival;

				bao.write(ba, 0, 4);

				len=pos+4;
			} else if(val instanceof Float) {
				pos=len+pad;

				bao.write(ba_null, 0, pad);

				ival = Float.floatToIntBits(((Float)val).floatValue());

				ba[0] = (byte) (ival >> 24);
				ba[1] = (byte) (ival >> 16);
				ba[2] = (byte) (ival >> 8);
				ba[3] = (byte) (ival >> 0);

				bao.write(ba, 0, 4);

				len=pos+4;
			} else if(val instanceof Double) {
				pos=len+pad;

				bao.write(ba_null, 0, pad);

				long lvalue = 
					Double.doubleToLongBits(((Double) val).doubleValue());

				int cursor=0;
				for (int ix = 7; ix >= 0; ix--) {
					ba[cursor++] = (byte) (lvalue >> ix*8);
				}

				bao.write(ba, 0, 8);

				len=pos+8;
			} else if(val instanceof Byte) {
				pos=len;

				//bao.write(((Byte) val).byteValue(), 0, 1);
				bao.write(((Byte) val).byteValue());

				len=pos+1;
			} else if(val instanceof byte[]) {
				pos=len;
				byte[] bx=(byte[])val;
				bao.write(bx, 0, bx.length);
				len=pos+bx.length;
			} else {
				// error
			}
			if(pad>0){
			}
		}

		pad=(align - (len%align))%align;
		bao.write(ba_null, 0, pad);

		return bao.toByteArray();
	}
	
	/** Pack array
	 */
	public byte[] packA(Object objA) {
		Class<? extends Object> cl = objA.getClass();
		//Class<?> ct = null;
		int sz;
		if (cl.isArray()) {
			//ct = cl.getComponentType();
			sz = Array.getLength(objA);
		} else {
			return pack(objA);
		}

		ByteArrayOutputStream bao=new ByteArrayOutputStream ();
		for(int i=0; i<sz; i++) {
			Object obj = Array.get(objA, i);
			byte[] ba = pack(obj);
			bao.write(ba, 0, ba.length);
		}
		return bao.toByteArray();
	}

	/**
	 * C STRUCTURE in byte array => JAVA OBJECT
	 */
	public Object unpack(String className, byte[] src) {

		log.debug("UNPACK: "+ className);

		Class<?> cl=null;
		try{
			cl=Class.forName(className);
		} catch (ClassNotFoundException e) { 
			// System.err.println(e.toString());
		}

		Object obj=null;
		try{
			obj=cl.newInstance();
		} catch (InstantiationException e) {
			// System.err.println(e.toString());
		} catch (IllegalAccessException e) {
			// System.err.println(e.toString());
		}

		_unpack(cl,obj, src);

		return obj;
	}

	public Object unpack(byte[] src) {

		Class<?> cl=this.getClass();

		Object obj=this;

		_unpack(cl,obj, src);

		return obj;
	}

	private void _unpack(Class<?> cl, Object obj, byte[] src) {
		Field[] fl=cl.getDeclaredFields();
		Field f;
		int len, pad, pos;

		len=0;
		for (int i=0; i<fl.length; i++) {
			f=fl[i];
			Object val=null;
			int ival;
			long lval;

			try{
				val=f.get(obj);
			}catch (IllegalAccessException e) { 
			}

			pad=(align - (len%align))%align;

			if (val instanceof Integer) {

				pos=len+pad;

				ival = 
					((src[pos+3] & 0xff) << 0) +
					((src[pos+2] & 0xff) << 8) +
					((src[pos+1] & 0xff) << 16) +
					((src[pos+0] & 0xff) << 24);

				try {
					f.setInt(obj,ival);	
				} catch (IllegalAccessException e) { 
				}
				len=pos+4;
			} else if(val instanceof Float) {
				pos=len+pad;

				ival = 
					((src[pos+3] & 0xff) << 0) +
					((src[pos+2] & 0xff) << 8) +
					((src[pos+1] & 0xff) << 16) +
					((src[pos+0] & 0xff) << 24);
				
				try {
					f.setFloat(obj, Float.intBitsToFloat(ival));
				} catch (IllegalAccessException e) { }

				len=pos+4;
			} else if(val instanceof Double) {
				pos=len+pad;

				lval = 
					(((long)src[pos+7] & 0xff) << 0) +
					(((long)src[pos+6] & 0xff) << 8) +
					(((long)src[pos+5] & 0xff) << 16) +
					(((long)src[pos+4] & 0xff) << 24) +
					(((long)src[pos+3] & 0xff) << 32) +
					(((long)src[pos+2] & 0xff) << 40) +
					(((long)src[pos+1] & 0xff) << 48) +
					(((long)src[pos+0] & 0xff) << 56);

				try {
		       		f.setDouble(obj, Double.longBitsToDouble(lval));
				} catch (IllegalAccessException e) { }

				len=pos+8;
			} else if(val instanceof Byte) {
				pos=len;

				try {
		       		f.setByte(obj, src[pos]);
				} catch (IllegalAccessException e) { }

				len=pos+1;
			} else if(val instanceof byte[]) {
				pos=len;

				byte[] bx=(byte[])val;
				for (int ix=0; ix<bx.length; ix++){
					bx[ix]=src[pos+ix];
				}
				len=pos+bx.length;
			} else {
				// error
			}
			if(pad>0){
			}
		}
	}

	public int sizeOf(Class<?> cl) throws Exception {

		Object obj;

		try {
			obj = (Object) cl.newInstance();
		} catch (Exception e) {
			throw e;
		}

		Field[] fl=cl.getDeclaredFields();
		Field f;

		int len,pos,pad;

		len=0;

		for(int i=0;i<fl.length; i++) {
			f=fl[i];

			Object val=null;

			try{
				val=f.get(obj);
			}catch (IllegalAccessException e) { }

			pad=(align - (len%align))%align;

			if (val instanceof Integer) {
				pos=len+pad;
				
				len=pos+4;
			} else if(val instanceof Float) {
				pos=len+pad;

				len=pos+4;
			} else if(val instanceof Double) {
				pos=len+pad;

				len=pos+8;
			} else if(val instanceof Byte) {
				pos=len;

				len=pos+1;
			} else if(val instanceof byte[]) {
				pos=len;
				byte[] bx=(byte[])val;
				len=pos+bx.length;
			} else {
				// error
			}
			if(pad>0){
			}
		}

		pad=(align - (len%align))%align;

		return len+pad;
	}
	
	/**
	 * @param clA
	 * @param src 
	 * @return  component class
	 */
	public Object[] unpackA(Class<TypeX> clA, byte[] src) throws Exception {

		Class<?> cl = clA.getComponentType();

		int sz = sizeOf(cl);

		int num = src.length/sz;
		Object res[] = new Object[num];
		byte[] ba = new byte[sz];
		
		try {
			for(int i=0; i<num; i++) {
				System.arraycopy(src, i*sz, ba, 0, sz);
				Object obj = cl.newInstance();
				_unpack(cl, obj, ba);
				res[i]=(Object)obj;
			}
		} catch (Exception e) {
			throw e;
		}

		return res;
	}
	
	/**
	 */
	static public String string(byte[] ba) {
		int i;
		for(i=0; i<ba.length-1; i++) {
			if(ba[i]==0){
				break;
			}
		}
		return new String(ba, 0, i);
		/*
		for(i=ba.length-1; i>=0; i--)
			if(ba[i]!=0)
				break;
		return new String(ba, 0, i+1);
		*/
	}

	/** main for test
	 */
	public static void main(String args[]){
		Binary b=new Binary();
		
		// Packing
		TypeX t = new TypeX();
		byte[] ba = b.pack(t);

		if(args.length>0){
			java.io.FileOutputStream fos=null;
			try{
				fos= new java.io.FileOutputStream (args[0]);
			}catch (Throwable e) {}

			try{
				fos.write(ba,0,ba.length);
			}catch (Throwable e) {}

			try{
				fos.close();
			}catch (Throwable e) {}
		}

		// Unpacking
		TypeX x=(TypeX) b.unpack(TypeX.class.getName(),  ba);
		log.debug("int i: " + x.i);
        log.debug("int j: " + x.j);
        log.debug("float f: " + x.f);
        log.debug("double d: " + x.d);

		// Unpacking Array
        log.debug("unapckA");
		Object[] oa;
		try {
			oa = b.unpackA(TypeX.class,  ba);
		} catch (Exception e) { 
            log.error(e);
			e.printStackTrace();
			System.exit(1);
			return;
		}

		for (int i=0; i<oa.length; i++) {
			x = (TypeX)oa[i];

            log.debug("  int i: " + x.i);
            log.debug("  int j: " + x.j);
            log.debug("  float f: " + x.f);
            log.debug("  double d: " + x.d);
		}
	}
}

// Sample class for test
class TypeX{
	int i,j;
	byte b;
	byte bl[] =new byte[2];
	float f;
	double d;

	public TypeX(int x) {
		this.i=x;
	}

	public TypeX() {
		i=1;
		j=2;
		b=0;
		bl[0]=1;
		bl[1]=2;
		f=1;
		d=1.1;
	}
}
