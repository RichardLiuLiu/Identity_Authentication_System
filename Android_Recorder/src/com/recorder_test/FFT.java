package com.recorder_test;

import java.util.Arrays;

public class FFT {
	
	private short[] mDataBuf;
	private double[] mResultBuf;
	private int mLength,mExp;
	
	public FFT() {}
	
	public FFT(short[] data, int size) {
		mLength=1;
		mExp=0;
		while (mLength<size) { mLength*=2; mExp++; }
		mDataBuf=Arrays.copyOf(data, mLength);	
		mResultBuf=new double[mLength];
	}
	
	public void getData(short[] data, int size) {
		mLength=1;
		mExp=0;
		while (mLength<size) { mLength*=2; mExp++; }
		mDataBuf=Arrays.copyOf(data, mLength);	
		mResultBuf=new double[mLength];
	}
	
	private cplx Cadd(cplx A, cplx B, cplx ans) {
		ans.re=A.re+B.re;
		ans.im=A.im+B.im;
		return ans;
	}
	
	private cplx Cmin(cplx A, cplx B, cplx ans) {
		ans.re=A.re-B.re;
		ans.im=A.im-B.im;
		return ans;
	}
	
	private cplx Cmpy(cplx A, cplx B, cplx ans) {
		ans.re=A.re * B.re - A.im * B.im;
		ans.im=A.re * B.im + A.im * B.re;
		return ans;
	}
	
	private cplx Wexp(int N, int k, cplx ans) {
		ans.re=Math.cos(2*Math.PI*k/N);
		ans.im=-1*Math.sin(2*Math.PI*k/N);
		return ans;
	}
	
	private int flipBit(int origin, int bitnum) {
		int ans;
		//int result;
		//ans=(long)origin&0x0ffffffff;
		ans=((origin<<1) & 0xaaaaaaaa) | ((origin>>>1) & 0x55555555);
		ans=((ans<<2) & 0xcccccccc) | ((ans>>>2) & 0x33333333);
		ans=((ans<<4) & 0xf0f0f0f0) | ((ans>>>4) & 0x0f0f0f0f);
		ans=((ans<<8) & 0xff00ff00) | ((ans>>>8) & 0x00ff00ff);
		ans=((ans<<16) & 0xffff0000) | ((ans>>>16) & 0x0000ffff);
		ans=ans>>>(32-bitnum);
		//result=(int)ans;
		//return result;
		return ans;
	}
	
	public double[] transform() {
		int i,j;
		int UnitLength=mLength;
		cplx Buf1[],Buf2[],Buf3,Buf4;
		cplx Data[],Result[],Switch[];
		Buf1=new cplx[mLength];
		Buf2=new cplx[mLength];
		for (i=0;i<mLength;i++) {
			Buf1[i]=new cplx();
			Buf2[i]=new cplx();
		}
		Buf3=new cplx(); Buf4=new cplx();
		for (i=0; i<mLength; i++) Buf1[i].re=mDataBuf[i];
		
		Data=Buf1;Result=Buf2;
		while (UnitLength>1) {
			for (i=0;i<mLength/UnitLength;i++) {
				for (j=0;j<UnitLength/2;j++) {
					int pos1=i*UnitLength+j, pos2=i*UnitLength+UnitLength/2+j;
					Cadd(Data[pos1], Data[pos2], Result[pos1]);
					Cmin(Data[pos1], Data[pos2], Buf3);
					Wexp(mLength, mLength/UnitLength*j, Buf4);
					Cmpy(Buf3, Buf4, Result[pos2]);
					
				}
			}
			Switch=Result; Result=Data; Data=Switch;
			UnitLength/=2;
		}
			
		if (mExp%2==0) 
			for (i=0;i<mLength;i++) 
				mResultBuf[flipBit(i,mExp)]=Math.sqrt(Math.pow(Data[i].re, 2)+Math.pow(Data[i].im, 2));
		else 
			for (i=0;i<mLength;i++) 
				mResultBuf[flipBit(i,mExp)]=Math.sqrt(Math.pow(Result[i].re, 2)+Math.pow(Result[i].im, 2));
		
		return mResultBuf;
	}

}

class cplx {
	public double re, im;
	cplx() {
		re=0;
		im=0;
	}
	cplx(double data1, double data2) {
		re=data1;
		im=data2;
	}
}