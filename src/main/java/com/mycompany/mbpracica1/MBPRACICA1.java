/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mbpracica1;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class MBPRACICA1 {
    static int nRanking = 1;
    static int nConsulta = 1;
    public static void main(String[] args) throws SolrServerException {
        // Ruta del archivo que contiene el texto con el formato proporcionado
        
        String ruta = "C:\\Users\\luism\\OneDrive\\Documentos\\NetBeansProjects\\PRACTICAMOTORES\\corpusquerys.xml";
        

        try {
            List<String> contentList = getContentBetweenWAndI(ruta);

            // Imprimir el contenido entre ".W" y ".I"
            for (String content : contentList) {
                System.out.println("QUERY");
                lanzaConsulta(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getContentBetweenWAndI(String filePath) throws IOException {
        List<String> contentList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            boolean shouldRead = false;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(".")) {
                    if (shouldRead) {
                        contentList.add(content.toString());
                        content.setLength(0);
                    }
                    shouldRead = false;
                }

                if (shouldRead) {
                    content.append(line).append("\n");
                }

                if (line.startsWith(".W")) {
                    shouldRead = true;
                }
            }

            if (shouldRead && content.length() > 0) {
                contentList.add(content.toString());
            }
        }

        return contentList;
    }
    
    private static void lanzaConsulta(String consulta) throws SolrServerException, IOException   
    {
       
       HttpSolrClient solr = new HttpSolrClient.Builder("http://localhost:8983/solr/gate3").build();
       SolrQuery query = new SolrQuery();
       consulta = consulta.replaceAll("[^a-zA-Z0-9 ]","");
       
       
       

       List<String> personas = new ArrayList<>();
       List<String> orgs = new ArrayList<>();
       List<String> lugares = new ArrayList<>();
       
       String etiquetaPer = "Person";
       personas = buscarContenido(etiquetaPer,consulta);
       String etiquetaOrg = "Organization";
       orgs = buscarContenido(etiquetaOrg,consulta);
       String etiquetaLugar = "Location";
       lugares = buscarContenido(etiquetaLugar,consulta);
       
       String palabras = reemplazarContenidoEtiqueta(consulta);
       System.out.println(palabras);
       
       if (!personas.isEmpty()) {
           System.out.println("No esta vacia"); 
           query.addFilterQuery("Personas: " + personas);
        }

        if (!orgs.isEmpty()) {
            System.out.println("Organiazaciones No esta vacia");
            query.addFilterQuery("Organizaciones: " + orgs);
        }

        if (!lugares.isEmpty()) {
            query.addFilterQuery("Lugares: " + lugares);
        }
       
       
       query.setQuery("*");
       query.setFields("autor", "titulo", "score", "idp");
       query.addFilterQuery("texto: " + palabras);
       QueryResponse rsp = solr.query(query);
       SolrDocumentList docs = rsp.getResults();
       
       converTrec(docs,nConsulta);
       //System.out.println(nConsulta);
       nConsulta++;
       
       
       
    }
    
    private static void converTrec(SolrDocumentList docs, int nConsulta) throws IOException
    {
        
        String ruta = "C:\\Users\\luism\\OneDrive\\Documentos\\NetBeansProjects\\MBPRACICA1\\miTrec.TREC";
        File filename = new File(ruta);
        BufferedWriter escritor = new BufferedWriter(new FileWriter(filename,true));
        for (SolrDocument doc: docs)
        {
            Object titulo = doc.getFieldValue("titulo");
            Object autor = doc.getFieldValue("autor");
            Object score  = doc.getFieldValue("score");
            Object idp = doc.getFieldValue("idp");
            
            String tituloSTR = titulo.toString();
            String autorSTR = autor.toString();
            String scoreSTR = score.toString();
            String idpSTR = idp.toString();
            String patron = "[^a-zA-Z0-9\\s]"; 
            String idpSTR1 = idpSTR.replaceAll(patron, "");
            
            escritor.write(nConsulta + " " + "Q0" + " " + idpSTR1 + " " + nRanking + " " + scoreSTR + " " + "Luismi" + " " + "\n" );
            nRanking++;
        }
        escritor.close();
        
        //adecuaCisi();
        
    }
    
    public static String reemplazarContenidoEtiqueta(String input) {
        // Expresión regular para buscar cualquier etiqueta y reemplazar su contenido
        String regex = "<([A-Za-z0-9]+)>([\\s\\S]*?)</\\1>";
        
        // Reemplazar el contenido utilizando la expresión regular
        return input.replaceAll(regex, "$2");
    }
    
    public static List<String> buscarContenido(String etiqueta, String line) {
        
        List<String> contenidoEncontrado = new ArrayList<>();
        
        // Patrón regex para encontrar las etiquetas
        String patron = "<" + etiqueta + ">(.*?)</" + etiqueta + ">";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(line);
        
        // Buscar todas las coincidencias en la línea
        while (matcher.find()) {
            contenidoEncontrado.add(matcher.group(1)); // Agregar el contenido a la lista
        }
        
        return contenidoEncontrado;
    }
}
