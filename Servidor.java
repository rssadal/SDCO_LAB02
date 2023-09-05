import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;


public class Servidor {

	private static Socket socket;
	private static ServerSocket server;

	private static DataInputStream entrada;
	private static DataOutputStream saida;

	private int porta = 1025;

	public final static Path path = Paths.get("src\\fortune-br.txt");
	
	public class FileReader {

		public int countFortunes() throws FileNotFoundException {

			int lineCount = 0;

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();

				}// fim while

				//System.out.println(lineCount);
			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
			return lineCount;
		}

		
		public void parser(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			InputStream is = new BufferedInputStream(
					new FileInputStream(
							path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

				int lineCount = 0;

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();
					StringBuffer fortune = new StringBuffer();
					while (!(line == null) && !line.equals("%")) {
						fortune.append(line + "\n");
						line = br.readLine();
					}

					hm.put(lineCount, fortune.toString());
					
				}

			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
		}

		public String read(HashMap<Integer, String> hm)
				throws FileNotFoundException {
			
			Random random_object = new Random(); 
			int number = random_object.nextInt(hm.size());
			
			return hm.get(number);
		}

		public void write(HashMap<Integer, String> hm, String new_mensage)
				throws FileNotFoundException {
			
			
			try {
				hm.put((hm.size()- 1), new_mensage);
				Writer wr = new FileWriter(path.toString(), true);
		        BufferedWriter br = new BufferedWriter(wr); 
		        
		        br.write("\n%\n");
		        
		        br.write(new_mensage);
		        
		        br.write("\n%");
		        
		        br.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	public HashMap<String, String> parserRequest(String mensage) {
		mensage = mensage.substring(1, mensage.length()-1);           
		String[] Pairs = mensage.split(",,");              
		HashMap<String,String> map = new HashMap<>();               

		
		for(String pair : Pairs)                       
		{
			String pairFormat = pair.replace('"', ' ');
		    String[] entry = pairFormat.split("::");                
		    map.put(entry[0].trim(), entry[1].trim());          
		}
		return map;
	}
	
	public void iniciar() {
		FileReader file_reader = new FileReader();
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		try {
			file_reader.parser(hm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			HashMap<String, String> request = new HashMap<String, String>();
			server = new ServerSocket(porta);
			System.out.println("Servidor iniciado na porta: " + porta);
			String mensagemEntrada = "";
			String mensagemSaida = "";
			
			socket = server.accept();
			System.out.println("Conectado a " + socket.getLocalPort());
			entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());
			
			while(true) {
	            //MENU DO USUÁRIO
            	
            	mensagemEntrada = entrada.readUTF();
				request = parserRequest(mensagemEntrada);
            	System.out.println(request);
				
				String comando = request.get("method");
            	switch(comando) {
            		case "read":
            			mensagemSaida = file_reader.read(hm);
						saida.writeUTF("{"+ "\"result\"::"+ "\""+ mensagemSaida+ "\""+ "}");
            			break;
            			
            		case "write":
						String arg = request.get("args");
						arg = arg.substring(1, arg.length()-1);
						file_reader.write(hm, arg);
						saida.writeUTF("{"+ "\"result\"::"+ "\""+ arg+ "\""+ "}");
						break;
            		
            		default:
            			System.out.println("Unknown");
            			break;
	            }
            }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new Servidor().iniciar();

	}

}
