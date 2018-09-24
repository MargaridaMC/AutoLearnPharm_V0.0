package entrega1;

import java.io.*;
import java.util.Arrays;

public class CSVReader{

    public static void reader(String[] arg) throws Exception {
        // throws indica ao Java para dar erro caso o ficheiro não exista

        BufferedReader CSVFile = new BufferedReader(new FileReader("EM20.csv"));

        String dataRow = CSVFile.readLine(); // Le a primeira linha.

        while (dataRow != null){
            // Qd chega ao fim o dataRow fica a null
            String[] dataArray = dataRow.split(";");
            //split() mŽtodo que separa as dados da string num array 
            double[] doubleArray=new double[dataArray.length];
            //vari‡vel intArray vai guardar os dados como inteiros; 
            int i=0;
            while(i<dataArray.length) {
                    doubleArray[i]=Double.parseDouble(dataArray[i]);
                    System.out.println(doubleArray[i]);
                //parseInt transforma uma string num inteiro 
                i++;
            }
            System.out.println(Arrays.toString(doubleArray)); // Imprime um Array
            dataRow = CSVFile.readLine(); // Le a proxima linha
        }
        CSVFile.close();
        //Fechar o ficheiro
        System.out.println();
    }
} 