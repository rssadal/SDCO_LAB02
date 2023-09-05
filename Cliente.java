
/**
 * Laboratorio 1 de Sistemas Distribuidos
 * 
 * Autor: Lucio A. Rocha
 * Ultima atualizacao: 17/12/2022
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class Cliente {
    
    private static Socket socket;
    private static DataInputStream entrada;
    private static DataOutputStream saida;
    
    private int porta=1025;
    
    public HashMap<String, String> parser(String mensage) {
    	HashMap<String,String> Hmap = new HashMap<>();  
    	mensage = mensage.substring(1, mensage.length()-1);         
    	
		String[] Pairs = mensage.split(",,");              
		             

		for(String pair : Pairs)  // Format the mensage                       
		{
			String pairFormat = pair.replace('"', ' ');
		    String[] entry = pairFormat.split("::");                 
		    Hmap.put(entry[0].trim(), entry[1].trim());          
		}
		return Hmap;
	}
    
    
    public void iniciar(){
    	System.out.println("Cliente iniciado na porta: " + porta);
    	
    	try {
            
            socket = new Socket("127.0.0.1", porta);
            
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            HashMap<String,String> hash_map_out = new HashMap<>();
            
            while(true) {
	            //MENU DO USUÁRIO
            	System.out.println("Menu \n R to read - W to write - O to get out \n");
            	String comando = "";
            	comando = br.readLine();
            	switch(comando) {
            		case "R":
            			
            			System.out.println("Read");
            			saida.writeUTF("{" + "\"method\"::\"read\",," + "\"args\"::[\"\"]" + "}");
            			
            			hash_map_out = parser(entrada.readUTF());
            			System.out.println("Read:\n" + "{\nresult:" + hash_map_out.get("result") + "\n}");
            			break;
            			
            		case "W":
            			
            			System.out.println("[CLIENT](write)");
            			String going_mensage = "";
            			going_mensage = br.readLine();
            			saida.writeUTF("{" + "\"method\"::\"write\",," + "\"args\"::[\"" + going_mensage + "\n\"]" + "}");
            			hash_map_out = parser(entrada.readUTF());
            			if(hash_map_out.get("result").equals("true")) {
            				System.out.println("Write :\n" + "{\nresult:" + hash_map_out.get("result") + "\n}");
            			}else {
            				System.out.println("Error : Not accept");
            			}
            			break;
            			
            		case "O":
            			
            			System.out.println("Going Out");
            			socket.close();
            			return;
            			
            		default:
            			
            			System.out.println("Unknown");
            			break;
	            }
            }
            
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
    
}
