
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

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
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("ImpatientesSentiAnalysisEmotion.txt")));
        String line, sCurrentLine;
        ArrayList<String> ArrImp =new ArrayList<String>();
        ArrayList<String> ArrFac=new ArrayList<String>();
        ArrayList<String> ArrCan =new ArrayList<String>();
        int nbPostFac = 40, nbPostCan = 200, nbPostImp = 200, PostClas = 60;
        
        FileWriter fw = new FileWriter("CorpusAAnoterImpatientes.txt");
        PrintWriter output = new PrintWriter(new BufferedWriter(fw));
        
        HashMap<String, Integer>  map = new HashMap<String, Integer>() ;  
        
        while ((sCurrentLine = r.readLine()) != null) {
            String Message = sCurrentLine;
            ArrImp.add(Message);
        }
        r.close();
        
        /*r = new BufferedReader(new InputStreamReader(new FileInputStream("CancerDuSein.txt")));
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
        r.close();*/
        
        int i  = 0, postImp = 0 ; int cpt = 0, cptClas = 0;
        while (i < ArrImp.size()){
            System.out.println(ArrImp.get(i));
            
            StringTokenizer st = new StringTokenizer(ArrImp.get(i),"\t");
            if(st.countTokens()>=4) {
                st.nextElement();
		String phr = st.nextElement().toString();
                //String [] Case = ArrImp.get(i).split("  ");
                //System.out.println(Case.length+":"+Case[0]);
                String FirstClassInfo = st.nextElement().toString();
                String SecondClassInfo = st.nextElement().toString();
                //System.out.println("Message: "+FirstClassInfo);
                
                String FirstEmo = FirstClassInfo.split(":")[0];
                String FirstProb = FirstClassInfo.split(":")[1]; 
                String SecondProb = SecondClassInfo.split(":")[1];
                
                System.out.println("Message: "+FirstProb+" and "+SecondProb);
                
                if ((cpt == 0)&&(FirstProb.equalsIgnoreCase("0.33333"))&&(SecondProb.equalsIgnoreCase("0.20000"))){
                    if(!map.containsKey(FirstEmo)){ 
                        map.put(FirstEmo, 1);
                        output.println("LesImpatientes\t"+phr);
                        cptClas++;
                    }
                    else if (map.get(FirstEmo) <= PostClas){
                        int oldValue = map.get(FirstEmo);
                        map.replace(FirstEmo, oldValue+1);
                        output.println("LesImpatientes\t"+phr);
                        cptClas++;
                    }
                }else if ((cpt == 1)&&(FirstProb.equalsIgnoreCase("0.33333"))&&(SecondProb.equalsIgnoreCase("0.26667"))){
                    if(!map.containsKey(FirstEmo)){
                        map.put(FirstEmo, 1);
                        output.println("LesImpatientes\t"+phr);
                        cptClas++;
                    }
                    else if (map.get(FirstEmo) <= PostClas){
                        int oldValue = map.get(FirstEmo);
                        map.replace(FirstEmo, oldValue+1);
                        output.println("LesImpatientes\t"+phr);
                        cptClas++;
                    }
                }
            }
            
            i++;
            if ((i == ArrImp.size())&&(cptClas<nbPostImp)){
                cpt++;
                i = 0 ;
            }
        }
        output.close();
        r.close();
    }
}