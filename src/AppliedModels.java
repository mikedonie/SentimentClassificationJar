import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import org.apache.commons.cli.*;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
//import org.json.*;
import javax.json.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mikedonald
 */

public class AppliedModels {
    public static void Applied (String pathIn, String pathOut, String FichierJson, String path, String detect, int TN) throws Exception
    {
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(pathIn)));
        String line;

        FileWriter fw = new FileWriter(pathOut);
        PrintWriter output = new PrintWriter(new BufferedWriter(fw));

        CalculAttributs ca = new CalculAttributs(path);
        ConstructionARFF obj = new ConstructionARFF(path,ca);
        
        Properties prop = new Properties();
	InputStream input = new FileInputStream(path);
        prop.load(input);
        
        String dateHeure = "11/11/2000 17:40:48";
            
        // Liste des attributs
        ArrayList<Attribute> atts = new ArrayList(2);
        // Ajouter le descripteur
        atts.add(new Attribute("_text",(ArrayList<String>)null));
        // Construire l'attribut de classe
        ArrayList<String> classVal = new ArrayList<String>();
        // Ajouter l'attribut de classe (nominal)
        atts.add(new Attribute("_class",classVal));
        
        
        // Créer l'objet Instances data ayant comme attributs atts
        Instances data = new Instances("instance",atts,0);
        // L'instance
        Instance ins = new DenseInstance(2);
        ins.setDataset(data);
        while ((line=r.readLine())!=null){
            ins.setValue(0, line);
            data.add(ins);
        }
        
        int id=0;
        Instance inst;
        
        //Emotions
        String Emo[] = {"DEPLAISIR", "DERANGEMENT", "MEPRIS", "SURPRISE_NEGATIVE", "PEUR","COLERE","ENNUI","TRISTESSE","PLAISIR","APAISEMENT","AMOUR","SURPRISE_POSITIVE",
            "SATISFACTION","INSATISFACTION","ACCORD","VALORISATION","DESACCORD","DEVALORISATION","INSTRUCTION"};
        int EmoVal[] = {179, 203, 329, 299, 119,191,359,149,19,29,39,269,49,215,59,9,227,239,-1};
        
        String Pol[] = {"POSITIF", "NEGATIF", "NEUTRE"};
        
        JsonArrayBuilder builderJS = Json.createArrayBuilder(); 
        JsonArrayBuilder builderJsonEric = Json.createArrayBuilder(); 
        
        data = obj.ConstructionInstances(data);
        for (Instance in:data){
            JsonArrayBuilder BuilderJAB = Json.createArrayBuilder();
            id++;
           /*StringToWordVector stw = (StringToWordVector) weka.core.SerializationHelper.read("models//DEFT15_T1_STW.model");
            AttributeSelection ats = (AttributeSelection)weka.core.SerializationHelper.read("models//DEFT15_T1_IG.model");
            Classifier clsSMO = (Classifier) weka.core.SerializationHelper.read("models//DEFT15_T1_SMO.model");*/
            StringToWordVector stw = (StringToWordVector) weka.core.SerializationHelper.read(prop.getProperty("StringToWordVector"));
            AttributeSelection ats = (AttributeSelection) weka.core.SerializationHelper.read(prop.getProperty("AttributeSelection"));
            Classifier clsSMO = (Classifier) weka.core.SerializationHelper.read(prop.getProperty("Classifier"));
        
            stw.input(in);
            inst = stw.output();
            ats.input(inst);
            inst = ats.output();
            int classe=(int)clsSMO.classifyInstance(inst);
            int NbC = clsSMO.distributionForInstance(inst).length ;  //Nombre de classe
            int IndP [] = new int[NbC] ;
            int Rank [] = new int[NbC] ;
                
            double [] DistP = new double [NbC];  //for probabilities dist
                
            for (int k = 0 ; k < NbC ; k++){
                IndP[k] = k ;
                Rank[k] = 0 ;
            }
                
            HashMap<String, Integer>  map = new HashMap<String, Integer>() ;            
            //sSystem.out.println(inst.attribute(0)) ;
            Affect(clsSMO.distributionForInstance(inst), DistP);
            RangeTable(DistP, IndP, Rank);
            //System.out.println("SMO");
            //Affichage (Emo, IndP, DistP, Rank, NbC) ;  System.out.println();
            if(detect.equalsIgnoreCase("polarity")){
                IncrMap(map, DistP, IndP, Pol, TN, NbC);
            }else if(detect.equalsIgnoreCase("emotion")){
                IncrMap(map, DistP, IndP, Emo, TN, NbC);
            }  
            //Start with tree
            TreeMap<String, Integer> treemap = new TreeMap<String, Integer>(map);
            Map sortedMap = sortByValues(treemap);
            Set set = sortedMap.entrySet();
            Iterator i = set.iterator();
            int cpt = 0 ;
            int val = 0 ;
            // Display elements
            output.print(id+"\t");
            while((i.hasNext()) && (cpt < TN)) {
                Map.Entry me = (Map.Entry)i.next();
                //System.out.println(me.getKey() + " : "+me.getValue());
                output.print(me.getKey() + ":"+me.getValue()+"\t");
                //Ajout des infos dans le JSON
                JsonObjectBuilder buildertmp = Json.createObjectBuilder();
                buildertmp.add("Class",  me.getKey().toString());

                /***Pour le JSon à enlever apres***/
                for (int l=0; l<Emo.length; l++){
                    if(Emo[l].equalsIgnoreCase(me.getKey().toString()))
                        val = l;
                }
                /*********************************/
                
                buildertmp.add("Prob_class", new BigDecimal(DistP[(int)me.getValue()-1]).setScale(5, BigDecimal.ROUND_HALF_EVEN));
                JsonObject Jtmp = buildertmp.build() ;
                BuilderJAB.add(Jtmp);                  
                cpt++;
            }
                
            JsonArray JAB = BuilderJAB.build();
            JsonObjectBuilder builderId = Json.createObjectBuilder();
            builderId.add("id", id);
            builderId.add("classes", JAB);
            JsonObject JsId = builderId.build();
            builderJS.add(JsId);
                
            
            /*********************************Construire le json pour Eric ********************************************/
            if(id % 100  == 0){
                GregorianCalendar gc = new GregorianCalendar();
                int year = randBetween(1900, 2010);
                gc.set(gc.YEAR, year);
                int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
                gc.set(gc.DAY_OF_YEAR, dayOfYear);
                dateHeure = gc.get(gc.DAY_OF_MONTH)+"/"+(gc.get(gc.MONTH) + 1)+"/"+gc.get(gc.YEAR);
                dateHeure+=" 17:40:48";
                System.out.println(dateHeure);
            }
            JsonObjectBuilder builderIdEric = Json.createObjectBuilder();
            builderIdEric.add("date_time", dateHeure);
            builderIdEric.add("value", EmoVal[val]);
            builderJsonEric.add(builderIdEric);
            /*********************************************Fin********************************************/
            
            //System.out.println("\n");
            output.println();
            output.flush();
        }
            
        
        JsonArray JS = builderJS.build() ;
        
        /**************FICHIER JSON********************/
        if(!FichierJson.equalsIgnoreCase("")){
            try {
                FileWriter file = new FileWriter(FichierJson);
                //System.out.println(JS.toString());
		file.write(JS.toString());
		file.flush();
		file.close();
            } catch (IOException e) {
		e.printStackTrace();
            }
        }
        /*****************************************************/
            
            /**********************Sortie Eric*************************
            BufferedReader br = new BufferedReader(new FileReader("EnteteSentiment.json"));
            String sCurrentLine;
            String BeginFile = "";
            while ((sCurrentLine = br.readLine()) != null) {
                BeginFile+=sCurrentLine;
            }
            JsonArray JSEric = builderJsonEric.build() ;
            String EndFile = JSEric.toString()+"\\}";
            String AllFile = BeginFile+EndFile;
            FileWriter fileEric = new FileWriter("jsonEric.json");
            fileEric.write(AllFile);
            fileEric.flush();
            fileEric.close();
            /*****************************************************/
            
       output.close();
    }
    
    public static Instances makeInstance(String tweet) {

        // Liste des attributs
        ArrayList<Attribute> atts = new ArrayList(2);
        
        // Ajouter le descripteur
        atts.add(new Attribute("_text",(ArrayList<String>)null));
        
        // Construire l'attribut de classe
        ArrayList<String> classVal = new ArrayList<String>();
        classVal.add("+");
        classVal.add("-");
        classVal.add("=");
        
        // Ajouter l'attribut de classe (nominal)
        atts.add(new Attribute("_class",classVal));
        
        // Créer l'objet Instances data ayant comme attributs atts
        Instances data = new Instances("instance",atts,0);
        
        // L'instance
        Instance ins = new DenseInstance(2);
        ins.setDataset(data);
        
        // Remplir Instance
        ins.setValue(0, tweet);
        data.add(ins);
        
        return data;
  }
  
    public static void RangeTable (double [] Tab, int [] Ind, int [] Rank){
        int i_max, ti, cpt = 0, rang = 1 ;
        double t;
        for (int i = 0; i < Tab.length - 1; ++i) {
            i_max = i;
            for (int j = i+1; j < Tab.length; ++j)
                if (Tab[j] >= Tab[i_max])
                    i_max = j;
            t = Tab[i_max]; Tab[i_max] = Tab[i]; Tab[i] = t ;
            ti = Ind[i_max]; Ind[i_max] = Ind[i]; Ind[i] = ti ;
        }  
        Rank[0] = 1 ;
        for (int i = 1; i < Tab.length ; i++) {
            if(Tab[i-1] != Tab[i]){
                    rang = rang + cpt + 1 ;
                    cpt = 0 ;
            }else
                cpt++;
            Rank[i] = rang ;
        }
    }
    
    public static HashMap IncrMap(HashMap Ma, double [] Tab, int [] Ind, String [] Emo, int TN, int NbC ){
        int rang = 1 ;
        int cpt = 0 ;
        for (int k = 0; k < NbC ; k++){
            //System.out.print("\t"+Emo[Ind[NbC-k-1]]+":"+Tab[Ind[NbC-k-1]]);
            if(k>0){
                if(Tab[k-1] != Tab[k]){
                    rang = rang + cpt + 1 ;
                    cpt = 0 ;
                }
                else
                    cpt++;
            }
            
            if (!Ma.containsKey(Emo[Ind[k]])){
                Ma.put(Emo[Ind[k]], rang) ;
            }else{
                int oldvalue = (int) Ma.get(Emo[Ind[k]]) ;
                Ma.replace(Emo[Ind[k]], oldvalue+rang);
            }
        }
        return Ma;
    }
    
    public static <K, V extends Comparable<V>> Map<K, V> 
    sortByValues(final Map<K, V> map) {
    Comparator<K> valueComparator = 
             new Comparator<K>() {
      public int compare(K k1, K k2) {
        int compare = 
              map.get(k1).compareTo(map.get(k2));
        if (compare == 0) 
          return 1;
        else 
          return compare;
      }
    };
 
    Map<K, V> sortedByValues = 
      new TreeMap<K, V>(valueComparator);
    sortedByValues.putAll(map);
    return sortedByValues;
  }
    
    public static void Affichage (String [] Emo, int [] IndP, double [] DistP, int [] Rank, int firstvalue){
        for (int k = 0; k < firstvalue ; k++){
            System.out.println(Rank[k]+"\t"+Emo[IndP[k]]+":"+(new BigDecimal(DistP[k])).setScale(5, BigDecimal.ROUND_HALF_EVEN));
        }
    }   
    
    public static void Affect (double [] T2, double [] T1){
        int lg = T1.length;
        for (int i = 0; i < lg; i++)
            T1[i] = T2[i];
    }
    
    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}