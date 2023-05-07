package com.example.demo;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

class ExtractCoeffecients {
    private static double convert_to_double(String str){
        if(str.isEmpty()||str.equals("+"))
        {
            return 1;
        }
        else if(str.equals("-")){
            return -1;
        }
        else
            return Double.parseDouble(str);
    }
    private static int[] toint(ArrayList<Character> arr)
    {
        int temp[]=new int[arr.size()];
        for (int i = 0; i < temp.length; i++) {
            if(arr.get(i)=='s') {temp[i]=1;continue;}
            temp[i]= arr.get(i)-'0';
        }
        return temp;
    }
    private static double[] finalcoeff(double[] arr,int[] order)
    {
        double[] temp=new double[order[0]+1];
        int i=0,j=order[0];
        while (i < order.length)
        {
            temp[j-order[i]]=arr[i];
            i++;
        }
        return temp;
    }
    public static double[] extract_coeff(String eqn)
    {
        ArrayList<String> coefficients=new ArrayList<>();
        ArrayList<Character> exponents=new ArrayList<>();
        int interval=0;
        for (int i = 0; i < eqn.length(); i++) {
            if(eqn.charAt(i)=='+'||(eqn.charAt(i)=='-'&&i!=0))
            {
                coefficients.add(eqn.substring(interval,i));interval=i;
                exponents.add(eqn.charAt(interval-1));
            }
        }
        coefficients.add(eqn.substring(interval));
        if(coefficients.get(coefficients.size()-1).contains("s"))
            exponents.add(eqn.charAt(eqn.length()-1));
        else
            exponents.add('0');
        int temp[]=toint(exponents);
//        for (int i = 0; i < temp.length; i++) {
//            System.out.print(temp[i]+" ");
//        }
//        System.out.println();
//        System.out.println(coefficients);
        double[] coeff=new double[coefficients.size()];
        for (int i = 0; i < coeff.length; i++) {
            String []strarr=coefficients.get(i).split("[s^]");
            String str=strarr[0];
            coeff[i]=convert_to_double(str);
        }
        return finalcoeff(coeff,temp);
    }
}

@Service
public class RSService {
    static class Pair
    {
        private int changes;
        private boolean status;

        public int getChanges() {
            return changes;
        }

        public boolean isStatus() {
            return status;
        }
    }

    // Check stability using Routh-Hurwitz criterion
    public static void print(double[][]arr)
    {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.print(arr[i][j]+" ");
            }
            System.out.println();
        }
    }

    public double[][] getTable(String eqn){
        double[] coeffs = ExtractCoeffecients.extract_coeff(eqn);
        double[][]routh=new double[coeffs.length][(coeffs.length+1)/2];
        int l=0,k=1;
        boolean entire_is_zero=true;
        for (int i = 0; i < routh[0].length; i++) {
            routh[0][i]=coeffs[l];
            if(k< coeffs.length)
                routh[1][i]=coeffs[k];
            l=l+2;k=k+2;
        }
        for (int i = 2; i < routh.length; i++) {
            for (int j = 0; j < routh[0].length-1; j++) {
                if(routh[i-1][j]==0)
                    routh[i-1][j]=1E-300;
                routh[i][j] = (routh[i-1][0]*routh[i-2][j+1] - routh[i-2][0]*routh[i-1][j+1]) / routh[i-1][0];
            }
        }
        return routh;
    }
    public Pair isStable(double[] coeffs) {
        double[][]routh=new double[coeffs.length][(coeffs.length+1)/2];
        int l=0,k=1;
        boolean entire_is_zero=true;
        for (int i = 0; i < routh[0].length; i++) {
            routh[0][i]=coeffs[l];
            if(k< coeffs.length)
                routh[1][i]=coeffs[k];
            l=l+2;k=k+2;
        }
        for (int i = 2; i < routh.length; i++) {
            for (int j = 0; j < routh[0].length-1; j++) {
                if(routh[i-1][j]==0)
                    routh[i-1][j]=1E-300;
                routh[i][j] = (routh[i-1][0]*routh[i-2][j+1] - routh[i-2][0]*routh[i-1][j+1]) / routh[i-1][0];
            }
        }
        for (int i = 1; i < routh.length; i++) {
            for (int j = 0; j < routh[0].length; j++) {
                entire_is_zero = true;
                if(routh[i][j]>1E-300/*&&i< routh.length-1*/) {
                    entire_is_zero=false;
                    break;
                }
            }
            if (entire_is_zero) break;
        }
        print(routh);
        int signchange=0;
        for (int i = 1; i < routh.length; i++) {
            if(routh[i][0]*routh[i-1][0]<0)
                signchange++;
        }
//        System.out.println(signchange);
        Pair p=new Pair();
        p.changes=signchange;
        if(signchange==0&&entire_is_zero)
        {
            p.status=false;return p;
        }
        p.status=(signchange==0);
        return p;
    }

    public Pair solve(String eqn){
        System.out.println(eqn);
        double[] coeffs = ExtractCoeffecients.extract_coeff(eqn);
        System.out.println(Arrays.toString(coeffs));
        //pair
        return this.isStable(coeffs);
    }

    public static void main(String[] args) {
        // Example usage
//        String eqn="s^5+s^4+2s^3+2s^2+s+1";
//        String eqn = "s^4+2s^3+3s^3+4s+5";
//        String eqn = "s^3+10s^2+31s+1030";
//        String eqn = "s^3+s^2+2s+24";
//        String eqn = "s^3+2s^2+s+2";

//        String eqn = "s^2+2s+1";
//        String eqn = "s^3+8s^2+30s+50";
//        String eqn = "s^4+22s^3+10s^2+2s+0.4";
        String[] arr = new String[]{
                "s^5+s^4+2s^3+2s^2+s+1",
                "s^4+2s^3+3s^3+4s+5",
                "s^3+10s^2+31s+1030",
                "s^3+s^2+2s+24",
                "s^3+2s^2+s+2",
                "s^2+2s+1",
                "s^3+8s^2+30s+50",
                "s^4+22s^3+10s^2+2s+0.4",
                "s^2+1"
        };
        for(String eqn:arr){
            double[] coeffs = ExtractCoeffecients.extract_coeff(eqn);
            System.out.println();
            System.out.println(Arrays.toString(coeffs));
            System.out.println();
            RSService routh=new RSService();
            Pair p = routh.isStable(coeffs);
            System.out.println("The system is " + (p.isStatus() ? "stable" : "unstable"));
            System.out.println("Number of RHS Poles = "+p.getChanges());
        }

//        double eps=1E-300;
//        System.out.println((2*eps-eps)/eps);
    }

}
