import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.supervised.instance.Resample;  //pourquoi pas unservised


public class Arrfff
{
   public static void main(String[] args) throws FileNotFoundException, IOException
  {
        FileWriter fw = new FileWriter("Test/Test2.2M.arff");
        PrintWriter output = new PrintWriter(new BufferedWriter(fw));
        
        BufferedReader r , rt;
        Instances train, test = null;
        
        r = new BufferedReader(new InputStreamReader(new FileInputStream("Test/Test2.2.arff"), "UTF-8"));
        train = new Instances(r);
        train.setClassIndex(train.numAttributes() - 1);
        
        output.append("@relation tache2.2M\n");
			output.append("@attribute _text string\n");
			output.append("@attribute _class {JOIE, PEUR, COLERE, SURPRISE, DEGOUT, TRISTESSE}\n");
			output.append("\n\n");
			output.append("@data\n");
                        
        for (int i=0; i<train.numInstances(); i++){
            String TheString = train.instance(i).stringValue(train.attribute("_text"));
            TheString = weka.core.Utils.quote(TheString);
            String TheClass = train.instance(i).stringValue(train.attribute("_class")).toString();
            if((TheClass.equalsIgnoreCase("VALORISATION")) || (TheClass.equalsIgnoreCase("PLAISIR")) || (TheClass.equalsIgnoreCase("APPAISEMENT"))
                    || (TheClass.equalsIgnoreCase("AMOUR")) ||(TheClass.equalsIgnoreCase("SATISFACTION")) ||(TheClass.equalsIgnoreCase("ACCORD"))){
                TheClass = "JOIE" ;
                output.println(TheString+","+TheClass);
            }else if((TheClass.equalsIgnoreCase("PEUR")) || (TheClass.equalsIgnoreCase("TRISTESSE"))){
                TheClass = "PEUR" ;
                output.println(TheString+","+TheClass);
            }else if((TheClass.equalsIgnoreCase("COLERE")) || (TheClass.equalsIgnoreCase("DERANGEMENT")) || (TheClass.equalsIgnoreCase("INSATISFACTION"))
                    || (TheClass.equalsIgnoreCase("DESACCORD")) ||(TheClass.equalsIgnoreCase("DEVALORISATION"))){
                TheClass = "COLERE" ;
                output.println(TheString+","+TheClass);
            }else if((TheClass.equalsIgnoreCase("SURPRISE_POSITIVE")) || (TheClass.equalsIgnoreCase("SURPRISE_NEGATIVE"))){
                TheClass = "SURPRISE" ;
                output.println(TheString+","+TheClass);
            }else if((TheClass.equalsIgnoreCase("MEPRIS")) || (TheClass.equalsIgnoreCase("ENNUI"))){
                TheClass = "DEGOUT" ;
                output.println(TheString+","+TheClass);
            }else if((TheClass.equalsIgnoreCase("TRISTESSE")) || (TheClass.equalsIgnoreCase("DEPLAISIR"))){
                TheClass = "TRISTESSE" ;
                output.println(TheString+","+TheClass);
            }
        }
        output.close();
  }

}