package com.aimir.util;

import java.util.Vector;

/**
 * Token
 *
 * @author 2.x버전에서 가져옴
 */
public class Token{
	private int count = 0;
	@SuppressWarnings("unchecked")
	private Vector<String> element = new Vector();
	public Token(String str, String del) 
    {
		String target  = str;
		String subtarget;
		String token;
		int index = target.indexOf(del);
		while(index >= 0) {
			subtarget = target.substring(index+1,target.length());
			token = target.substring(0,index);		
			element.add(count,token);
			target = subtarget;
			index = target.indexOf(del);
			count += 1;
		}
		element.add(count,target);
		count += 1;
	}

	public int getCountElt() {
		return count;
	}

	public String getElementAt(int i) {
		if(i < count)
			return (String)element.elementAt(i);
		return null;
	}
	public Vector<String> getAllElement() {
		return element;
	}

}
