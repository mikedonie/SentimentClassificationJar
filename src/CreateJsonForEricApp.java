
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mikedonald
 */
public class CreateJsonForEricApp {
    
    public static void main(String[] args) throws Exception            
    {
        Options options = new Options();
        
        Option inp = new Option("j", "jinput", true, "input json file");
        inp.setRequired(true);
        options.addOption(inp);

        Option outp = new Option("o", "joutput", true, "output json file");
        outp.setRequired(true);
        options.addOption(outp);
        
        Option fil = new Option("f", "filetxt", true, "principal file");
        fil.setRequired(true);
        options.addOption(fil);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }
        
        String jsonfile = "";
        if(cmd.hasOption("jinput"))
            jsonfile = cmd.getOptionValue("inputjson");  
        
        String jsonEric = "";
        if(cmd.hasOption("joutput"))
            jsonEric = cmd.getOptionValue("output");  

        String FichierDeTweet = "";
        if(cmd.hasOption("filetxt"))
            FichierDeTweet = cmd.getOptionValue("file");

        
        JsonArrayBuilder builderJsonEric = Json.createArrayBuilder(); 
        String sCurrentLine, StringJson = "";
        
        BufferedReader br = new BufferedReader(new FileReader(jsonfile));
        while ((sCurrentLine = br.readLine()) != null) {
            StringJson+=sCurrentLine;
        }
        
        ArrayList<String> ArrResult =new ArrayList<String>();
        JsonArrayBuilder builderJS = Json.createArrayBuilder();
        JsonArray JS = builderJS.build() ;
        JSONArray array = new JSONArray(StringJson);
        for(int i=0; i<array.length(); i++){
            JSONObject jsonObj  = array.getJSONObject(i);
            JSONArray sarray = jsonObj.getJSONArray("classes");
            if(sarray.length() > 0){
                JSONObject ObjArray = (JSONObject) sarray.get(0) ;
                System.out.print(jsonObj.getInt("id")+"\t");
                System.out.println(ObjArray.getString("Class"));
                ArrResult.add(ObjArray.getString("Class"));
            }
        }
        
        int id = 0 ;
        br = new BufferedReader(new FileReader(FichierDeTweet));
        while((sCurrentLine = br.readLine()) != null){
            String line [] = sCurrentLine.split("\t");
            //Parcours du fichier ; 
            String dateHeure = line[1];
            
            JsonObjectBuilder builderIdEric = Json.createObjectBuilder();
            builderIdEric.add("date_time", dateHeure);
            builderIdEric.add("value", ArrResult.get(id));
            builderJsonEric.add(builderIdEric);
            id++;
        }
            
        /**********************Sortie Eric*************************/
        br = new BufferedReader(new FileReader("EnteteSentiment.json"));
        String BeginFile = "";
        while ((sCurrentLine = br.readLine()) != null){
            BeginFile+=sCurrentLine;
        }
        JsonArray JSEric = builderJsonEric.build();
        String EndFile = JSEric.toString()+"\\}";
        String AllFile = BeginFile+EndFile;
        FileWriter fileEric = new FileWriter(jsonEric);
        fileEric.write(AllFile);
        fileEric.flush();
        fileEric.close();
        /*****************************************************/
        //System.out.println(array);
    }
}
