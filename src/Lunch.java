
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Amine
 */
public class Lunch {
    public static void main(String[] args)
    throws Exception
  {  
      String pathIn = args[0];
      String pathOut = args[1];
      
      BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(pathIn)));
      String line;
      
      FileWriter fw = new FileWriter(pathOut);
      PrintWriter output = new PrintWriter(new BufferedWriter(fw));
      
      CalculAttributs ca = new CalculAttributs();
      StringToWordVector stw = (StringToWordVector) weka.core.SerializationHelper.read("models//DEFT15_T2.2_STW.model");
      AttributeSelection ats = (AttributeSelection)weka.core.SerializationHelper.read("models//DEFT15_T2.2_IG.model");
      Classifier cls = (Classifier) weka.core.SerializationHelper.read("models//DEFT15_T2.2_SMO.model");
      
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
        ConstructionARFF obj = new ConstructionARFF(ca);
        data = obj.ConstructionInstances(data);
        int id=0;
        Instance inst;
        for (Instance in:data){
            id++;
            stw.input(in);
            inst = stw.output();
            ats.input(inst);
            inst = ats.output();

            double classe=cls.classifyInstance(inst);

            output.print(id+"\t");

            if (classe==0) {
                output.println("DEPLAISIR ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==1) {
                output.println("DERANGEMENT ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==2) {
                output.println("MEPRIS ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==3) {
                output.println("SURPRISE_NEGATIVE ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==4) {
                output.println("PEUR ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==5) {
                output.println("COLERE ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==6) {
                output.println("ENNUI ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==7) {
                output.println("TRISTESSE ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==8) {
                output.println("PLAISIR ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==9) {
                output.println("APAISEMENT ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==10) {
                output.println("AMOUR ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==11) {
                output.println("SURPRISE_POSITIVE ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==12) {
                output.println("SATISFACTION ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==13) {
                output.println("INSATISFACTION ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==14) {
                output.println("ACCORD ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==15) {
                output.println("VALORISATION ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==16) {
                output.println("DESACCORD ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==17) {
                output.println("DEVALORISATION ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else if (classe==18) {
                output.println("INSTRUCTION ("+cls.distributionForInstance(inst)[(int) classe]+")");
            }
            else {
                output.println("Erreur");
            }
            /*if (classe==0) {
                output.println("POSITIF");
            }
            else if (classe==1) {
                output.println("NEGATIF");
            }
            else if (classe==2) {
                output.println("NEUTRE");
            }
            else {
                output.println("Erreur");
            }*/
            output.flush();
       }
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
    
}
