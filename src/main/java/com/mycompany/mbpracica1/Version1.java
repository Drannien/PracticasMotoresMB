/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mbpracica1;

import java.io.File;
import java.util.Scanner;
import java.io.*;
import static java.lang.Thread.sleep;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.midi.Soundbank;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author luism
 */
public class Version1 {

    public static void main(String[] args) throws IOException, SolrServerException, InterruptedException {
        String filename = "C:\\Users\\luism\\OneDrive\\Documentos\\NetBeansProjects\\PRACTICAMOTORES\\corpuscompleto.xml";
        Scanner scan = new Scanner(new File(filename));

        int id = 1;
        String autor = " ";
        String titulo = " ";
        String texto = " ";
        
        
        String persona = "Person";
        String org = "Organization";
        String lugar = "Location";
        
        StringBuilder stb1 = new StringBuilder();
                
        
        
        while (scan.hasNextLine()) {
            
            String line = scan.nextLine();

            if (line.startsWith(".I")) {
                autor = " ";
                titulo = " ";
                texto = " ";
                
                
                
            }  
            if (line.startsWith(".T")) {
                StringBuilder stb = new StringBuilder();
                while (scan.hasNextLine()) {
                    line = scan.nextLine();
                if (line.startsWith(".A")) {
                    break;
                }
                stb.append(line).append("\n");
            }
            titulo = stb.toString().trim();
                
                
            }
            if (line.startsWith(".A")) {
                autor = scan.nextLine().trim();
                
                
            } 
            if (line.startsWith(".W")) {
                StringBuilder stb = new StringBuilder();

                while (scan.hasNextLine()) {
                    line = scan.nextLine();
                    if (line.startsWith(".I") || line.startsWith(".A") || line.startsWith(".T")) {
                        break;
                    } 

                    stb.append(line).append("\n");
                }
                texto = stb.toString().trim();
                
                
            introducedoc(titulo, autor, texto, Integer.toString(id));
            
            String p = "Person";
            //List<String> nuevaLista = encuentraEtiquetas(p);
            
            id++;
            }
        }

    }
    
    
    private static void introducedoc(String titulo, String autor, String texto, String id) throws SolrServerException, IOException
    {
        
        
        if(titulo != " " && autor != " " && texto != " "){
            //System.out.println("El titulo es " + " "+ titulo);
            //System.out.println("El autor es " + " "+ autor);
            //ystem.out.println("El texto es " + " "+ texto);
            //System.out.println("El id" + " " + id);
            
            
            
            
            
            List<String> personas = new ArrayList<>();
            List<String> personasTitulo = new ArrayList<>();
            List<String> personasAutor = new ArrayList<>();
            List<String> orgs = new ArrayList<>();
            List<String> orgsTitulo = new ArrayList<>();
            List<String> orgsAutor = new ArrayList<>();
            List<String> lugares = new ArrayList<>();
            List<String> lugaresTitulo = new ArrayList<>();
            List<String> lugaresAutor = new ArrayList<>(); 
            
            //Busqueda de Personas
            String etiquetaPer = "Person";
            personasTitulo = buscarContenido(etiquetaPer,titulo);
            personas = buscarContenido(etiquetaPer,texto);
            personasAutor = buscarContenido(etiquetaPer,autor);
            
            //Busqueda de Organizaciones
            String etiquetaOrg = "Organization";
            orgsTitulo = buscarContenido(etiquetaOrg,titulo);
            orgs = buscarContenido(etiquetaOrg,texto);
            orgsAutor = buscarContenido(etiquetaOrg,autor);
            
            //Busqueda de Lugares
            String etiquetaLugar = "Location";
            lugaresTitulo = buscarContenido(etiquetaLugar,titulo);
            lugares = buscarContenido(etiquetaLugar,texto);
            lugaresAutor = buscarContenido(etiquetaLugar,autor);
            
            //LIMPIAMOS LAS ETIQUETAS
            
            
            
             //UNION ARRAYS
            personas.addAll(personasTitulo);
            personas.addAll(personasAutor);
            orgs.addAll(orgsTitulo);
            orgs.addAll(orgsAutor);
            lugares.addAll(lugaresTitulo);
            lugares.addAll(lugaresAutor);
            
            
            titulo = reemplazarContenidoEtiqueta(titulo);
            texto = reemplazarContenidoEtiqueta(texto);
            autor = reemplazarContenidoEtiqueta(autor);
            
            //System.out.println(titulo);
            //System.out.println("DOCUMENTO:" + id);
            //System.out.println("Personas: " + personas);
            //System.out.println("Organizaciones: " + orgs);
            //System.out.println("Lugares: " + lugares);
            
            //Creacion del SolrDoc    
            SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/gate3").build();
            SolrInputDocument doc = new SolrInputDocument();
        
            System.out.println("DOCUMENTO: " + id);
            doc.addField("titulo", titulo);
            doc.addField("autor", autor);
            doc.addField("texto", texto);
            doc.addField("idp", id);
            
            
            if (!personas.isEmpty()) {
                doc.addField("Personas", personas);
            }
            
            if (!orgs.isEmpty()) {
                doc.addField("Organizaciones", orgs);
            }
            
            if (!lugares.isEmpty()) {
                doc.addField("Lugares", lugares);
            }
            
            visualizaDoc(doc);
                
            //client.add(doc);
            //client.commit();
        }
    }
    
    private static void vaciaString(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = null; // Llenamos el arreglo con datos para demostración
        }
    }
    
    private static String extractNextWord(String line) {
        String[] words = line.split("</");
        String nueva = words[0];
        return nueva;
        
    }
    
    private static void visualizaString(String [] lugares)
    {
        for(int i = 0; i<lugares.length; i++)
        {
            System.out.println(lugares[i]);
        }
    }
    
    private static void visualizaDoc(SolrInputDocument solrDoc)
    {
        int i = 1;
        for (String fieldName : solrDoc.getFieldNames()) {
            
            System.out.println(fieldName + ": " + solrDoc.getFieldValues(fieldName));
            
        }
    }
    
    private static String [] creaString(String [] cadenas, int tamaño)
            
    {
        
        String [] nuevoString = new String[tamaño];
        for(int i = 0; i<tamaño; i++){
            nuevoString[i] = cadenas[i];
        }
        return nuevoString;
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
    
    public static String reemplazarContenidoEtiqueta(String input) {
        // Expresión regular para buscar cualquier etiqueta y reemplazar su contenido
        String regex = "<([A-Za-z0-9]+)>(.*?)</\\1>";
        
        // Reemplazar el contenido utilizando la expresión regular
        return input.replaceAll(regex, "$2");
    }
}
            
    
    
     
    
    
   
    
    
    

