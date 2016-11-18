
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
public class CreateJson {
    public static void main(String[] args) throws Exception
    {
        JSONArray array = new JSONArray();
        JsonArrayBuilder builderJS = Json.createArrayBuilder(); 
        String dateTime = "", message = "";
        //String fichier ="TestImp.txt";
        String fichier ="TweetCausePourLaCauseInfosPrincipal.txt";
        //String FichierJsonSimple="TestImp.json";
        String FichierJsonSimple="TweetCausePourLaCauseInfosPrincipal.json";
        
        try{
            InputStream ips=new FileInputStream(fichier); 
            InputStreamReader ipsr=new InputStreamReader(ips);
            BufferedReader br=new BufferedReader(ipsr);
            String ligne;
            int cpt= 0;
            while ((ligne=br.readLine())!=null && cpt < 5000){
                if(!ligne.equalsIgnoreCase("")){
                    String line [] = ligne.split("\t");
                    //System.out.println(line.length);
                    if(line.length == 3){ 
                        cpt++;
                        message = line[1];
                        dateTime = line[2];
                        JsonObjectBuilder builderId = Json.createObjectBuilder();
                        builderId.add("Message", message);
                        builderId.add("DateTime", dateTime);
                        JsonObject jsonObj = builderId.build();
                        builderJS.add(jsonObj); 
                    }else if (line.length == 2){
                        cpt++;
                        message =line[0] ;
                        dateTime =line[1];
                        JsonObjectBuilder builderId = Json.createObjectBuilder();
                        builderId.add("Message", message);
                        builderId.add("DateTime", dateTime);
                        JsonObject jsonObj = builderId.build();
                        builderJS.add(jsonObj); 
                    }
                }
            }
            System.out.println(cpt);
            br.close();
            JsonArray JS = builderJS.build() ;
            /**************FICHIER JSON********************/
            try {
                FileWriter file = new FileWriter(FichierJsonSimple);
                file.write(JS.toString());
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            System.out.println(e.toString());
	}
    }
}
