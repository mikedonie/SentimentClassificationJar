import org.apache.commons.cli.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mikedonald
 */

public class Lunch {
    public static void main(String[] args) throws Exception
    {
        Options options = new Options();

        Option work = new Option("w", "work", true, "learn or applied");
        work.setRequired(true);
        options.addOption(work);
        
        Option inp = new Option("i", "input", true, "input file path");
        inp.setRequired(false);
        options.addOption(inp);

        Option outp = new Option("o", "output", true, "output file");
        outp.setRequired(false);
        options.addOption(outp);
        
        Option topn = new Option("t", "topn", true, "top n classes");
        topn.setRequired(false);
        options.addOption(topn);
        
        Option model = new Option("m", "model", true, "choix du modèle");
        model.setRequired(false);
        options.addOption(model);
        
        /*Option detection = new Option("d", "detection", true, "choix du travail");
        detection.setRequired(false);
        options.addOption(detection);*/
        
        Option config = new Option("c", "config", true, "fichier de configuration");
        config.setRequired(true);
        options.addOption(config);
        
        Option jsonfile = new Option("j", "jsonfile", true, "fichier json");
        jsonfile.setRequired(false);
        options.addOption(jsonfile);
        
        Option jsonfileEric = new Option("k", "jsonfileEric", true, "fichier json format Application Eric");
        jsonfileEric.setRequired(false);
        options.addOption(jsonfileEric);
        
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
        
        String pathIn = "";
        if(cmd.hasOption("input"))
            pathIn = cmd.getOptionValue("input");  
        
        String pathOut = "";
        if(cmd.hasOption("output"))
            pathOut = cmd.getOptionValue("output");  

        int TN = 1 ;
        if(cmd.hasOption("topn"))
            TN = Integer.parseInt(cmd.getOptionValue("topn"));

        int modele = 18 ;
        if(cmd.hasOption("model"))
             modele = Integer.parseInt(cmd.getOptionValue("model"));

        /*String detect = "";
        if(cmd.hasOption("detection"))
            detect = cmd.getOptionValue("detection"); */
        
        String propPath="";
        if(cmd.hasOption("config"))
            propPath = cmd.getOptionValue("config");
        
        String workM="";
        if(cmd.hasOption("work"))
            workM = cmd.getOptionValue("work");
        
        String JsonFile = "";
        if(cmd.hasOption("jsonfile"))
            JsonFile = cmd.getOptionValue("jsonfile");
        
         String JsonFileEric = "";
        if(cmd.hasOption("jsonfileEric"))
            JsonFileEric = cmd.getOptionValue("jsonfileEric");
        
        
        if (workM.equalsIgnoreCase("applied")){
            if((pathIn.equalsIgnoreCase("")) || (pathOut.equalsIgnoreCase("")) /*|| (detect.equalsIgnoreCase(""))*/ ){
                System.out.println("Missing required options: \n" +
                "usage: utility-name\n" +
                " -d,--detection <arg>   choix du travail\n" +
                " -i,--input <arg>       input file path\n" +
                " -o,--output <arg>      output file");
                System.exit(1);
            }
            AppliedModels.Applied (pathIn, pathOut, JsonFile, JsonFileEric, propPath/*, detect*/, TN) ;
        }else{
            LearnModels LM = new LearnModels();
            LM.learn(propPath) ;
        }
    }
}