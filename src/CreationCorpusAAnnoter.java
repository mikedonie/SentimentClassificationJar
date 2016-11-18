
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mikedonald
 */
public class CreationCorpusAAnnoter {
    
    
  public static void main(String[] args) throws IOException{
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("Impatientes.txt")));
        String line, sCurrentLine;
        ArrayList<String> ArrImp =new ArrayList<String>();
        ArrayList<String> ArrFac=new ArrayList<String>();
        ArrayList<String> ArrCan =new ArrayList<String>();
        int nbPostFac = 40, nbPostCan = 200, nbPostImp = 200, PostClas = 60;
        
        FileWriter fw = new FileWriter("CorpusAAnoter.txt");
        PrintWriter output = new PrintWriter(new BufferedWriter(fw));
        
        HashMap<String, Integer>  map = new HashMap<String, Integer>() ;  
        
        while ((sCurrentLine = r.readLine()) != null) {
            String Message = sCurrentLine;
            ArrImp.add(Message);
        }
        r.close();
        
        r = new BufferedReader(new InputStreamReader(new FileInputStream("CancerDuSein.txt")));
        while ((sCurrentLine = r.readLine()) != null) {
            String Message = sCurrentLine;
            ArrCan.add(Message);
        }
        r.close();
        
        r = new BufferedReader(new InputStreamReader(new FileInputStream("Facebook.txt")));
        while ((sCurrentLine = r.readLine()) != null) {
            String Message = sCurrentLine;
            ArrFac.add(Message);
        }
        r.close();
        
        int i  = 0, postImp = 0 ; int cpt = 0 ;
        while (i < ArrImp.size()){
            String [] Case = ArrImp.get(i).split("\t");
            
            String FirstEmo = Case[2].split("\t")[0];
            String FirstProb = Case[2].split("\t")[1]; 
            String SecondProb = Case[3].split("\t")[1];
            
            if ((cpt == 0)&&(FirstProb.equalsIgnoreCase("0.3333"))&&(SecondProb.equalsIgnoreCase("0.2000"))){
                if(!map.containsKey(FirstEmo)){ 
                    map.put(FirstEmo, 1);
                    output.println("LesImpatientes\t"+ArrImp.get(i));
                }
                else if (map.get(FirstProb) <= PostClas){
                    int oldValue = map.get(FirstProb);
                    map.replace(FirstEmo, oldValue+1);
                    output.println("LesImpatientes\t"+ArrImp.get(i));
                }
            }
            
            if ((cpt == 1)&&(FirstProb.equalsIgnoreCase("0.3333"))&&(SecondProb.equalsIgnoreCase("0.2000"))){
                if(!map.containsKey(FirstEmo)){ 
                    map.put(FirstEmo, 1);
                    output.println("LesImpatientes\t"+ArrImp.get(i));
                }
                else if (map.get(FirstProb) <= PostClas){
                    int oldValue = map.get(FirstProb);
                    map.replace(FirstEmo, oldValue+1);
                    output.println("LesImpatientes\t"+ArrImp.get(i));
                }
            }
            
            i++;
            if (i == ArrImp.size()){
                cpt++;
                i = 0 ;
            }
        }
        output.close();
        r.close();
    }
}